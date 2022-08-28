package com.github.quantakt.anilistclient.data.mappers

import com.github.quantakt.apollo.type.MediaType

fun MediaType.toEntity() = when (this) {
    MediaType.ANIME -> com.github.quantakt.anilistclient.domain.entities.MediaType.ANIME
    MediaType.MANGA -> com.github.quantakt.anilistclient.domain.entities.MediaType.MANGA
    else -> null
}

fun com.github.quantakt.anilistclient.domain.entities.MediaType.toGraphQlType() = when (this) {
    com.github.quantakt.anilistclient.domain.entities.MediaType.ANIME -> MediaType.ANIME
    com.github.quantakt.anilistclient.domain.entities.MediaType.MANGA -> MediaType.MANGA
}