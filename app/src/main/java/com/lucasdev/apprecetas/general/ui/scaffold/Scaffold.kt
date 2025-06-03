package com.lucasdev.apprecetas.general.ui.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.bottomBar.BottomBarNavigation
import com.lucasdev.apprecetas.general.ui.appButtons.FAB
import com.lucasdev.apprecetas.general.ui.topBar.TopBar
import com.lucasdev.apprecetas.ui.navigation.Routes
import kotlinx.coroutines.launch

/**
 * Scaffold composable that provides a layout with a top bar,
 * bottom navigation bar, modal navigation drawer, and optional floating action button.
 *
 * @param userName The name of the current user to be displayed in the top bar.
 * @param modifier Modifier for styling the scaffold.
 * @param content The main content composable that receives the padding values.
 * @param onFabClick Optional callback invoked when the floating action button is clicked.
 * @param navController NavHostController to handle navigation between screens.
 */
@Composable
fun AppScaffold(
    userName: String,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
    onFabClick: (() -> Unit)? = null,
    navController: NavHostController,
    helpText: AnnotatedString? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showHelpDialog = remember { mutableStateOf(false) }

    if (showHelpDialog.value && helpText != null) {
        HelpDialog(
            helpText = helpText,
            onDismiss = { showHelpDialog.value = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = colorResource(id = R.color.orange)) {
                MyModalDrawer { route ->
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate(route)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    userName = userName,
                    onClickIcon = {/*todo in the future this will contain search */ },
                    onClickHelp = { showHelpDialog.value = true },
                    onClickDrawer = { coroutineScope.launch { drawerState.apply { if (isClosed) open() else close() } } })
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = { BottomBarNavigation(navController = navController) },
            floatingActionButton = {
                onFabClick?.let {
                    FAB(onFabClick = it)
                }
            },
            floatingActionButtonPosition = FabPosition.End,

            ) { innerPadding ->
            Box(
                modifier = Modifier
                    .background(Color.White)

            ) {
                content(innerPadding)
            }
        }
    }
}

/**
 * A modal navigation drawer displaying a list of navigation items.
 *
 * @param onNavigate Callback invoked when a navigation item is selected,
 *                   providing the route of the selected item.
 */
@Composable
fun MyModalDrawer(
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Routes.MyIngredients.route to "Mis Ingredientes",
        Routes.MyRecipes.route to "Mis recetas",
        Routes.MyPendingRecipes.route to "Recetas pendientes",
        Routes.MyFavouriteRecipes.route to "Recetas favoritas",
        Routes.MyShoppingHistory.route to "Últimas compras",
        Routes.Settings.route to "Ajustes"
    )

    var selectedRoute by remember { mutableStateOf<String?>(null) }

    Column(Modifier.padding(8.dp)) {
        items.forEachIndexed { index, (route, label) ->
            val selected = selectedRoute == route
            NavigationDrawerItem(
                label = {
                    Text(
                        text = label,
                        color = Color.Black
                    )
                },
                selected = selected,
                onClick = {
                    selectedRoute = route
                    onNavigate(route)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = colorResource(id = R.color.orange),
                    unselectedContainerColor = colorResource(id = R.color.orange),
                    selectedTextColor = Color.White
                )
            )
            if (index < items.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun HelpDialog(
    helpText:
    AnnotatedString?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
            ) {
                Text("Entendido")
            }
        },
        title = { Text("Ayuda") },
        text = { Text(helpText!!) }
    )
}
