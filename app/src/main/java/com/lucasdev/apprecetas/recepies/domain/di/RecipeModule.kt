package com.lucasdev.apprecetas.recepies.domain.di

import com.lucasdev.apprecetas.recepies.data.datasource.RecipeFirebaseDataSource
import com.lucasdev.apprecetas.recepies.domain.repository.impl.RecipeRepositoryImpl
import com.lucasdev.apprecetas.recepies.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RecipeModule {
    @Binds
    abstract fun bindRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RecipeDataSourceModule {
    @Provides
    fun provideRecipeDataSource() = RecipeFirebaseDataSource()
}
