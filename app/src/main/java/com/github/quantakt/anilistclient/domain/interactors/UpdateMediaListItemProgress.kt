package com.github.quantakt.anilistclient.domain.interactors

import com.github.quantakt.anilistclient.domain.ResultInteractor
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.anilistclient.domain.repositories.MediaListRepository
import javax.inject.Inject

class UpdateMediaListItemProgress @Inject constructor(
    private val mediaListRepository: MediaListRepository
) : ResultInteractor<UpdateMediaListItemProgress.Params, Result<Unit>>() {

    data class Params(
        val mediaListItem: MediaListItem,
        val progress: Int? = null,
        val progressVolume: Int? = null,
    )

    override suspend fun doWork(params: Params): Result<Unit> {
        return mediaListRepository.updateMediaListItemProgress(
            params.mediaListItem,
            params.progress,
            params.progressVolume
        )
    }
}