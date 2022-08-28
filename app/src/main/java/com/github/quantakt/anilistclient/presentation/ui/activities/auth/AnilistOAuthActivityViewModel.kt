package com.github.quantakt.anilistclient.presentation.ui.activities.auth

import androidx.lifecycle.ViewModel
import com.github.quantakt.anilistclient.domain.interactors.LoginUser
import com.github.quantakt.anilistclient.domain.interactors.SetDefaultLoggedInUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnilistOAuthActivityViewModel @Inject constructor(
    private val loginUser: LoginUser,
    private val setDefaultLoggedInUser: SetDefaultLoggedInUser
) : ViewModel() {

    suspend fun login(token: String) {
        val user = loginUser.executeSync(LoginUser.Params(token))
        setDefaultLoggedInUser.executeSync(SetDefaultLoggedInUser.Params(user.id))
    }
}