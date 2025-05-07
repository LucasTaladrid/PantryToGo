package com.lucasdev.apprecetas.users.domain.model

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.recepies.domain.model.RecepieModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingModel

//todo, lista de compra, lista de recetas, lista de recetas favoritas
data class UserModel(
    val uid: String="",
    val name: String="",
    val email: String="",
    val status: UserStatus = UserStatus.FREE,
    val isAdmin: Boolean = false,
    val ingredients: List<IngredientModel> = emptyList(),
    val recipes: List<RecepieModel> = emptyList(),
    val shoppingList: List<ShoppingModel> = emptyList(),
    val inventory : List<PantryIngredientModel> = emptyList()
)

enum class UserStatus {
    FREE, PREMIUM
}