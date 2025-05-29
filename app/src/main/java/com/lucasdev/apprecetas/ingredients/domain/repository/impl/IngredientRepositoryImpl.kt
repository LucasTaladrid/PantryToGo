package com.lucasdev.apprecetas.ingredients.domain.repository.impl

import com.lucasdev.apprecetas.ingredients.data.datasource.IngredientFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import javax.inject.Inject

/**
 * Implementation of [IngredientRepository] that delegates all data operations
 * related to ingredients to [IngredientFirebaseDataSource].
 *
 * This repository handles CRUD operations for both user-specific and common ingredients,
 * determining access and behavior based on the user's role (admin or regular).
 *
 * @property dataSource the data source that provides access to Firebase for ingredient data.
 */
class IngredientRepositoryImpl @Inject constructor(
    private val dataSource: IngredientFirebaseDataSource
) : IngredientRepository {

    /**
     * Retrieves a combined list of common and user-specific ingredients.
     */
    override suspend fun getIngredients(): List<IngredientModel> = dataSource.getIngredients()

    /**
     * Retrieves only the common ingredients.
     */
    override suspend fun getCommonIngredients(): List<IngredientModel> =
        dataSource.getCommonIngredients()


    /**
     * Adds a new ingredient to the appropriate collection based on user role.
     * Returns false if the ingredient already exists.
     */
    override suspend fun addIngredient(ingredient: IngredientModel): Boolean =
        dataSource.addIngredient(ingredient)

    /**
     * Updates an existing ingredient and propagates changes to related data (e.g., pantry, shopping list).
     */
    override suspend fun updateIngredient(ingredient: IngredientModel): Boolean =
        dataSource.updateIngredient(ingredient)

    /**
     * Deletes an ingredient by ID and removes references across related collections.
     */
    override suspend fun deleteIngredient(id: String): Boolean =
        dataSource.deleteIngredient(id)

    /**
     * Retrieves only the ingredients added by the current user.
     */
    override suspend fun getUserIngredients(): List<IngredientModel> =
        dataSource.getUserIngredients()

}


