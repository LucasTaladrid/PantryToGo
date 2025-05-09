package com.lucasdev.apprecetas.ingredients.data.repository

import com.lucasdev.apprecetas.ingredients.data.datasource.PantryIngredientFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.PantryIngredientRepository
import javax.inject.Inject

class PantryIngredientRepositoryImpl @Inject constructor(
    private val dataSource: PantryIngredientFirebaseDataSource
) : PantryIngredientRepository {

    override suspend fun getIngredients(): List<PantryIngredientModel> = dataSource.getPantry()

    override suspend fun addIngredient(ingredient: PantryIngredientModel): PantryIngredientModel =
        dataSource.addIngredientToPantry(ingredient)

    override suspend fun updateIngredient(ingredient: PantryIngredientModel): Boolean =
        dataSource.updateIngredientPantry(ingredient)

    override suspend fun deleteIngredient(id: String): Boolean =
        dataSource.deleteIngredientFromPantry(id)

    override suspend fun getIngredientById(id: String): PantryIngredientModel? {
        return dataSource.getIngredientById(id)
    }

    override suspend fun addIngredientsToPantryFromShopping(ingredients: List<PantryIngredientModel>): List<PantryIngredientModel> {
       return dataSource.addIngredientsToPantry(ingredients)
    }


}
