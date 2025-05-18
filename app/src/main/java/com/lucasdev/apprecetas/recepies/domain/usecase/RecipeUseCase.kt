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

class GetRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): List<RecipeModel> =
        repository.getRecipes()
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
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.addToPending(recipe)
    }
}

class RemoveRecipeFromPendingUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: RecipeModel) {
        repository.removeFromPending(recipe)
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

