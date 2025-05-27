package com.lucasdev.apprecetas.recepies.domain.usecase

import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.repository.RecipeRepository
import javax.inject.Inject

class AddRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel): RecipeModel? =
        repository.addRecipe(recipe)
}

class GetCommonRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getCommonRecipes()
}
class AddRecipeToFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.addToFavorites(recipe)
    }
}

class RemoveRecipeFromFavoritesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.removeFromFavorites(recipe)
    }
}

class AddRecipeToPendingUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel, shoppingListId: String) {
        repository.addToPending(recipe, shoppingListId)
    }
}

class RemoveRecipeFromPendingUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel, shoppingListId: String) {
        repository.removeFromPending(recipe, shoppingListId)
    }
}

class GetFavoritesRecipesUseCase @Inject constructor(
    private val repository:RecipeRepository
){
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getFavoriteRecipes()

}

class GetPendingRecipesUseCase @Inject constructor(
    private val repository:RecipeRepository
){
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getPendingRecipes()

}
class GetUserRecipeUseCase @Inject constructor(
    private val repository:RecipeRepository
){
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getUserRecipes()
}

class DeleteRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: String): Boolean =
        repository.deleteRecipe(recipeId)
}

class UpdateRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel): Boolean =
        repository.updateRecipe(recipe)
}

class MarkRecipeAsCookedUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.markRecipeAsCooked(recipe)
    }
}

