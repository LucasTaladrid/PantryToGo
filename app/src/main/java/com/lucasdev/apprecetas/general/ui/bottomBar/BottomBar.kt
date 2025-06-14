package com.lucasdev.apprecetas.general.ui.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.ui.navigation.Routes


val orange = Color(0xFFFFA500)
/**
 * Bottom navigation bar with three navigation items: Ingredients, Recipes, and Shopping.
 *
 * The navigation bar updates the selected item based on the current route from the [navController].
 * Clicking on an item navigates to the corresponding route, saving and restoring navigation state.
 *
 * @param navController The [NavHostController] used to control navigation between destinations.
 */
@Composable
fun BottomBarNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedIndex = when (currentRoute) {
        Routes.Ingredients.route -> 0
        Routes.Recipes.route -> 1
        Routes.Shopping.route -> 2
        else -> -1
    }

    NavigationBar(containerColor = colorResource(id=R.color.dark_orange)) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = {
                if (currentRoute != Routes.Ingredients.route) {
                    navController.navigate(Routes.Ingredients.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                indicatorColor = orange,
                selectedTextColor = Color.Black,
                unselectedTextColor = Color.White
            ),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.outline_dining_24),
                    contentDescription = "Despensa"
                )
            },
            label = { Text(text = "Despensa") }
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = {
                if (currentRoute != Routes.Recipes.route) {
                    navController.navigate(Routes.Recipes.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                indicatorColor = orange,
                selectedTextColor = Color.Black,
                unselectedTextColor = Color.White
            ),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_book_24),
                    contentDescription = "Recetas"
                )
            },
            label = { Text(text = "Recetas") }
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = {
                if (currentRoute != Routes.Shopping.route) {
                    navController.navigate(Routes.Shopping.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                indicatorColor = orange,
                selectedTextColor = Color.Black,
                unselectedTextColor = Color.White
            ),
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Compra"
                )
            },
            label = { Text(text = "Compra") }
        )
    }
}