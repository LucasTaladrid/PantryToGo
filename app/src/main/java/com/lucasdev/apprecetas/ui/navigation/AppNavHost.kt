package com.lucasdev.apprecetas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.lucasdev.apprecetas.ingredients.ui.IngredientsScreen
import androidx.navigation.compose.composable
import com.lucasdev.apprecetas.users.ui.LoginScreen
import com.lucasdev.apprecetas.users.ui.LoginScreenViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasdev.apprecetas.ingredients.ui.MyIngredientsScreen
import com.lucasdev.apprecetas.ingredients.ui.PantryIngredientsViewModel
import com.lucasdev.apprecetas.shopping.ui.ShoppingListScreen
import com.lucasdev.apprecetas.shopping.ui.ShoppingListViewModel
import com.lucasdev.apprecetas.users.ui.RegisterScreen
import com.lucasdev.apprecetas.users.ui.RegisterViewModel


@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            val viewModel: LoginScreenViewModel = hiltViewModel()
            LoginScreen(
                loginScreenViewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Ingredients.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                registerViewModel = viewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                backLoginScreen = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Ingredients.route) {
            val loginViewModel: LoginScreenViewModel = hiltViewModel()
            val isAdmin by loginViewModel.isAdmin.collectAsState()
            val pantryIngredientsViewModel: PantryIngredientsViewModel = hiltViewModel()
            LaunchedEffect(isAdmin) {pantryIngredientsViewModel.isAdmin=isAdmin }
            IngredientsScreen(pantryIngredientsViewModel = hiltViewModel(), navController = navController)
        }

        composable(Routes.MyIngredients.route) {
            MyIngredientsScreen(
                back = {
                    navController.navigateUp()
                },
                myIngredientsViewModel = hiltViewModel()
            )
        }
        composable(Routes.Shopping.route){
            val viewModel: ShoppingListViewModel = hiltViewModel()
            val pantryIngredientsViewModel: PantryIngredientsViewModel = hiltViewModel()

            ShoppingListScreen(
                shoppingListViewModel = viewModel,
                pantryIngredientsViewModel = pantryIngredientsViewModel,
                navController = navController

            )
        }
    }
}
