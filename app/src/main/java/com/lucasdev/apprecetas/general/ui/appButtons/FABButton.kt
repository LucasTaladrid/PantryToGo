package com.lucasdev.apprecetas.general.ui.appButtons

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//todo, cambiar el color, añadir funcionalidad, en las recetas, añade recetas, en la de ingredientes añade ingredientes.
@Composable
fun FAB(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = {onFabClick()},
        containerColor = Color.Yellow,
        contentColor = Color.Black,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }
}