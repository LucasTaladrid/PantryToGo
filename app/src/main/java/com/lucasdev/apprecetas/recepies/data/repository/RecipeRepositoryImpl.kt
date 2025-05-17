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

}
