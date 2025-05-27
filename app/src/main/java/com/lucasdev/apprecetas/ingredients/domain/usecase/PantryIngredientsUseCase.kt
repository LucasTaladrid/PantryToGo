package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.PantryIngredientRepository
import javax.inject.Inject

/**
 * Use case to get all pantry ingredients for the user.
 */
class GetUserPantryIngredientsUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    /**
     * Executes the retrieval of the user's pantry ingredients.
     */
    suspend operator fun invoke() = repository.getIngredients()
}

/**
 * Use case to add an ingredient to the user's pantry.
 */
class AddUserPantryIngredientUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    /**
     * Executes adding a pantry ingredient.
     *
     * @param ingredient The pantry ingredient to add.
     * @return The added pantry ingredient with updated information.
     */
    suspend operator fun invoke(ingredient: PantryIngredientModel) = repository.addIngredient(ingredient)
}

/**
 * Use case to update an existing pantry ingredient.
 */
class UpdateUserPantryIngredientUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    /**
     * Executes the update of a pantry ingredient's quantity.
     * Checks if the ingredient exists before updating.
     *
     * @param ingredient The pantry ingredient with updated data.
     * @return True if update was successful, false otherwise.
     */
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

/**
 * Use case to delete a pantry ingredient by its ID.
 */
class DeleteUserPantryIngredientUseCase @Inject constructor(private val repository: PantryIngredientRepository) {
    /**
     * Executes the deletion of a pantry ingredient.
     *
     * @param id The ID of the pantry ingredient to delete.
     * @return True if deletion was successful, false otherwise.
     */
    suspend operator fun invoke(id: String) = repository.deleteIngredient(id)
}


/**
 * Use case to add multiple ingredients to the pantry from the shopping list.
 */
class AddIngredientsToPantryFromShoppingUseCase @Inject constructor(
    private val repository: PantryIngredientRepository
) {
    /**
     * Executes adding a list of pantry ingredients to the pantry.
     *
     * @param ingredients List of pantry ingredients to add.
     * @return List of added pantry ingredients with updated info.
     */
    suspend operator fun invoke(ingredients: List<PantryIngredientModel>): List<PantryIngredientModel> {
        return repository.addIngredientsToPantryFromShopping(ingredients)
    }
}

/**
 * Use case to get a pantry ingredient by its associated ingredient ID.
 */
class GetUserPantryIngredientByIngredientIdUseCase @Inject constructor(
    private val repository: PantryIngredientRepository
) {
    /**
     * Executes retrieval of a pantry ingredient by ingredientId.
     *
     * @param ingredientId The ingredientId to search for.
     * @return The pantry ingredient if found, or null if not found.
     */
    suspend operator fun invoke(ingredientId: String): PantryIngredientModel? {
        return repository.getIngredientById(ingredientId)
    }
}


