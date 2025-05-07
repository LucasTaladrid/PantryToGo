package com.lucasdev.apprecetas.recepies.domain.model

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
//todo, averiguar como se guardan los datos de las recetas, para saber como asignar un id.
data class RecepieModel(
    val id: Int,
    val name: String="",
    val ingredients: List<IngredientModel> = emptyList(),
    val description: String="",
    val photoUrl: String="",
    val instructions: String=""
)
