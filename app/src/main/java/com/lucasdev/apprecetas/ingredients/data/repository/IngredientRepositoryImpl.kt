package com.lucasdev.apprecetas.ingredients.data.repository

import com.lucasdev.apprecetas.ingredients.data.datasource.IngredientFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val dataSource: IngredientFirebaseDataSource
) : IngredientRepository {

    override suspend fun getIngredients(): List<IngredientModel> = dataSource.getIngredients()

    override suspend fun addIngredient(ingredient: IngredientModel): Boolean =
        dataSource.addIngredient(ingredient)

    override suspend fun updateIngredient(ingredient: IngredientModel): Boolean =
        dataSource.updateIngredient(ingredient)

    override suspend fun deleteIngredient(id: String): Boolean =
        dataSource.deleteIngredient(id)

    override suspend fun getUserIngredients(): List<IngredientModel> =
        dataSource.getUserIngredients()

}


