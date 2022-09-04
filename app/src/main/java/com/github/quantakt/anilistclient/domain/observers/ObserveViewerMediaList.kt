package com.github.quantakt.anilistclient.domain.observers

import androidx.paging.PagingData
import com.github.quantakt.anilistclient.domain.entities.AuthState
import com.github.quantakt.anilistclient.domain.entities.MediaListItem
import com.github.quantakt.anilistclient.domain.interactors.ObservableInteractor
import com.github.quantakt.anilistclient.domain.entities.AnilistMediaListQuery
import com.github.quantakt.anilistclient.domain.entities.MediaListSort
import com.github.quantakt.anilistclient.domain.entities.MediaListStatus
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import com.github.quantakt.anilistclient.domain.repositories.MediaListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveViewerMediaList @Inject constructor(
    private val listRepository: MediaListRepository,
    private val loginRepository: LoginRepository,
) : ObservableInteractor<ObserveViewerMediaList.Params, PagingData<MediaListItem>>() {

    data class Params(
        val type: MediaType? = null,
        val status: Set<MediaListStatus>? = null,
        val sort: MediaListSort? = null,
        val customList: String? = null
    )

    override fun createObservable(params: Params): Flow<PagingData<MediaListItem>> =
        loginRepository.authStateFlow.flatMapLatest { authState ->
            when (authState) {
                is AuthState.NotLoggedIn -> {
                    flowOf(PagingData.empty())
                }
                is AuthState.LoggedIn -> {
                    listRepository.getMediaListPagingFlow(
                        AnilistMediaListQuery(
                            userId = authState.user.id,
                            mediaType = params.type,
                            sort = params.sort,
                            status = params.status,
                            customList = params.customList,
                        )
                    )
                }
            }
        }
}