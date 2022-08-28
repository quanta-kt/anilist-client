package com.github.quantakt.anilistclient.data.di

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GraphQlModule {

    private const val ANILIST_SERVER_URL = "https://graphql.anilist.co"
    private const val LOGGING_INTERCEPTOR_TAG = "LoggingInterceptor"

    private fun logToLogCat(msg: String) = Log.d(LOGGING_INTERCEPTOR_TAG, msg)

    @Provides
    fun provideApolloClient(): ApolloClient = ApolloClient.Builder()
        .serverUrl(ANILIST_SERVER_URL)
        .addHttpInterceptor(LoggingInterceptor(
            LoggingInterceptor.Level.BODY,
            ::logToLogCat
        ))
        .build()
}