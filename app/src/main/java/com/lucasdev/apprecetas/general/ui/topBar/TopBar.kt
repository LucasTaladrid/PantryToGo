package com.lucasdev.apprecetas.general.ui.topBar

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.lucasdev.apprecetas.R

//todo Cambiar colors, añadir dialogo de ayuda para mostar el tutorial
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    userName: String,
    onClickIcon: (String) -> Unit,
    onClickDrawer: () -> Unit,
    onClickHelp: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = { Text("Hola, $userName") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id=R.color.dark_orange),
            titleContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = { onClickDrawer() }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú"
                )
            }
        },
        actions = {
            onClickHelp?.let {
                IconButton(onClick = { onClickHelp() }) {
                    Icon(
                        imageVector = Icons.Filled.HelpOutline,
                        contentDescription = "Ayuda"
                    )
                }
            }
        }
        /*
        todo add search and close features in the future
        actions = {
            IconButton(onClick = { onClickIcon("Buscar") }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "buscar"
                )
            }
            IconButton(onClick = { onClickIcon("Cerrar") }) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "cerrar"
                )
            }
        },

         */
    )
}
