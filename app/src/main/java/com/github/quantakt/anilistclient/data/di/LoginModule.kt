package com.github.quantakt.anilistclient.data.di

import com.github.quantakt.anilistclient.data.repositories.LoginRepositoryImpl
import com.github.quantakt.anilistclient.domain.repositories.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {

    @Binds
    abstract fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository
}