package com.lucasdev.apprecetas.general.ui.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.appButtons.FAB
import kotlinx.coroutines.launch

/**
 * Scaffold composable with a top app bar that includes a back button,
 * an optional floating action button, and a snackbar host.
 *
 * @param title The title to display in the top app bar.
 * @param onBackClick Callback invoked when the back button in the top bar is clicked.
 * @param content The main screen content as a composable lambda, receives inner padding values.
 * @param onFabClick Optional callback for the floating action button click. If null, FAB is not shown.
 */
@Composable
fun AppScaffoldWithoutBottomBar(
    title: String,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    onFabClick: (() -> Unit)? = null,
    helpText: AnnotatedString? = null,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showHelpDialog = remember { mutableStateOf(false) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet() {
                MyModalDrawer {
                    coroutineScope.launch { drawerState.close() }
                }
            }
        }
    ) {
        if (showHelpDialog.value && helpText != null) {
            HelpDialog(
                helpText = helpText,
                onDismiss = { showHelpDialog.value = false }
            )
        }
        Scaffold(
            topBar = {
                TopBarWithBackButton(
                    title = title,
                    onBackClick = onBackClick,
                    onClickHelp = {showHelpDialog.value = true     }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {  onFabClick?.let {
                FAB(onFabClick = it)
            } },
            floatingActionButtonPosition = FabPosition.End,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(innerPadding)
                )
                content(innerPadding)
            }
        )
    }
}

/**
 * A top app bar with a title and a back navigation button.
 *
 * @param title The text to display as the title in the app bar.
 * @param onBackClick Callback invoked when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithBackButton(title: String, onBackClick: () -> Unit, onClickHelp: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(id= R.color.dark_orange),
            titleContentColor = Color.Black
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver"
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
    )
}
