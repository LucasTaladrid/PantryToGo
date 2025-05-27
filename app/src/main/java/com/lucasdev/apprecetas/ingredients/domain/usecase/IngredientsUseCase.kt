package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import javax.inject.Inject

/**
 * Use case to retrieve all ingredients.
 */
class GetIngredientsUseCase @Inject constructor(private val repository: IngredientRepository) {
    /**
     * Invokes the retrieval of all ingredients.
     */
    suspend operator fun invoke() = repository.getIngredients()
}

/**
 * Use case to add a new ingredient.
 */
class AddIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    /**
     * Invokes adding a new ingredient.
     *
     * @param ingredient The ingredient to add.
     */
    suspend operator fun invoke(ingredient: IngredientModel) = repository.addIngredient(ingredient)
}

/**
 * Use case to update an existing ingredient.
 */
class UpdateIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    /**
     * Invokes updating an existing ingredient.
     *
     * @param ingredient The ingredient with updated data.
     */
    suspend operator fun invoke(ingredient: IngredientModel) = repository.updateIngredient(ingredient)
}

/**
 * Use case to delete an ingredient by its ID.
 */
class DeleteIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    /**
     * Invokes deleting an ingredient.
     *
     * @param id The ID of the ingredient to delete.
     */
    suspend operator fun invoke(id: String) = repository.deleteIngredient(id)
}

/**
 * Use case to retrieve ingredients specific to the user.
 */
class GetUserIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    /**
     * Invokes retrieval of user-specific ingredients.
     */
    suspend operator fun invoke() = repository.getUserIngredients()
}
