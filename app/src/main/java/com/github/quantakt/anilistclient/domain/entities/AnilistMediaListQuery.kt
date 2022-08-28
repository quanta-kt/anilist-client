package com.github.quantakt.anilistclient.domain.entities

enum class MediaType {
    ANIME, MANGA
}

enum class MediaListStatus {
    CURRENT,
    PLANNING,
    COMPLETED,
    DROPPED,
    PAUSED,
    REPEATING
}

// Note: Only a subset of sort options available on AniList are supported
enum class MediaListSort {
    MEDIA_ID,
    MEDIA_ID_DESC,
    SCORE,
    SCORE_DESC,
    STATUS,
    STATUS_DESC,
    PROGRESS,
    PROGRESS_DESC,
    PROGRESS_VOLUMES,
    PROGRESS_VOLUMES_DESC,
}


/**
 * Represents a user submitted query for media list items
 *
 * @param userId the user id to filter by
 * @param mediaType media type to filter by (i.e. anime or manga)
 * @param status status of the media to filter by. an item is included in the result if it's status
 *        is one among the values in the set
 * @param customList filter by presence in a custom list
 * @param sort sort strategy
 */
data class AnilistMediaListQuery(
    val userId: Int,
    val mediaType: MediaType? = null,
    val status: Set<MediaListStatus>? = null,
    val customList: String? = null,
    val sort: MediaListSort? = null,
)