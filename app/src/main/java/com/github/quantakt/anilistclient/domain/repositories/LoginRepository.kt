package com.github.quantakt.anilistclient.domain.repositories

import com.github.quantakt.anilistclient.domain.entities.AuthState
import com.github.quantakt.anilistclient.domain.entities.LoggedInUser
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    val authStateFlow: Flow<AuthState>

    suspend fun authTokenOrNull(): String?

    suspend fun setDefaultLoggedInUser(userId: Int)

    suspend fun loginUser(token: String): LoggedInUser
}