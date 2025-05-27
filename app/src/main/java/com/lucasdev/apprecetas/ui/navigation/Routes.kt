package com.lucasdev.apprecetas.ui.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    object Splash : Routes("splash")
    object Ingredients : Routes("ingredients")
    object Recipes : Routes("recipes")
    object Shopping : Routes("shopping")
    object MyIngredients : Routes("myIngredients")
    object MyRecipes : Routes("myRecepies")
    object MyPendingRecipes : Routes("myPendingRecipes")
    object MyFavouriteRecipes : Routes("myFavouriteRecipes")
    object MyShoppingHistory : Routes("myShoppingHistory")
    object Settings : Routes("settings")

}