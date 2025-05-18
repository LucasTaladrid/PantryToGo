package com.lucasdev.apprecetas.recepies.data.repository

import com.lucasdev.apprecetas.recepies.data.datasource.RecipeFirebaseDataSource
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.repository.RecipeRepository
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val dataSource: RecipeFirebaseDataSource
) : RecipeRepository {
    override suspend fun addRecipe(recipe: RecipeModel): RecipeModel? =
        dataSource.addRecipe(recipe)

    override suspend fun getRecipes(): List<RecipeModel> =
        dataSource.getCommonRecipes()

    override suspend fun addToFavorites(recipe: RecipeModel) {
        dataSource.addToFavorites(recipe)
    }

    override suspend fun removeFromFavorites(recipe: RecipeModel) {
        dataSource.removeFromFavorites(recipe)
    }

    override suspend fun addToPending(recipe: RecipeModel) {
        dataSource.addToPending(recipe)
    }

    override suspend fun removeFromPending(recipe: RecipeModel) {
        dataSource.removeFromPending(recipe)
    }

    override suspend fun getFavoriteRecipes(): List<RecipeModel> =
        dataSource.getFavoriteRecipes()


    override suspend fun getPendingRecipes(): List<RecipeModel> =
        dataSource.getPendingRecipes()


}
