package com.github.quantakt.anilistclient.data.mappers

import com.github.quantakt.apollo.type.MediaListSort

fun MediaListSort.toEntity() = when (this) {
    MediaListSort.MEDIA_ID -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.MEDIA_ID
    MediaListSort.MEDIA_ID_DESC -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.MEDIA_ID_DESC
    MediaListSort.SCORE -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.SCORE
    MediaListSort.SCORE_DESC -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.SCORE_DESC
    MediaListSort.STATUS -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.STATUS
    MediaListSort.STATUS_DESC -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.STATUS_DESC
    MediaListSort.PROGRESS -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS
    MediaListSort.PROGRESS_DESC -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS_DESC
    MediaListSort.PROGRESS_VOLUMES -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS_VOLUMES
    MediaListSort.PROGRESS_VOLUMES_DESC -> com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS_VOLUMES_DESC
    else -> null
}

fun com.github.quantakt.anilistclient.domain.entities.MediaListSort.toGraphQlType() = when (this) {
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.MEDIA_ID -> MediaListSort.MEDIA_ID
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.MEDIA_ID_DESC -> MediaListSort.MEDIA_ID_DESC
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.SCORE -> MediaListSort.SCORE
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.SCORE_DESC -> MediaListSort.SCORE_DESC
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.STATUS -> MediaListSort.STATUS
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.STATUS_DESC -> MediaListSort.STATUS_DESC
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS -> MediaListSort.PROGRESS
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS_DESC -> MediaListSort.PROGRESS_DESC
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS_VOLUMES -> MediaListSort.PROGRESS_VOLUMES
    com.github.quantakt.anilistclient.domain.entities.MediaListSort.PROGRESS_VOLUMES_DESC -> MediaListSort.PROGRESS_VOLUMES_DESC
}