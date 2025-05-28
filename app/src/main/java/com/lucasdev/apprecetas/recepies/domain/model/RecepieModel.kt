package com.lucasdev.apprecetas.recepies.domain.model

import com.google.firebase.Timestamp
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel

/**
 * Represents a recipe that can be created, saved, or retrieved by the user.
 *
 * @property id Unique identifier for the recipe.
 * @property name Name of the recipe.
 * @property ingredients List of ingredients required for the recipe.
 * @property steps Step-by-step instructions to prepare the recipe.
 * @property dateCreated Timestamp indicating when the recipe was created.
 */
data class RecipeModel(
    val id: String = "",
    val name: String="",
    val ingredients: List<PantryIngredientModel> = emptyList(),
    val steps: List<String> = emptyList(),
    val dateCreated: Timestamp = Timestamp.now()
)
