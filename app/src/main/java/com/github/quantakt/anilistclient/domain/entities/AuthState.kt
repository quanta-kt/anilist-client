package com.github.quantakt.anilistclient.domain.entities

sealed class AuthState {
    data class LoggedIn(val user: LoggedInUser): AuthState()
    object NotLoggedIn : AuthState()
}