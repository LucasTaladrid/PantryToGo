package com.lucasdev.apprecetas.recepies.domain.repository

import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel

/**
 * Repository interface for managing recipe-related operations.
 *
 * This interface defines the contract for working with recipes, including
 * CRUD operations, marking as favorites or pending, and pantry integration.
 */
interface RecipeRepository {

    /**
     * Adds a new recipe to the database.
     *
     * @param recipe The recipe to be added.
     * @return The saved [RecipeModel] with a generated ID if successful, or null otherwise.
     */
    suspend fun addRecipe(recipe: RecipeModel): RecipeModel?

    /**
     * Retrieves a list of common (public) recipes accessible by all users.
     *
     * @return A list of [RecipeModel] representing public recipes.
     */
    suspend fun getCommonRecipes(): List<RecipeModel>

    /**
     * Adds a recipe to the user's favorites list.
     *
     * @param recipe The recipe to add to favorites.
     */
    suspend fun addToFavorites(recipe: RecipeModel)

    /**
     * Removes a recipe from the user's favorites list.
     *
     * @param recipe The recipe to remove from favorites.
     */
    suspend fun removeFromFavorites(recipe: RecipeModel)

    /**
     * Adds a recipe to the user's pending list and updates the shopping list accordingly.
     *
     * @param recipe The recipe to add to the pending list.
     * @param shoppingListId The ID of the shopping list to update with missing ingredients.
     */
    suspend fun addToPending(recipe: RecipeModel, shoppingListId: String)

    /**
     * Removes a recipe from the user's pending list and updates the shopping list.
     *
     * @param recipe The recipe to remove from the pending list.
     * @param shoppingListId The ID of the shopping list to update.
     */
    suspend fun removeFromPending(recipe: RecipeModel, shoppingListId: String)

    /**
     * Retrieves the list of recipes marked as favorites by the user.
     *
     * @return A list of [RecipeModel] representing the user's favorite recipes.
     */
    suspend fun getFavoriteRecipes(): List<RecipeModel>

    /**
     * Retrieves the list of recipes currently marked as pending by the user.
     *
     * @return A list of [RecipeModel] representing pending recipes.
     */
    suspend fun getPendingRecipes(): List<RecipeModel>

    /**
     * Retrieves the list of recipes created by the current user.
     *
     * @return A list of [RecipeModel] owned by the user.
     */
    suspend fun getUserRecipes(): List<RecipeModel>

    /**
     * Deletes a recipe by its ID. Admin users may also remove the recipe from
     * all users' favorites and pending lists.
     *
     * @param recipeId The unique identifier of the recipe to delete.
     * @return True if the operation succeeded, false otherwise.
     */
    suspend fun deleteRecipe(recipeId: String): Boolean

    /**
     * Updates an existing recipe's data. This may also update its presence
     * in user favorites and pending collections.
     *
     * @param recipe The modified recipe.
     * @return True if the update succeeded, false otherwise.
     */
    suspend fun updateRecipe(recipe: RecipeModel): Boolean

    /**
     * Marks a recipe as cooked. This removes the recipe from the pending list
     * and updates pantry ingredient quantities accordingly.
     *
     * @param recipe The recipe that has been cooked.
     */
    suspend fun markRecipeAsCooked(recipe: RecipeModel)
}

