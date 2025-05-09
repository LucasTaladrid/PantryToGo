package com.lucasdev.apprecetas.general.ui.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lucasdev.apprecetas.general.ui.bottomBar.BottomBarNavigation
import com.lucasdev.apprecetas.general.ui.fabButton.FAB
import com.lucasdev.apprecetas.general.ui.topBar.TopBar
import com.lucasdev.apprecetas.ui.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun AppScaffold(
    userName: String,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
    onFabClick: (() -> Unit)? = null,
    onNavigate: (String) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color.Red) {
                MyModalDrawer { route ->
                    coroutineScope.launch {
                        drawerState.close()
                        onNavigate(route)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    userName = userName,
                    onClickIcon = {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Has pulsado $it") }
                    },
                    onClickDrawer = { coroutineScope.launch { drawerState.apply { if (isClosed) open() else close() } } })
            },

            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { BottomBarNavigation(onNavigate) },
            floatingActionButton = {
                onFabClick?.let {
                    FAB(onFabClick = it)
                }
            },
            floatingActionButtonPosition = FabPosition.End,

            ) { innerPadding ->
            content(innerPadding)

        }
    }
}

/*
TODO aquí se podrán encontrar los ajustes, mis recetas, mis recetas favoritas, mis recetas pendientes, mis recetas que voy hacer, las antiguas compras
*/
@Composable
fun MyModalDrawer(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Routes.MyIngredients.route to "Mis Ingredientes",
        "mis_recetas" to "Mis recetas",
        "recetas_pendientes" to "Recetas pendientes",
        "ultimas_compras" to "Últimas compras",
        "ajustes" to "Ajustes"
    )

    var selectedRoute by remember { mutableStateOf<String?>(null) }

    Column(Modifier.padding(8.dp)) {
        items.forEach { (route, label) ->
            val selected = selectedRoute == route
            NavigationDrawerItem(
                label = { Text(label) },
                selected = selected,
                onClick = {
                    selectedRoute = route
                    onNavigate(route)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color.Red,
                    unselectedContainerColor = Color.Magenta,
                    selectedTextColor = Color.White
                )
            )
        }
    }
}