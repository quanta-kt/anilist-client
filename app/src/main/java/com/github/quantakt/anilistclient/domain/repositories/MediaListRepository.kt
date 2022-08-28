package com.github.quantakt.anilistclient.domain.repositories

import androidx.paging.PagingData
import com.github.quantakt.anilistclient.data.db.dbentities.UserCustomList
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.anilistclient.domain.entities.AnilistMediaListQuery
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.domain.entities.PagedResult
import com.github.quantakt.apollo.MediaListQuery
import kotlinx.coroutines.flow.Flow

interface MediaListRepository {
    suspend fun getMediaListPage(
        query: AnilistMediaListQuery,
        page: Int,
        perPage: Int?,
    ): PagedResult<MediaListQuery.MediaList>

    suspend fun getMediaListPagingFlow(
        query: AnilistMediaListQuery
    ): Flow<PagingData<MediaListItem>>

    fun getUserCustomLists(
        userId: Int,
        mediaType: MediaType? = null
    ): Flow<List<UserCustomList>>

    suspend fun updateMediaListItemProgress(
        mediaListItem: MediaListItem,
        progress: Int?,
        progressVolumes: Int?,
    ): Result<Unit>
}