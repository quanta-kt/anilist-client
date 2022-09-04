package com.github.quantakt.anilistclient.data.di

import com.github.quantakt.anilistclient.data.repositories.MediaRepositoryImpl
import com.github.quantakt.anilistclient.domain.repositories.MediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MediaModule {

    @Binds
    fun bindsMediaRepository(mediaRepositoryImpl: MediaRepositoryImpl): MediaRepository
}