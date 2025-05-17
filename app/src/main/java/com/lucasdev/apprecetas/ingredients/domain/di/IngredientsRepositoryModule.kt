package com.lucasdev.apprecetas.ingredients.domain.di

import com.lucasdev.apprecetas.ingredients.data.repository.IngredientRepositoryImpl
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IngredientsRepositoryModule {
    @Binds
    abstract fun bindIngredientsRepository(
        ingredientsRepositoryImpl: IngredientRepositoryImpl
    ): IngredientRepository

}
