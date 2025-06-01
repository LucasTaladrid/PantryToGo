package com.lucasdev.apprecetas.users.ui.logout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.appButtons.AppButton
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffoldWithoutBottomBar
import kotlinx.coroutines.launch

@Composable
fun LogoutScreen(
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    logoutViewModel: LogoutViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    AppScaffoldWithoutBottomBar(
        title = "Ajustes",
        onBackClick = onBackClick,
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(),
                contentAlignment = Alignment.Center
            ) {
                AppButton(
                    text = if (isLoading) "Cerrando sesión..." else "Salir",
                    onClick = {
                        if (!isLoading) {
                            isLoading = true
                            logoutViewModel.logout(
                                onComplete = {
                                    isLoading = false
                                    onNavigateToLogin()
                                },
                                onError = { error ->
                                    isLoading = false
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error al cerrar sesión: ${error.message}")
                                    }
                                }
                            )
                        }
                    },
                    enabled = !isLoading
                )
            }
        }
    )
}

