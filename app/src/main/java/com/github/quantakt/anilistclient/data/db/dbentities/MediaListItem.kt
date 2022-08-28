package com.github.quantakt.anilistclient.data.db.dbentities

import androidx.room.*
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType

@Entity
data class MediaListItem(
    @PrimaryKey val id: Int,
    val userId: Int?,
    val mediaId: Int?,
    val type: MediaType?,
    val totalEpisodes: Int?,
    val totalChapters: Int?,
    val totalVolumes: Int?,
    val coverImageUrl: String?,
    val progress: Int?,
    val progressVolume: Int?,
    val score: Double?,
    val title: String?,
    val status: MediaListStatus?,
)

data class MediaListItemWithCustomLists(
    @Embedded val mediaListItem: MediaListItem,

    @Relation(
        parentColumn = "id",
        entityColumn = "mediaId"
    )
    val customLists: Set<MediaCustomLists>
)

/**
 * A table providing a mapping of media to a custom list
 */
@Entity(primaryKeys = [
    "name",
    "mediaId",
    "userId",
    "mediaType",
])
data class MediaCustomLists(
    val name: String,
    val mediaId: Int,
    val userId: Int,
    val mediaType: MediaType,
)

/**
 * A table of a available custom lists per user and media type
 */
@Entity(primaryKeys = ["name", "userId", "mediaType"])
data class UserCustomList(
    val name: String,
    val userId: Int,
    val mediaType: MediaType,
)