package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import javax.inject.Inject

class GetIngredientsUseCase @Inject constructor(private val repository: IngredientRepository) {
    suspend operator fun invoke() = repository.getIngredients()
}

class AddIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    suspend operator fun invoke(ingredient: IngredientModel) = repository.addIngredient(ingredient)
}

class UpdateIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    suspend operator fun invoke(ingredient: IngredientModel) = repository.updateIngredient(ingredient)
}

class DeleteIngredientUseCase @Inject constructor(private val repository: IngredientRepository) {
    suspend operator fun invoke(id: String) = repository.deleteIngredient(id)
}

class GetUserIngredientUseCase @Inject constructor(private val repository: IngredientRepository){
    suspend operator fun invoke()=repository.getUserIngredients();
}