package com.github.quantakt.anilistclient.domain.observers

import com.github.quantakt.anilistclient.domain.entities.AuthState
import com.github.quantakt.anilistclient.domain.interactors.ObservableInteractor
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthState @Inject constructor(
    private val loginRepository: LoginRepository
) : ObservableInteractor<Unit, AuthState>() {

    override fun createObservable(params: Unit): Flow<AuthState> {
        return loginRepository.authStateFlow
    }
}