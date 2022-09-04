package com.github.quantakt.anilistclient.domain.observers

import com.github.quantakt.anilistclient.domain.entities.MediaDetails
import com.github.quantakt.anilistclient.domain.interactors.ResultInteractor
import com.github.quantakt.anilistclient.domain.repositories.MediaRepository
import javax.inject.Inject

class GetMediaDetails @Inject constructor(
    private val mediaRepository: MediaRepository,
): ResultInteractor<GetMediaDetails.Params, MediaDetails>() {

    data class Params(
        val mediaId: Int,
    )

    override suspend fun doWork(params: Params): MediaDetails {
        return mediaRepository.getMediaDetails(params.mediaId)
    }
}