package com.lucasdev.apprecetas.shopping.domain.model

import com.google.type.Date
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel

//todo puede que la lista la haya que cambiar, tengo que ver como asignar esas cantidades
data class ShoppingModel(
    val id: Int,
    val date: Date,
    val ingredients: List<IngredientModel> = emptyList()
)