package com.lucasdev.apprecetas.ingredients.domain.model

/**
 * Represents an ingredient with its details.
 *
 * @property id Unique identifier of the ingredient.
 * @property name Name of the ingredient.
 * @property category Category to which the ingredient belongs.
 * @property unit Unit type used to measure the ingredient.
 */
data class IngredientModel(
    val id:String="",
    val name:String="",
    val category: CategoryModel= CategoryModel(),
    val unit: UnitTypeModel=UnitTypeModel(),
)

/**
 * Represents an ingredient stored in the user's pantry.
 *
 * @property id Unique identifier of the pantry ingredient entry.
 * @property ingredientId Identifier of the related ingredient.
 * @property name Name of the ingredient.
 * @property category Category of the ingredient.
 * @property unit Unit type used to measure the ingredient.
 * @property quantity Amount of the ingredient in the pantry.
 */
data class PantryIngredientModel(
    val id: String = "",
    val ingredientId:String="",
    val name: String = "",
    val category: CategoryModel = CategoryModel(),
    val unit: UnitTypeModel = UnitTypeModel(),
    val quantity: Double =0.0
)

/**
 * Represents a section grouping ingredients by category.
 *
 * @property category Name of the category.
 * @property ingredients List of ingredients under this category.
 */
data class IngredientSection(
    val category: String,
    val ingredients: List<IngredientModel>
)

/**
 * Represents a section grouping pantry ingredients by category.
 *
 * @property category Name of the category.
 * @property ingredients List of pantry ingredients under this category.
 */
data class PantryIngredientSection(
    val category: String,
    val ingredients: List<PantryIngredientModel>
)