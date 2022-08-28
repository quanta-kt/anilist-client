package com.github.quantakt.anilistclient.domain.entities

/**
 * An AniList media item (an anime or manga) in an user's list
 */
data class MediaListItem(
    val id: Int,
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
    val customLists: Map<String, Boolean>,
)
