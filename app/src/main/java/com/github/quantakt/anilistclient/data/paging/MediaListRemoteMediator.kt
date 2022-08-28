package com.github.quantakt.anilistclient.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.github.quantakt.anilistclient.data.db.AppDatabase
import com.github.quantakt.anilistclient.data.db.dbentities.*
import com.github.quantakt.anilistclient.data.mappers.toDbEntity
import com.github.quantakt.anilistclient.data.mappers.toEntity
import com.github.quantakt.anilistclient.domain.entities.AnilistMediaListQuery
import com.github.quantakt.anilistclient.domain.repositories.MediaListRepository

@OptIn(ExperimentalPagingApi::class)
class MediaListRemoteMediator(
    private val repository: MediaListRepository,
    private val query: AnilistMediaListQuery,
    private val database: AppDatabase,
) : RemoteMediator<Int, MediaListItemWithCustomLists>() {

    private val mediaListDao = database.mediaListDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MediaListItemWithCustomLists>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1

            LoadType.PREPEND -> return MediatorResult.Success(
                endOfPaginationReached = true
            )

            LoadType.APPEND -> {
                remoteKeysDao.getByLabel(query.toString())?.nextKey
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
            }
        }

        val result = try {
            repository.getMediaListPage(
                query = query,
                page = page,
                perPage = state.config.pageSize
            )
        } catch (e: ApolloNetworkException) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        } catch (e: ApolloHttpException) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }

        val nextPage = if (result.hasNextPage) page + 1 else null

        database.withTransaction {
            val entities = result.items.asSequence().map { it.toDbEntity() }

            remoteKeysDao.insert(RemoteMediatorRemoteKey(query.toString(), nextPage))

            if (loadType == LoadType.REFRESH) {
                // Delete existing data for this query on refresh
                mediaListDao.deleteMediaListItemsByQuery(query)
                mediaListDao.deleteUserCustomLists(query.userId, query.mediaType)
                mediaListDao.deleteCustomLFor(query.userId, query.mediaType)

                // Find all custom lists and save to db
                val userCustomLists = result.items
                    .asSequence()
                    .distinctBy { it.media?.type }
                    .flatMap { name ->
                        val mediaType = name.media?.type?.toEntity() ?: return@flatMap emptySequence()
                        val customList = name.customLists as? Map<*, *> ?: emptyMap<String, Boolean>()

                        customList.keys
                            .asSequence()
                            .filterIsInstance<String>()
                            .map {
                                UserCustomList(
                                    name = it,
                                    userId = query.userId,
                                    mediaType = mediaType
                                )
                            }
                    }
                    .toList()

                mediaListDao.insertUserCustomLists(userCustomLists)
            }

            // Insert all media items and custom list associations
            val mediaListItems = entities.map { it.mediaListItem }
            val mediaCustomLists = entities.flatMap { it.customLists }

            mediaListDao.insertMediaListItems(mediaListItems.toList())
            mediaListDao.insertMediaCustomLists(mediaCustomLists.toList())
        }

        return MediatorResult.Success(
            endOfPaginationReached = !result.hasNextPage
        )
    }
}