package com.lucasdev.apprecetas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.lucasdev.apprecetas.ingredients.ui.pantry.PantryIngredientScreen
import androidx.navigation.compose.composable
import com.lucasdev.apprecetas.users.ui.login.LoginScreen
import com.lucasdev.apprecetas.users.ui.login.LoginScreenViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lucasdev.apprecetas.ingredients.ui.myingredients.MyIngredientsScreen
import com.lucasdev.apprecetas.ingredients.ui.pantry.PantryIngredientsViewModel
import com.lucasdev.apprecetas.recepies.ui.favorites.MyFavoritesRecipesScreen
import com.lucasdev.apprecetas.recepies.ui.myrecipes.MyRecipesScreen
import com.lucasdev.apprecetas.recepies.ui.pending.MyPendingsRecipesScreen
import com.lucasdev.apprecetas.recepies.ui.recipesmain.RecipeViewModel
import com.lucasdev.apprecetas.recepies.ui.recipesmain.RecipesScreen
import com.lucasdev.apprecetas.shopping.ui.myshoppinghistory.MyShoppingHistoryScreen
import com.lucasdev.apprecetas.shopping.ui.shoppingmain.ShoppingListScreen
import com.lucasdev.apprecetas.shopping.ui.shoppingmain.ShoppingListViewModel
import com.lucasdev.apprecetas.users.ui.logout.LogoutScreen
import com.lucasdev.apprecetas.users.ui.register.RegisterScreen
import com.lucasdev.apprecetas.users.ui.register.RegisterViewModel


@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination =Routes.Splash.route
    ) {
        composable(Routes.Splash.route) {
            LaunchedEffect(Unit) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    navController.navigate(Routes.Ingredients.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Routes.Login.route) {
            val viewModel: LoginScreenViewModel = hiltViewModel()
            LoginScreen(
                loginScreenViewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Ingredients.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
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
            LaunchedEffect(isAdmin) { pantryIngredientsViewModel.isAdmin = isAdmin }
            PantryIngredientScreen(
                pantryIngredientsViewModel = hiltViewModel(),
                navController = navController
            )
        }

        composable(Routes.Shopping.route) {
            val viewModel: ShoppingListViewModel = hiltViewModel()
            val pantryIngredientsViewModel: PantryIngredientsViewModel = hiltViewModel()

            ShoppingListScreen(
                shoppingListViewModel = viewModel,
                pantryIngredientsViewModel = pantryIngredientsViewModel,
                navController = navController

            )
        }
        composable(Routes.Recipes.route) {
            val viewModel: RecipeViewModel = hiltViewModel()
            RecipesScreen(
                recipeViewModel = viewModel,
                navController = navController
            )
        }
        composable(Routes.MyIngredients.route) {
            MyIngredientsScreen(
                back = {
                    navController.navigateUp()
                },
                myIngredientsViewModel = hiltViewModel()
            )
        }

        composable(Routes.MyShoppingHistory.route) {
            MyShoppingHistoryScreen(
                back = {
                    navController.navigateUp()
                },
                myShoppingHistoryViewModel = hiltViewModel()
            )

        }
        composable(Routes.MyRecipes.route) {
            MyRecipesScreen(
                back = {
                    navController.navigateUp()
                },
                myRecipesViewModel = hiltViewModel()
            )
        }
        composable(Routes.MyPendingRecipes.route) {
            MyPendingsRecipesScreen(
                back = {
                    navController.navigateUp()
                },
                myPendingRecipesViewModel = hiltViewModel()
            )
        }
        composable(Routes.MyFavouriteRecipes.route) {
            MyFavoritesRecipesScreen(
                back = {
                    navController.navigateUp()
                },
                myFavoritesRecipesViewModel = hiltViewModel()
            )
        }
        composable(Routes.Settings.route) {
            LogoutScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Ingredients.route) { inclusive = true }
                    }
                },
                logoutViewModel = hiltViewModel()
            )
        }
    }
}
