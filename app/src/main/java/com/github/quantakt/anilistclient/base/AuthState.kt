package com.github.quantakt.anilistclient.base

import com.github.quantakt.anilistclient.domain.entities.LoggedInUser

sealed class AuthState {
    data class LoggedIn(val user: LoggedInUser): AuthState()
    object NotLoggedIn : AuthState()
}