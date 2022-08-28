package com.github.quantakt.anilistclient.data.mappers

import com.github.quantakt.anilistclient.data.db.dbentities.MediaCustomLists
import com.github.quantakt.anilistclient.data.db.dbentities.MediaListItemWithCustomLists
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.apollo.MediaListQuery

fun MediaListQuery.MediaList.toEntity() = MediaListItem(
    id = id,
    userId = userId,
    mediaId = media?.id,
    type = media?.type?.toEntity(),
    totalEpisodes = media?.episodes,
    totalChapters = media?.chapters,
    totalVolumes = media?.volumes,
    coverImageUrl = media?.coverImage?.large,
    progress = progress,
    progressVolume = progressVolumes,
    score = score,
    title = media?.title?.userPreferred,
    status = status?.toEntity(),
    customLists = customLists.customListsMapper(),
)

private fun Any?.customListsMapper(): Map<String, Boolean> {
    val map = this as? Map<*, *> ?: return emptyMap()

    return map.keys
        .asSequence()
        .filterIsInstance<String>()
        .associateWith { map[it] as? Boolean ?: false }
}

fun MediaListQuery.MediaList.toDbEntity(): MediaListItemWithCustomLists {
    val type = media?.type?.toEntity() ?: error("Tried to map MediaList without media information")

    return MediaListItemWithCustomLists(
        com.github.quantakt.anilistclient.data.db.dbentities.MediaListItem(
            id = id,
            userId = userId,
            mediaId = media.id,
            type = type,
            totalEpisodes = media.episodes,
            totalChapters = media.chapters,
            totalVolumes = media.volumes,
            coverImageUrl = media.coverImage?.large,
            progress = progress,
            progressVolume = progressVolumes,
            score = score,
            title = media.title?.userPreferred,
            status = status?.toEntity(),
        ),
        customLists = customLists
            .customListsMapper()
            .asSequence()
            .filter { it.value }
            .map { MediaCustomLists(it.key, media.id, userId, type) }
            .toSet()
    )
}

fun MediaListItemWithCustomLists.toEntity() = with(mediaListItem) {
    MediaListItem(
        id = id,
        userId = userId,
        mediaId = mediaId,
        type = type,
        totalEpisodes = totalEpisodes,
        totalChapters = totalChapters,
        totalVolumes = totalVolumes,
        coverImageUrl = coverImageUrl,
        progress = progress,
        progressVolume = progressVolume,
        score = score,
        title = title,
        status = status,
        customLists = customLists.customListsMapper()
    )
}

fun MediaListItem.toDbEntity() = com.github.quantakt.anilistclient.data.db.dbentities.MediaListItem(
    id = id,
    userId = userId,
    mediaId = mediaId,
    type = type,
    totalEpisodes = totalEpisodes,
    totalChapters = totalChapters,
    totalVolumes = totalVolumes,
    coverImageUrl = coverImageUrl,
    progress = progress,
    progressVolume = progressVolume,
    score = score,
    title = title,
    status = status
)