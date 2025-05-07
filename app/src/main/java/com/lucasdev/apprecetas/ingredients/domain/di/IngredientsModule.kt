package com.lucasdev.apprecetas.ingredients.domain.di

import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import com.lucasdev.apprecetas.ingredients.domain.usecase.AddIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.DeleteIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.UpdateIngredientUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class IngredientsModule {

    @Provides
    @ViewModelScoped
    fun provideGetIngredientsUseCase(repository: IngredientRepository): GetIngredientsUseCase {
        return GetIngredientsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideAddIngredientUseCase(repository: IngredientRepository): AddIngredientUseCase {
        return AddIngredientUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateIngredientUseCase(repository: IngredientRepository): UpdateIngredientUseCase {
        return UpdateIngredientUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteIngredientUseCase(repository: IngredientRepository): DeleteIngredientUseCase {
        return DeleteIngredientUseCase(repository)
    }
}