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
