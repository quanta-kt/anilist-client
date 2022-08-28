package com.github.quantakt.anilistclient.data.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.github.quantakt.anilistclient.data.db.dbentities.*
import com.github.quantakt.anilistclient.domain.entities.AnilistMediaListQuery
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MediaListDao {

    companion object {
        // NOTE: These constants must be in sync with MediaListSort ordinals
        const val SORT_BY_MEDIA_ID = 0
        const val SORT_BY_MEDIA_ID_DESC = 1
        const val SORT_BY_SCORE = 2
        const val SORT_BY_SCORE_DESC = 3
        const val SORT_BY_STATUS = 4
        const val SORT_BY_STATUS_DESC = 5
        const val SORT_BY_PROGRESS = 6
        const val SORT_BY_PROGRESS_DESC = 7
        const val SORT_BY_PROGRESS_VOLUMES = 8
        const val SORT_BY_PROGRESS_VOLUMES_DESC = 9
    }

    @Query("SELECT * FROM MediaListItem " +
            "WHERE (:userId IS NULL OR userId = :userId)" +
            "AND (:type IS NULL OR type = :type)" +
            "AND (status IN (:status))" +
            "AND (:customList IS NULL OR :customList IN (" +
            "  SELECT MediaCustomLists.name FROM MediaCustomLists" +
            "  WHERE MediaCustomLists.mediaId = MediaListItem.mediaId" +
            "))" +
            "ORDER BY " +
            "  CASE WHEN :sort IS NULL THEN id END ASC," +
            "  CASE WHEN :sort = $SORT_BY_MEDIA_ID THEN mediaId END ASC," +
            "  CASE WHEN :sort = $SORT_BY_MEDIA_ID_DESC THEN mediaId END DESC," +
            "  CASE WHEN :sort = $SORT_BY_PROGRESS THEN progress END ASC," +
            "  CASE WHEN :sort = $SORT_BY_PROGRESS_DESC THEN progress END DESC," +
            "  CASE WHEN :sort = $SORT_BY_PROGRESS_VOLUMES THEN progressVolume END ASC," +
            "  CASE WHEN :sort = $SORT_BY_PROGRESS_VOLUMES_DESC THEN progressVolume END DESC," +
            "  CASE WHEN :sort = $SORT_BY_SCORE THEN score END ASC," +
            "  CASE WHEN :sort = $SORT_BY_SCORE_DESC THEN score END DESC," +
            "  CASE WHEN :sort = $SORT_BY_STATUS THEN status END ASC," +
            "  CASE WHEN :sort = $SORT_BY_STATUS_DESC THEN status END DESC")
    abstract fun pagingSource(
        userId: Int?,
        type: MediaType?,
        status: Set<MediaListStatus>,
        sort: Int?,
        customList: String?,
    ): PagingSource<Int, MediaListItemWithCustomLists>

    fun pagingSource(query: AnilistMediaListQuery) =
        pagingSource(
            userId = query.userId,
            type = query.mediaType,
            status = query.status ?: MediaListStatus.values().toSet(),
            sort = query.sort?.ordinal,
            customList = query.customList,
        )

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateMediaListItem(item: MediaListItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMediaListItems(items: List<MediaListItem>)

    @Query("DELETE FROM MediaListItem " +
            "WHERE (:userId IS NULL OR userId = :userId) " +
            "AND (:type IS NULL or type = :type) " +
            "AND status IN (:status)")
    abstract suspend fun deleteMediaListItemsByQuery(userId: Int?, type: MediaType?, status: Set<MediaListStatus>)

    suspend fun deleteMediaListItemsByQuery(query: AnilistMediaListQuery) =
        deleteMediaListItemsByQuery(
            userId = query.userId,
            type = query.mediaType,
            status = query.status ?: MediaListStatus.values().toSet()
        )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUserCustomLists(userCustomList: List<UserCustomList>)

    @Query("DELETE FROM UserCustomList " +
            "WHERE (userId = :userId)" +
            "AND (:mediaType IS NULL OR mediaType = :mediaType)")
    abstract suspend fun deleteUserCustomLists(userId: Int, mediaType: MediaType?)

    @Query("SELECT * FROM UserCustomList " +
            "WHERE (userId = :userId)" +
            "AND (:mediaType IS NULL OR mediaType = :mediaType)")
    abstract fun getUserCustomLists(userId: Int, mediaType: MediaType? = null): Flow<List<UserCustomList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMediaCustomLists(mediaCustomList: List<MediaCustomLists>)

    @Query("DELETE FROM MediaCustomLists " +
            "WHERE (userId = :userId)" +
            "AND (:mediaType IS NULL OR mediaType = :mediaType)")
    abstract suspend fun deleteCustomLFor(userId: Int, mediaType: MediaType? = null)
}