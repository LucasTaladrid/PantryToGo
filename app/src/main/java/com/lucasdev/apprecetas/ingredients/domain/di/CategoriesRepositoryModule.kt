package com.lucasdev.apprecetas.ingredients.domain.di

import com.lucasdev.apprecetas.ingredients.domain.repository.impl.CategoriesRepositoryImpl
import com.lucasdev.apprecetas.ingredients.domain.repository.CategoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoriesRepositoryModule {

    @Binds
    abstract fun bindCategoriesRepository(
        impl: CategoriesRepositoryImpl
    ): CategoriesRepository
}
