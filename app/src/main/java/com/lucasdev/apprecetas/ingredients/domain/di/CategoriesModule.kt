package com.lucasdev.apprecetas.ingredients.domain.di

import com.lucasdev.apprecetas.ingredients.domain.repository.CategoriesRepository
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class CategoriesModule {
    @Provides
    @ViewModelScoped
    fun provideGetCategoriesUseCase(
        repository: CategoriesRepository
    ): GetCategoriesUseCase {
        return GetCategoriesUseCase(repository)
    }
}