package com.github.quantakt.anilistclient.data.di

import com.github.quantakt.anilistclient.data.repositories.ListRepositoryImpl
import com.github.quantakt.anilistclient.domain.repositories.MediaListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ListModule {

    @Binds
    abstract fun bindListRepository(listRepositoryImpl: ListRepositoryImpl): MediaListRepository
}