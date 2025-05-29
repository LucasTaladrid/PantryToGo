package com.lucasdev.apprecetas.ingredients.domain.di

import com.lucasdev.apprecetas.ingredients.domain.repository.impl.PantryIngredientRepositoryImpl
import com.lucasdev.apprecetas.ingredients.domain.repository.PantryIngredientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserIngredientsRepositoryModule {

    @Binds
    abstract fun bindUserIngredientsRepository(
        pantryIngredientRepositoryImpl: PantryIngredientRepositoryImpl
    ): PantryIngredientRepository

}