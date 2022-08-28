package com.github.quantakt.anilistclient.data.repositories

import com.apollographql.apollo3.ApolloClient
import com.github.quantakt.anilistclient.data.anilistAuth
import com.github.quantakt.anilistclient.data.db.AppDatabase
import com.github.quantakt.anilistclient.data.db.dbentities.LoggedInUserEntity
import com.github.quantakt.anilistclient.data.exceptions.AnilistAuthException
import com.github.quantakt.anilistclient.data.pref.PreferenceManager
import com.github.quantakt.anilistclient.base.AuthState
import com.github.quantakt.anilistclient.domain.entities.LoggedInUser
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import com.github.quantakt.apollo.ViewerIdQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val apolloClient: ApolloClient,
    database: AppDatabase,
) : LoginRepository {

    private val loggedInUserDao = database.loggedInUserDao()

    override val authStateFlow = preferenceManager.defaultUserIdFlow
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                loggedInUserDao.getById(id)
            }
        }.mapLatest { user ->
            if (user == null)
                AuthState.NotLoggedIn
            else {
                AuthState.LoggedIn(user)
            }
        }.distinctUntilChanged()

    override suspend fun authTokenOrNull(): String? {
        return when (val state = authStateFlow.first()) {
            is AuthState.NotLoggedIn -> null
            is AuthState.LoggedIn -> state.user.token
        }
    }

    override suspend fun setDefaultLoggedInUser(userId: Int) {
        preferenceManager.setDefaultLoggedInUserId(userId)
    }

    override suspend fun loginUser(token: String): LoggedInUser {
        val query = ViewerIdQuery()
        val result = apolloClient
            .query(query)
            .anilistAuth(token)
            .execute()

        val userId = result.data?.Viewer?.id ?: throw AnilistAuthException()
        val user = LoggedInUserEntity(id = userId, token = token)
        loggedInUserDao.insert(user)

        return user
    }
}