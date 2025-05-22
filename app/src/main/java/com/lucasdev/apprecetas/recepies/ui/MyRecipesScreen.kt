package com.lucasdev.apprecetas.recepies.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel


@Composable
fun MyRecipesScreen(myRecipesViewModel: MyRecipesScreenViewModel, back: () -> Unit) {
    val recipes by myRecipesViewModel.recipes.collectAsState()
    val isLoading by myRecipesViewModel.isLoading.collectAsState()
    val errorMessage by myRecipesViewModel.errorMessage.collectAsState()
    val categories by myRecipesViewModel.categories.collectAsState()
    val ingredients by myRecipesViewModel.allIngredients.collectAsState()
    val isSaving by myRecipesViewModel.isSaving.collectAsState()
    val favorites by myRecipesViewModel.favoriteRecipes.collectAsState()
    val pending by myRecipesViewModel.pendingRecipes.collectAsState()
    val showOptionsDialog by myRecipesViewModel.showOptionsDialog.collectAsState()
    val showEditDialog by myRecipesViewModel.showEditDialog.collectAsState()
    val showDeleteConfirmation by myRecipesViewModel.showDeleteConfirmation.collectAsState()
    val selectedRecipe by myRecipesViewModel.selectedRecipe.collectAsState()


    var expandedId by remember { mutableStateOf<String?>(null) }
    var showCreateRecipeDialog by remember { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                myRecipesViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffoldWithoutBottomBar(
        title = "Mis recetas",
        onBackClick = back,
        onFabClick = { showCreateRecipeDialog = true },
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
                                text = "¡Todavía no tienes recetas! Usa el botón ➕ para crear una.",
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
                                    RecipeItemPress(
                                        recipe = recipe,
                                        isExpanded = isExpanded,
                                        onToggleExpand = {
                                            expandedId = if (isExpanded) null else recipe.id
                                        },
                                        isFavorite = isFavorite,
                                        isPending = isPending,
                                        onToggleFavorite = { myRecipesViewModel.toggleFavorite(recipe) },
                                        onTogglePending = { myRecipesViewModel.togglePending(recipe) },
                                        onLongPress = { myRecipesViewModel.onRecipeLongClick(recipe) }
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }

                if (showCreateRecipeDialog) {
                    RecipeCreateDialog(
                        categories = categories,
                        ingredients = ingredients,
                        errorMessage = errorMessage,
                        isSaving = isSaving,
                        onAddIngredient = { myRecipesViewModel.addOrUpdateIngredient(it) },
                        onRemoveIngredient = { myRecipesViewModel.removeIngredient(it) },
                        onCreateRecipe = { name, ingredients, steps, onSuccess ->
                            myRecipesViewModel.onNameChange(name)
                            myRecipesViewModel.onStepsChange(steps)
                            myRecipesViewModel.createRecipe(
                                RecipeModel(
                                    name = name,
                                    steps = steps.split("\n"),
                                    ingredients = ingredients
                                ),
                                onSuccess
                            )
                        },
                        onDismiss = { showCreateRecipeDialog = false }
                    )
                }
                if (showOptionsDialog && selectedRecipe != null) {
                    OptionsDialog(
                        onDismiss = { myRecipesViewModel.clearDialogs() },
                        onEdit = { myRecipesViewModel.editSelectedRecipe() },
                        onDelete = { myRecipesViewModel.confirmDeleteSelectedRecipe() }
                    )

                }
                if (showDeleteConfirmation && selectedRecipe != null) {
                   ConfirmDeleteDialog(
                       onDismiss = { myRecipesViewModel.clearDialogs() },
                       onConfirm = { myRecipesViewModel.deleteSelectedRecipe() }
                   )
                }
                if (showEditDialog && selectedRecipe != null) {
                    RecipeCreateDialog(
                        initialRecipe = selectedRecipe,
                        categories = categories,
                        ingredients = ingredients,
                        errorMessage = errorMessage,
                        isSaving = isSaving,
                        onAddIngredient = myRecipesViewModel::addOrUpdateIngredient,
                        onRemoveIngredient = myRecipesViewModel::removeIngredient,
                        onCreateRecipe = { name, ingredients, steps, onSuccess ->
                            // A futuro reemplazar por updateRecipe()
                            val recipe = selectedRecipe!!.copy(
                                name = name,
                                ingredients = ingredients,
                                steps = steps.split("\n")
                            )
                            myRecipesViewModel.createRecipe(recipe, onSuccess)
                        },
                        onDismiss = {
                            myRecipesViewModel.clearDialogs()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun OptionsDialog(onDismiss: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit){
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Acciones sobre la receta") },
        text = { Text("¿Qué quieres hacer con esta receta?") },
        confirmButton = {
            TextButton(onClick = { onEdit() }) {
                Text("Modificar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDelete() }) {
                Text("Eliminar")
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = {onDismiss() },
        title = { Text("Eliminar receta") },
        text = { Text("¿Estás seguro de que quieres eliminar esta receta?") },
        confirmButton = {
            TextButton(onClick = {onConfirm() }) {
                Text("Sí, eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = {onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}






