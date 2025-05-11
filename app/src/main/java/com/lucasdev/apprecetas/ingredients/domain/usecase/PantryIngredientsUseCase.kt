package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.PantryIngredientRepository
import javax.inject.Inject

class GetUserPantryIngredientsUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    suspend operator fun invoke() = repository.getIngredients()
}

class AddUserPantryIngredientUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    suspend operator fun invoke(ingredient: PantryIngredientModel) = repository.addIngredient(ingredient)
}

class UpdateUserPantryIngredientUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    suspend operator fun invoke(ingredient: PantryIngredientModel): Boolean {
        val currentIngredient = repository.getIngredientById(ingredient.id)

        return if (currentIngredient != null) {
            val updatedIngredient = currentIngredient.copy(quantity = ingredient.quantity)
            repository.updateIngredient(updatedIngredient)
        } else {
            false
        }
    }
}

class DeleteUserPantryIngredientUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    suspend operator fun invoke(id: String) = repository.deleteIngredient(id)
}

class AddIngredientsToPantryFromShoppingUseCase @Inject constructor(
    private val repository: PantryIngredientRepository
) {
    suspend operator fun invoke(ingredients: List<PantryIngredientModel>): List<PantryIngredientModel> {
        return repository.addIngredientsToPantryFromShopping(ingredients)
    }
}

class GetUserPantryIngredientByIngredientIdUseCase @Inject constructor(
    private val repository: PantryIngredientRepository
) {
    suspend operator fun invoke(ingredientId: String): PantryIngredientModel? {
        return repository.getIngredientById(ingredientId)
    }
}


