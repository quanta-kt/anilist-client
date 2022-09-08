package com.github.quantakt.anilistclient.domain.repositories

import com.github.quantakt.anilistclient.domain.entities.MediaDetails

interface MediaRepository {
    suspend fun getMediaDetails(mediaId: Int) : MediaDetails
}