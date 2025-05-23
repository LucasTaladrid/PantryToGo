package com.lucasdev.apprecetas.recepies.domain.repository

import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel

interface RecipeRepository {
    suspend fun addRecipe(recipe: RecipeModel): RecipeModel?
    suspend fun getCommonRecipes(): List<RecipeModel>
    suspend fun addToFavorites(recipe: RecipeModel)
    suspend fun removeFromFavorites(recipe: RecipeModel)
    suspend fun addToPending(recipe: RecipeModel)
    suspend fun removeFromPending(recipe: RecipeModel)
    suspend fun getFavoriteRecipes(): List<RecipeModel>
    suspend fun getPendingRecipes(): List<RecipeModel>
    suspend fun getUserRecipes() :List<RecipeModel>
    suspend fun deleteRecipe(recipeId: String): Boolean
    suspend fun updateRecipe(recipe: RecipeModel): Boolean

}
