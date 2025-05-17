package com.lucasdev.apprecetas.recepies.domain.repository

import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel

interface RecipeRepository {
    suspend fun addRecipe(recipe: RecipeModel): RecipeModel?
    suspend fun getRecipes(): List<RecipeModel>
}
