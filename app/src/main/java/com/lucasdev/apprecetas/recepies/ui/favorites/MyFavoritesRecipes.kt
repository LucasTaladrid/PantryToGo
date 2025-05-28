package com.lucasdev.apprecetas.recepies.ui.favorites

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffoldWithoutBottomBar
import com.lucasdev.apprecetas.recepies.ui.common.RecipeItem
import com.lucasdev.apprecetas.recepies.ui.common.RecipeItemPress


@Composable
fun MyFavoritesRecipesScreen(myFavoritesRecipesViewModel: MyFavoritesRecipesViewModel, back: () -> Unit) {
    val recipes by myFavoritesRecipesViewModel.favoriteRecipes.collectAsState()
    val isLoading by myFavoritesRecipesViewModel.isLoading.collectAsState()
    val errorMessage by myFavoritesRecipesViewModel.errorMessage.collectAsState()
    val favorites by myFavoritesRecipesViewModel.favoriteRecipes.collectAsState()
    val pending by myFavoritesRecipesViewModel.pendingRecipes.collectAsState()

    var expandedId by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                myFavoritesRecipesViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffoldWithoutBottomBar(
        title = "Mis recetas favoritas",
        onBackClick = back,
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
                                text = "¡Todavía no tienes recetas favoritas! Vete a la pantalla de recetas y selecciona las que quieras añadir.",
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
                                        onToggleFavorite = { myFavoritesRecipesViewModel.toggleFavorite(recipe) },
                                        onTogglePending = { myFavoritesRecipesViewModel.togglePending(recipe) },
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}