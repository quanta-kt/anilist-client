package com.github.quantakt.anilistclient.domain.observers

import com.github.quantakt.anilistclient.base.AuthState
import com.github.quantakt.anilistclient.data.db.dbentities.UserCustomList
import com.github.quantakt.anilistclient.domain.ObservableInteractor
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import com.github.quantakt.anilistclient.domain.repositories.MediaListRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ObserveViewerCustomLists @Inject constructor(
    private val mediaListRepository: MediaListRepository,
    private val loginRepository: LoginRepository,
) : ObservableInteractor<ObserveViewerCustomLists.Params, List<UserCustomList>>() {

    data class Params(
        val mediaType: MediaType? = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createObservable(params: Params): Flow<List<UserCustomList>> {
        return loginRepository.authStateFlow.flatMapLatest { authState ->
            when (authState) {
                is AuthState.NotLoggedIn -> {
                    emptyFlow()
                }
                is AuthState.LoggedIn -> {
                    mediaListRepository.getUserCustomLists(
                        userId = authState.user.id,
                        mediaType = params.mediaType
                    )
                }
            }
        }
    }
}