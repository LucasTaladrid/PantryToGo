package com.lucasdev.apprecetas.recepies.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.lucasdev.apprecetas.general.ui.appTextFields.AppOutlinedTextField
import com.lucasdev.apprecetas.general.ui.dropDownSelector.DropdownSelector
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffold
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel


@Composable
fun RecipesScreen(
    recipeViewModel: RecipeViewModel,
    navController: NavHostController
) {
    val userName by recipeViewModel.userName.collectAsState()
    val recipes by recipeViewModel.recipes.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val errorMessage by recipeViewModel.errorMessage.collectAsState()
    val categories by recipeViewModel.categories.collectAsState()
    val ingredients by recipeViewModel.allIngredients.collectAsState()
    val isSaving by recipeViewModel.isSaving.collectAsState()
    val favorites by recipeViewModel.favoriteRecipes.collectAsState()
    val pending by recipeViewModel.pendingRecipes.collectAsState()

    // Estado UI
    var expandedId by remember { mutableStateOf<String?>(null) }
    var showCreateRecipeDialog by remember { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                recipeViewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffold(
        userName = userName,
        navController = navController,
        onFabClick = { showCreateRecipeDialog = true },
        content = { padding ->

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else if (errorMessage != null) {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(recipes) { recipe ->
                            val isFavorite = favorites.any{it.id==recipe.id}
                            val isPending = pending.any{it.id==recipe.id}
                            val isExpanded = expandedId  == recipe.id
                            RecipeItem(
                                recipe = recipe,
                                isExpanded = isExpanded,
                                onToggleExpand = {
                                    expandedId = if (isExpanded) null else recipe.id

                                },
                                isFavorite = isFavorite,
                                isPending = isPending,
                                onToggleFavorite = { recipeViewModel.toggleFavorite(recipe) },
                                onTogglePending = { recipeViewModel.togglePending(recipe) }
                            )
                            Divider()
                        }
                    }


                }
                if (showCreateRecipeDialog) {
                    RecipeCreateDialog(
                        categories = categories,
                        ingredients = ingredients,
                        errorMessage = errorMessage,
                        isSaving = isSaving,
                        onAddIngredient = { recipeViewModel.addOrUpdateIngredient(it) },
                        onRemoveIngredient = { recipeViewModel.removeIngredient(it) },
                        onCreateRecipe = { name, ingredients, steps, onSuccess ->
                            recipeViewModel.onNameChange(name)
                            recipeViewModel.onStepsChange(steps)
                            recipeViewModel.createRecipe(
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
            }
        }
    )
}




