package com.lucasdev.apprecetas.ui.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    object Ingredients : Routes("ingredients")
    object Recepies : Routes("recipes")
    object Shopping : Routes("shopping")
    object MyIngredients : Routes("myIngredients")
    object MyRecepies : Routes("myRecepies")
    object MyShoppingHistory : Routes("myShoppingHistory")

}