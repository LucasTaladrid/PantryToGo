package com.lucasdev.apprecetas.ingredients.domain.di

import com.lucasdev.apprecetas.ingredients.data.repository.UnitTypeRepositoryImpl
import com.lucasdev.apprecetas.ingredients.domain.repository.UnitTypeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UnitTypeRepositoryModule {

    @Binds
    abstract fun bindUnitTypeRepository(
    impl: UnitTypeRepositoryImpl
    ): UnitTypeRepository
}