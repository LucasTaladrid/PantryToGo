package com.lucasdev.apprecetas.recepies.domain.usecase

import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.repository.RecipeRepository
import com.lucasdev.apprecetas.shopping.domain.usecase.AddShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import javax.inject.Inject

/**
 * Use case for adding a new recipe to the repository.
 */
class AddRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to add a new recipe.
     *
     * @param recipe The recipe to be added.
     * @return The newly added [RecipeModel] or null if failed.
     */
    suspend operator fun invoke(recipe: RecipeModel): RecipeModel? =
        repository.addRecipe(recipe)
}

/**
 * Use case for retrieving common (public/shared) recipes.
 */
class GetCommonRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to fetch common recipes.
     *
     * @return A list of [RecipeModel] shared by all users.
     */
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getCommonRecipes()
}

/**
 * Use case for adding a recipe to the user's favorites.
 */
class AddRecipeToFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to mark a recipe as favorite.
     *
     * @param recipe The recipe to be added to favorites.
     */
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.addToFavorites(recipe)
    }
}

/**
 * Use case for removing a recipe from the user's favorites.
 */
class RemoveRecipeFromFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to unmark a recipe as favorite.
     *
     * @param recipe The recipe to be removed from favorites.
     */
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.removeFromFavorites(recipe)
    }
}

/**
 * Use case for adding a recipe to the pending list.
 */
class AddRecipeToPendingUseCase @Inject constructor(
    private val repository: RecipeRepository,
    private val getShoppingListsUseCase: GetShoppingListsUseCase,
    private val addShoppingListUseCase: AddShoppingListUseCase,
) {
    /**
     * Invokes the use case to add a recipe to the pending list.
     *
     * @param recipe The recipe to add.
     * @param shoppingListId The ID of the shopping list to update.
     */
    suspend operator fun invoke(recipe: RecipeModel, shoppingListId: String) {
        repository.addToPending(recipe, shoppingListId)
    }
}

/**
 * Use case for removing a recipe from the pending list.
 */
class RemoveRecipeFromPendingUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to remove a recipe from the pending list.
     *
     * @param recipe The recipe to remove.
     * @param shoppingListId The ID of the shopping list to update.
     */
    suspend operator fun invoke(recipe: RecipeModel, shoppingListId: String) {
        repository.removeFromPending(recipe, shoppingListId)
    }
}

/**
 * Use case for retrieving the user's favorite recipes.
 */
class GetFavoritesRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to fetch favorite recipes.
     *
     * @return A list of [RecipeModel] marked as favorites.
     */
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getFavoriteRecipes()
}

/**
 * Use case for retrieving the user's pending recipes.
 */
class GetPendingRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to fetch pending recipes.
     *
     * @return A list of [RecipeModel] marked as pending.
     */
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getPendingRecipes()
}

/**
 * Use case for retrieving recipes created by the user.
 */
class GetUserRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to fetch user-created recipes.
     *
     * @return A list of [RecipeModel] owned by the user.
     */
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getUserRecipes()
}

/**
 * Use case for deleting a recipe.
 */
class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to delete a recipe by ID.
     *
     * @param recipeId The ID of the recipe to delete.
     * @return True if deleted successfully, false otherwise.
     */
    suspend operator fun invoke(recipeId: String): Boolean =
        repository.deleteRecipe(recipeId)
}

/**
 * Use case for updating a recipe.
 */
class UpdateRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to update a recipe's details.
     *
     * @param recipe The updated recipe data.
     * @return True if updated successfully, false otherwise.
     */
    suspend operator fun invoke(recipe: RecipeModel): Boolean =
        repository.updateRecipe(recipe)
}

/**
 * Use case for marking a recipe as cooked.
 */
class MarkRecipeAsCookedUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    /**
     * Invokes the use case to mark a recipe as cooked.
     * This may update the user's pantry and remove the recipe from pending.
     *
     * @param recipe The recipe to mark as cooked.
     */
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.markRecipeAsCooked(recipe)
    }
}

