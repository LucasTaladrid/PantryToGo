package com.lucasdev.apprecetas.ingredients.domain.repository


import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel

interface PantryIngredientRepository {
    suspend fun getIngredients(): List<PantryIngredientModel>
    suspend fun addIngredient(ingredient: PantryIngredientModel): PantryIngredientModel
    suspend fun updateIngredient(ingredient: PantryIngredientModel):Boolean
    suspend fun deleteIngredient(id:String): Boolean
    suspend fun getIngredientById(id: String): PantryIngredientModel?
    suspend fun addIngredientsToPantryFromShopping(ingredients: List<PantryIngredientModel>): List<PantryIngredientModel>



}