package com.github.quantakt.anilistclient.data.repositories

import com.apollographql.apollo3.ApolloClient
import com.github.quantakt.anilistclient.data.anilistAuthOptional
import com.github.quantakt.anilistclient.data.mappers.toEntity
import com.github.quantakt.anilistclient.domain.entities.MediaDetails
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import com.github.quantakt.anilistclient.domain.repositories.MediaRepository
import com.github.quantakt.apollo.MediaDetailsQuery
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val loginRepository: LoginRepository,
    private val apolloClient: ApolloClient,
) : MediaRepository {

    override suspend fun getMediaDetails(mediaId: Int) : MediaDetails {
        val result = apolloClient
            .query(MediaDetailsQuery(mediaId))
            .anilistAuthOptional(loginRepository.authTokenOrNull())
            .execute()

        val media = result.data?.Media ?: error("result.data?.Media == null")
        return media.toEntity()
    }
}