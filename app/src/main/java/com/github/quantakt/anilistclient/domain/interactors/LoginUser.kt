package com.github.quantakt.anilistclient.domain.interactors

import com.github.quantakt.anilistclient.domain.entities.LoggedInUser
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import javax.inject.Inject

class LoginUser @Inject constructor(
    private val loginRepository: LoginRepository
) : ResultInteractor<LoginUser.Params, LoggedInUser>() {

    override suspend fun doWork(params: Params): LoggedInUser {
        return loginRepository.loginUser(params.token)
    }

    data class Params(val token: String)
}