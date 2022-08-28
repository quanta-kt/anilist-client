package com.github.quantakt.anilistclient.data.repositories

import androidx.paging.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.github.quantakt.anilistclient.data.anilistAuthOptional
import com.github.quantakt.anilistclient.data.db.AppDatabase
import com.github.quantakt.anilistclient.data.db.dbentities.UserCustomList
import com.github.quantakt.anilistclient.data.mappers.toDbEntity
import com.github.quantakt.anilistclient.data.mappers.toEntity
import com.github.quantakt.anilistclient.data.mappers.toGraphQlType
import com.github.quantakt.anilistclient.data.paging.MediaListRemoteMediator
import com.github.quantakt.anilistclient.data.toOptional
import com.github.quantakt.anilistclient.domain.entities.AnilistMediaListQuery
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.domain.entities.PagedResult
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import com.github.quantakt.anilistclient.domain.repositories.MediaListRepository
import com.github.quantakt.apollo.MediaListQuery
import com.github.quantakt.apollo.UpdateMediaListProgressMutation
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private typealias MediaListItemDb = com.github.quantakt.anilistclient.data.db.dbentities.MediaListItem

@Singleton
class ListRepositoryImpl @Inject constructor(
    private val loginRepository: LoginRepository,
    private val apolloClient: ApolloClient,
    private val database: AppDatabase,
) : MediaListRepository {

    private val mediaListDao = database.mediaListDao()
    
    override suspend fun getMediaListPage(
        query: AnilistMediaListQuery,
        page: Int,
        perPage: Int?,
    ): PagedResult<MediaListQuery.MediaList> {

        val requestQuery = with(query) {
            MediaListQuery(
                userId = userId,
                type = mediaType?.toGraphQlType().toOptional(),
                status_in = status?.map { it.toGraphQlType() }.toOptional(),
                sort = sort?.toGraphQlType()?.let { listOf(it) }.toOptional(),
                perPage = perPage.toOptional(),
                page = page.toOptional(),
            )
        }

        val result = apolloClient
            .query(requestQuery)
            .anilistAuthOptional(loginRepository.authTokenOrNull())
            .execute()

        val pageResult = result.data?.Page ?: error("result.data == null")
        val pageInfo = pageResult.pageInfo ?: error("result.data.Page.pageInfo == null")

        return PagedResult(
            items = pageResult.mediaList?.filterNotNull()
                ?: error("result.data.Page.mediaList == null"),
            lastPage = pageInfo.lastPage ?: error("pageInfo.lastPage == null"),
            currentPage = pageInfo.currentPage ?: error("pageInfo.currentPage == null"),
            hasNextPage = pageInfo.hasNextPage ?: error("pageInfo.hasNextPage == null"),
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getMediaListPagingFlow(query: AnilistMediaListQuery): Flow<PagingData<MediaListItem>> = Pager(
        config = PagingConfig(pageSize = 50),
        remoteMediator = MediaListRemoteMediator(
            repository = this,
            query = query,
            database = database
        )
    ) {
        
        mediaListDao.pagingSource(query)
        
    }.flow.map { pagingData ->
        pagingData.map { it.toEntity() }
    }

    override fun getUserCustomLists(userId: Int, mediaType: MediaType?): Flow<List<UserCustomList>> {
        return mediaListDao.getUserCustomLists(userId, mediaType)
    }

    override suspend fun updateMediaListItemProgress(
        mediaListItem: MediaListItem,
        progress: Int?,
        progressVolumes: Int?,
    ): Result<Unit> = coroutineScope {

        val mediaListItemDb = mediaListItem.toDbEntity()

        val optimisticUpdate = async {
            // Optimistically update the entry in DB instead of waiting for the graphql call to finish
            val optimisticallyUpdated = mediaListItemDb.copy(
                progress = progress ?: mediaListItemDb.progress,
                progressVolume = progressVolumes ?: mediaListItemDb.progressVolume
            )
            mediaListDao.insertMediaListItems(listOf(optimisticallyUpdated))
        }

        val mutation = UpdateMediaListProgressMutation(
            mediaListItem.id,
            progress.toOptional(),
            progressVolumes.toOptional()
        )

        val result = try {
            apolloClient.mutation(mutation)
                .anilistAuthOptional(loginRepository.authTokenOrNull())
                .execute()
        } catch (e: Exception) {
            e.printStackTrace()
            optimisticUpdate.await()
            mediaListDao.insertMediaListItems(listOf(mediaListItemDb))
            return@coroutineScope Result.failure(e)
        }

        val data = result.data?.SaveMediaListEntry

        // Update DB with latest data available
        if (data != null) {
            val updated = mediaListItemDb.copy(
                progress = data.progress ?: mediaListItemDb.progress,
                progressVolume = data.progressVolumes ?: mediaListItemDb.progressVolume
            )

            optimisticUpdate.await()
            mediaListDao.insertMediaListItems(listOf(updated))
        }

        return@coroutineScope Result.success(Unit)
    }
}