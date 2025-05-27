package com.lucasdev.apprecetas.users.domain.di

import com.lucasdev.apprecetas.users.data.datasource.RegisterFirebaseDataSource
import com.lucasdev.apprecetas.users.domain.repository.RegisterRepository
import com.lucasdev.apprecetas.users.domain.repository.RegisterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegisterRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        registerRepositoryImpl: RegisterRepositoryImpl
    ): RegisterRepository


}