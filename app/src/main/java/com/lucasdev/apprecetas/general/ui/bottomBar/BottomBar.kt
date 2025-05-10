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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.ui.navigation.Routes

//TODO conseguir que los iconos se puedan clickar
val orange = Color(0xFFFFA500)
@Composable
fun BottomBarNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedIndex = when (currentRoute) {
        Routes.Ingredients.route -> 0
        Routes.Recepies.route -> 1
        Routes.Shopping.route -> 2
        else -> -1
    }

    NavigationBar(containerColor = Color.Red) {
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
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray
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
                if (currentRoute != Routes.Recepies.route) {
                    navController.navigate(Routes.Recepies.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                indicatorColor = orange,
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray
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
                unselectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.Gray
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