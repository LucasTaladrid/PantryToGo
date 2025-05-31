package com.lucasdev.apprecetas.general.ui.appButtons

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.lucasdev.apprecetas.R

/**
 * Floating Action Button (FAB) with an "add" icon.
 *
 * @param onFabClick Lambda function to be invoked when the FAB is clicked.
 */
@Composable
fun FAB(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = {onFabClick()},
        containerColor = colorResource(id= R.color.orange),
        contentColor = Color.Black,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }
}