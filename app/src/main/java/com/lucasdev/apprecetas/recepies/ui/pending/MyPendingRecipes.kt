package com.lucasdev.apprecetas.recepies.ui.pending

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffoldWithoutBottomBar
import com.lucasdev.apprecetas.general.ui.textApp.helpText.MyPendingRecipesHelp
import com.lucasdev.apprecetas.recepies.ui.common.RecipeItem


@Composable
fun MyPendingsRecipesScreen(myPendingRecipesViewModel: MyPendingRecipesViewModel, back: () -> Unit) {
    val recipes by myPendingRecipesViewModel.recipes.collectAsState()
    val isLoading by myPendingRecipesViewModel.isLoading.collectAsState()
    val errorMessage by myPendingRecipesViewModel.errorMessage.collectAsState()
    val favorites by myPendingRecipesViewModel.favoriteRecipes.collectAsState()
    val pending by myPendingRecipesViewModel.pendingRecipes.collectAsState()
    val isTogglingPending by myPendingRecipesViewModel.isTogglingPending.collectAsState()


    var expandedId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                myPendingRecipesViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(myPendingRecipesViewModel.snackbarMessage.collectAsState().value) {
        val message = myPendingRecipesViewModel.snackbarMessage.value
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            myPendingRecipesViewModel.clearSnackbarMessage()
        }
    }

    AppScaffoldWithoutBottomBar(
        title = "Mis recetas pendientes",
        onBackClick = back,
        helpText = MyPendingRecipesHelp.myPendingRecipesHelp,
        content = { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }

                    errorMessage != null -> {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        if (recipes.isEmpty()) {
                            Text(
                                text = MyPendingRecipesHelp.myPendingRecipesHelp,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopCenter)
                            )
                        } else {
                            LazyColumn(Modifier.fillMaxSize()) {
                                items(recipes) { recipe ->
                                    val isFavorite = favorites.any { it.id == recipe.id }
                                    val isPending = pending.any { it.id == recipe.id }
                                    val isExpanded = expandedId == recipe.id
                                    RecipeItem(
                                        recipe = recipe,
                                        isExpanded = isExpanded,
                                        onToggleExpand = {
                                            expandedId = if (isExpanded) null else recipe.id
                                        },
                                        isFavorite = isFavorite,
                                        isPending = isPending,
                                        onToggleFavorite = { myPendingRecipesViewModel.toggleFavorite(recipe) },
                                        onTogglePending = { myPendingRecipesViewModel.togglePending(recipe) },
                                        onMarkAsCooked = { myPendingRecipesViewModel.markAsCooked(recipe) }

                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }
                if (isTogglingPending) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .zIndex(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    )
}