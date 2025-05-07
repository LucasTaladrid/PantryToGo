package com.lucasdev.apprecetas.general.ui.topBar

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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

//todo, decidir si quiero un menú, añadir el nombre del usuario, decidir si quiero hacer búsquedas, permitir limpiar la barra de búsqueda con el botón que ahora es cerrar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(userName:String,onClickIcon: (String) -> Unit,onClickDrawer:()->Unit) {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = { Text("Hola $userName") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Red,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = { onClickDrawer()}) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú"
                )
            }
        },
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
    )
}