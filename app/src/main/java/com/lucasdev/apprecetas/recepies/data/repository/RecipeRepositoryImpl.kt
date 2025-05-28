package com.lucasdev.apprecetas.recepies.data.repository

import com.lucasdev.apprecetas.recepies.data.datasource.RecipeFirebaseDataSource
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.repository.RecipeRepository
import javax.inject.Inject

/**
 * Implementation of [RecipeRepository] that delegates data operations
 * to [RecipeFirebaseDataSource], which handles interaction with Firebase.
 *
 * @property dataSource The Firebase data source used to perform recipe operations.
 */
class RecipeRepositoryImpl @Inject constructor(
    private val dataSource: RecipeFirebaseDataSource
) : RecipeRepository {

    /**
     * Adds a new recipe to the database.
     *
     * @param recipe The recipe to be added.
     * @return The recipe with an assigned ID if successful, null otherwise.
     */
    override suspend fun addRecipe(recipe: RecipeModel): RecipeModel? =
        dataSource.addRecipe(recipe)

    /**
     * Retrieves a list of common (public) recipes.
     *
     * @return A list of [RecipeModel] representing common recipes.
     */
    override suspend fun getCommonRecipes(): List<RecipeModel> =
        dataSource.getCommonRecipes()

    /**
     * Adds a recipe to the user's favorites collection.
     *
     * @param recipe The recipe to be added to favorites.
     */
    override suspend fun addToFavorites(recipe: RecipeModel) {
        dataSource.addToFavorites(recipe)
    }

    /**
     * Removes a recipe from the user's favorites collection.
     *
     * @param recipe The recipe to be removed from favorites.
     */
    override suspend fun removeFromFavorites(recipe: RecipeModel) {
        dataSource.removeFromFavorites(recipe)
    }

    /**
     * Adds a recipe to the user's pending list and updates the shopping list.
     *
     * @param recipe The recipe to be added to the pending list.
     * @param shoppingListId The ID of the shopping list to update.
     */
    override suspend fun addToPending(recipe: RecipeModel, shoppingListId: String) {
        dataSource.addToPending(recipe, shoppingListId)
    }

    /**
     * Removes a recipe from the user's pending list and subtracts its ingredients
     * from the associated shopping list.
     *
     * @param recipe The recipe to be removed from the pending list.
     * @param shoppingListId The ID of the shopping list to update.
     */
    override suspend fun removeFromPending(recipe: RecipeModel, shoppingListId: String) {
        dataSource.removeFromPending(recipe, shoppingListId)
    }

    /**
     * Retrieves the list of the user's favorite recipes.
     *
     * @return A list of [RecipeModel] representing the favorite recipes.
     */
    override suspend fun getFavoriteRecipes(): List<RecipeModel> =
        dataSource.getFavoriteRecipes()

    /**
     * Retrieves the list of the user's pending recipes.
     *
     * @return A list of [RecipeModel] representing the pending recipes.
     */
    override suspend fun getPendingRecipes(): List<RecipeModel> =
        dataSource.getPendingRecipes()

    /**
     * Retrieves the list of recipes created by the current user.
     *
     * @return A list of [RecipeModel] representing the user's recipes.
     */
    override suspend fun getUserRecipes(): List<RecipeModel> =
        dataSource.getUserRecipes()

    /**
     * Deletes a recipe from the database, including from all users' favorites and pending lists
     * if the current user is an admin.
     *
     * @param recipeId The ID of the recipe to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    override suspend fun deleteRecipe(recipeId: String): Boolean =
        dataSource.deleteRecipe(recipeId)

    /**
     * Updates an existing recipe in the database and reflects the change across all relevant user collections.
     *
     * @param recipe The updated recipe.
     * @return True if the update was successful, false otherwise.
     */
    override suspend fun updateRecipe(recipe: RecipeModel): Boolean =
        dataSource.updateRecipe(recipe)

    /**
     * Marks a recipe as cooked, removes it from the pending list, and updates pantry quantities accordingly.
     *
     * @param recipe The recipe that has been cooked.
     */
    override suspend fun markRecipeAsCooked(recipe: RecipeModel) {
        dataSource.markRecipeAsCooked(recipe)
    }


}
