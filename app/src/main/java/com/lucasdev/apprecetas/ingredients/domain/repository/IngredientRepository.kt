package com.lucasdev.apprecetas.ingredients.domain.repository

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel


interface IngredientRepository  {
    suspend fun getIngredients(): List<IngredientModel>
    suspend fun addIngredient(ingredient: IngredientModel): Boolean
    suspend fun updateIngredient(ingredient: IngredientModel): Boolean
    suspend fun deleteIngredient(id: String): Boolean
    suspend fun getUserIngredients(): List<IngredientModel>
}