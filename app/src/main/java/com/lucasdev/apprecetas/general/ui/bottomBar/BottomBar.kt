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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

import com.lucasdev.apprecetas.R
//TODO conseguir que los iconos se puedan clickar
val orange = Color(0xFFFFA500)
@Preview(showBackground = true)
@Composable
fun BottomBarNavigation() {
    var index by remember { mutableStateOf(0) }
    NavigationBar(containerColor = Color.Red) {

        NavigationBarItem(

            selected = index == 0,
            onClick = { index = 0 },
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
                    contentDescription = "Despensa",

                )
            },
            label = { Text(text = "Despensa") }
        )
        NavigationBarItem(
            selected = index == 1,
            onClick = { index = 1 },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                indicatorColor = Color.Red,
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
            selected = index == 2,
            onClick = { index = 2 },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                indicatorColor = Color.Red,
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