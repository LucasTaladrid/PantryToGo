package com.lucasdev.apprecetas.ingredients.data.repository

import com.lucasdev.apprecetas.ingredients.data.datasource.PantryIngredientFirebaseDataSource
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.PantryIngredientRepository
import javax.inject.Inject

/**
 * Implementation of [PantryIngredientRepository] that delegates pantry ingredient operations
 * to [PantryIngredientFirebaseDataSource].
 *
 * This repository manages CRUD operations for pantry ingredients specific to the current user.
 *
 * @property dataSource the data source responsible for interacting with Firebase Firestore.
 */
class PantryIngredientRepositoryImpl @Inject constructor(
    private val dataSource: PantryIngredientFirebaseDataSource
) : PantryIngredientRepository {

    /**
     * Retrieves the list of pantry ingredients for the current user.
     */
    override suspend fun getIngredients(): List<PantryIngredientModel> = dataSource.getPantry()

    /**
     * Adds a pantry ingredient. If the ingredient already exists, updates the quantity.
     *
     * @param ingredient the pantry ingredient to add or update.
     * @return the added or updated pantry ingredient with an assigned ID.
     */
    override suspend fun addIngredient(ingredient: PantryIngredientModel): PantryIngredientModel =
        dataSource.addIngredientToPantry(ingredient)

    /**
     * Updates the quantity or other mutable fields of an existing pantry ingredient.
     *
     * @param ingredient the pantry ingredient with updated data.
     * @return true if the update was successful, false otherwise.
     */
    override suspend fun updateIngredient(ingredient: PantryIngredientModel): Boolean =
        dataSource.updateIngredientPantry(ingredient)

    /**
     * Deletes a pantry ingredient by its unique ID.
     *
     * @param id the unique identifier of the pantry ingredient to delete.
     * @return true if deletion was successful, false otherwise.
     */
    override suspend fun deleteIngredient(id: String): Boolean =
        dataSource.deleteIngredientFromPantry(id)

    /**
     * Retrieves a pantry ingredient by its unique ID.
     *
     * @param id the unique identifier of the pantry ingredient.
     * @return the pantry ingredient if found, or null otherwise.
     */
    override suspend fun getIngredientById(id: String): PantryIngredientModel? {
        return dataSource.getIngredientById(id)
    }

    /**
     * Retrieves a pantry ingredient by the ingredient's ID it references.
     *
     * @param ingredientId the ID of the referenced ingredient.
     * @return the pantry ingredient if found, or null otherwise.
     */
    override suspend fun getIngredientByIngredientId(ingredientId: String): PantryIngredientModel? {
        return dataSource.getIngredientByIngredientId(ingredientId)
    }

    /**
     * Adds multiple pantry ingredients at once, typically from the shopping list.
     *
     * @param ingredients the list of pantry ingredients to add.
     * @return the list of added pantry ingredients with assigned IDs.
     */
    override suspend fun addIngredientsToPantryFromShopping(ingredients: List<PantryIngredientModel>): List<PantryIngredientModel> {
       return dataSource.addIngredientListToPantry(ingredients)
    }


}
