package com.github.quantakt.anilistclient.domain.interactors

import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import javax.inject.Inject

class SetDefaultLoggedInUser @Inject constructor(
    private val loginRepository: LoginRepository
) : Interactor<SetDefaultLoggedInUser.Params>() {

    data class Params(val userId: Int)

    override suspend fun doWork(params: Params) {
        loginRepository.setDefaultLoggedInUser(params.userId)
    }
}