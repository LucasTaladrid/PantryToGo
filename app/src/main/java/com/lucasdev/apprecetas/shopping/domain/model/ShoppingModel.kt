package com.lucasdev.apprecetas.shopping.domain.model

import com.google.firebase.Timestamp
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel



//todo puede que la lista la haya que cambiar, tengo que ver como asignar esas cantidades
data class ShoppingListModel(
    val id: String = "",
    val title: String = "",
    val date: Timestamp = Timestamp.now(),
    val items: List<ShoppingItemModel> = emptyList()
)
data class ShoppingItemModel(
    val ingredientId: String = "",
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: UnitTypeModel = UnitTypeModel(),
    val category: CategoryModel = CategoryModel(),
    val checked: Boolean = false
)
