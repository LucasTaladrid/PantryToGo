package com.lucasdev.apprecetas.ingredients.domain.repository


import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel

/**
 * Repository interface for managing pantry ingredients.
 */
interface PantryIngredientRepository {

    /**
     * Retrieves all pantry ingredients for the current user.
     *
     * @return A list of [PantryIngredientModel] representing the user's pantry ingredients.
     */
    suspend fun getIngredients(): List<PantryIngredientModel>

    /**
     * Adds a pantry ingredient or updates its quantity if it already exists.
     *
     * @param ingredient The [PantryIngredientModel] to add or update.
     * @return The updated or newly added [PantryIngredientModel] with assigned ID.
     */
    suspend fun addIngredient(ingredient: PantryIngredientModel): PantryIngredientModel

    /**
     * Updates a pantry ingredient's details.
     *
     * @param ingredient The [PantryIngredientModel] with updated information.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateIngredient(ingredient: PantryIngredientModel): Boolean

    /**
     * Deletes a pantry ingredient by its ID.
     *
     * @param id The ID of the pantry ingredient to delete.
     * @return `true` if the deletion was successful, `false` otherwise.
     */
    suspend fun deleteIngredient(id: String): Boolean

    /**
     * Retrieves a pantry ingredient by its pantry-specific ID.
     *
     * @param id The ID of the pantry ingredient.
     * @return The corresponding [PantryIngredientModel] or `null` if not found.
     */
    suspend fun getIngredientById(id: String): PantryIngredientModel?

    /**
     * Retrieves a pantry ingredient by the associated ingredient ID.
     *
     * @param ingredientId The ID of the ingredient.
     * @return The corresponding [PantryIngredientModel] or `null` if not found.
     */
    suspend fun getIngredientByIngredientId(ingredientId: String): PantryIngredientModel?

    /**
     * Adds multiple pantry ingredients, typically from a shopping list.
     *
     * @param ingredients The list of [PantryIngredientModel] to add.
     * @return The list of added [PantryIngredientModel] with assigned IDs.
     */
    suspend fun addIngredientsToPantryFromShopping(ingredients: List<PantryIngredientModel>): List<PantryIngredientModel>
}
