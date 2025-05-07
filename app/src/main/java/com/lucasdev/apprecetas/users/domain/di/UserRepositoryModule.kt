package com.lucasdev.apprecetas.users.domain.di

import com.lucasdev.apprecetas.users.data.datasource.UserDataSource
import com.lucasdev.apprecetas.users.data.datasource.UserFirebaseDataSource
import com.lucasdev.apprecetas.users.domain.repository.UserRepository
import com.lucasdev.apprecetas.users.domain.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserDataSource(
        userFirebaseDataSource: UserFirebaseDataSource
    ): UserDataSource
}