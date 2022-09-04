package com.github.quantakt.anilistclient.data.mappers

import com.github.quantakt.anilistclient.domain.entities.MediaDetails
import com.github.quantakt.apollo.MediaDetailsQuery

fun MediaDetailsQuery.Media.toEntity() = MediaDetails(
    id = id,
    title = title?.userPreferred ?: error("title == null"),
    coverImageLarge = coverImage?.large ?: error("coverImageLarge == null"),
    coverImageExtraLarge = coverImage.extraLarge ?: error("coverImageExtraLarge == null"),
)