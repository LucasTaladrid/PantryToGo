package com.lucasdev.apprecetas.ingredients.domain.model

data class IngredientModel(
    val id:String="",
    val name:String="",
    val category: CategoryModel= CategoryModel(),
    val unit: UnitTypeModel=UnitTypeModel(),
)

data class PantryIngredientModel(
    val id: String = "",
    val ingredientId:String="",
    val name: String = "",
    val category: CategoryModel = CategoryModel(),
    val unit: UnitTypeModel = UnitTypeModel(),
    val quantity: Double =0.0
)

data class IngredientSection(
    val category: String,
    val ingredients: List<IngredientModel>
)

data class PantryIngredientSection(
    val category: String,
    val ingredients: List<PantryIngredientModel>
)