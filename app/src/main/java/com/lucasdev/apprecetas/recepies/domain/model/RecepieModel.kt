package com.lucasdev.apprecetas.recepies.domain.model

import com.google.firebase.Timestamp
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel

//todo, averiguar como se guardan los datos de las recetas, para saber como asignar un id.
data class RecipeModel(
    val id: String = "",
    val name: String="",
    val ingredients: List<PantryIngredientModel> = emptyList(),
    val steps: List<String> = emptyList(),
    val dateCreated: Timestamp = Timestamp.now()
)
