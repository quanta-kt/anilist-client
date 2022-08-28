package com.github.quantakt.anilistclient.data.mappers

import com.github.quantakt.apollo.type.MediaListStatus

fun MediaListStatus.toEntity() = when (this) {
    MediaListStatus.CURRENT -> com.github.quantakt.anilistclient.domain.entities.MediaListStatus.CURRENT
    MediaListStatus.PLANNING -> com.github.quantakt.anilistclient.domain.entities.MediaListStatus.PLANNING
    MediaListStatus.COMPLETED -> com.github.quantakt.anilistclient.domain.entities.MediaListStatus.COMPLETED
    MediaListStatus.DROPPED -> com.github.quantakt.anilistclient.domain.entities.MediaListStatus.DROPPED
    MediaListStatus.PAUSED -> com.github.quantakt.anilistclient.domain.entities.MediaListStatus.PAUSED
    MediaListStatus.REPEATING -> com.github.quantakt.anilistclient.domain.entities.MediaListStatus.REPEATING
    MediaListStatus.UNKNOWN__ -> null

}

fun com.github.quantakt.anilistclient.domain.entities.MediaListStatus.toGraphQlType() = when (this) {
    com.github.quantakt.anilistclient.domain.entities.MediaListStatus.CURRENT -> MediaListStatus.CURRENT
    com.github.quantakt.anilistclient.domain.entities.MediaListStatus.PLANNING -> MediaListStatus.PLANNING
    com.github.quantakt.anilistclient.domain.entities.MediaListStatus.COMPLETED -> MediaListStatus.COMPLETED
    com.github.quantakt.anilistclient.domain.entities.MediaListStatus.DROPPED -> MediaListStatus.DROPPED
    com.github.quantakt.anilistclient.domain.entities.MediaListStatus.PAUSED -> MediaListStatus.PAUSED
    com.github.quantakt.anilistclient.domain.entities.MediaListStatus.REPEATING -> MediaListStatus.REPEATING
}