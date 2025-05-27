package com.lucasdev.apprecetas.ingredients.domain.repository

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel


/**
 * Repository interface for managing ingredients.
 */
interface IngredientRepository {

    /**
     * Retrieves all ingredients, including common and user-specific ones.
     *
     * @return A list of [IngredientModel] representing all ingredients.
     */
    suspend fun getIngredients(): List<IngredientModel>

    /**
     * Adds a new ingredient.
     *
     * @param ingredient The [IngredientModel] to add.
     * @return `true` if the ingredient was added successfully, `false` otherwise.
     */
    suspend fun addIngredient(ingredient: IngredientModel): Boolean

    /**
     * Updates an existing ingredient.
     *
     * @param ingredient The [IngredientModel] with updated data.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateIngredient(ingredient: IngredientModel): Boolean

    /**
     * Deletes an ingredient by its ID.
     *
     * @param id The ID of the ingredient to delete.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    suspend fun deleteIngredient(id: String): Boolean

    /**
     * Retrieves only the ingredients specific to the current user.
     *
     * @return A list of [IngredientModel] representing user-specific ingredients.
     */
    suspend fun getUserIngredients(): List<IngredientModel>
}
