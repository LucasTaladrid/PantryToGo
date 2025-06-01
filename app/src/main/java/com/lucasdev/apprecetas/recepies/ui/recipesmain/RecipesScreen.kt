package com.lucasdev.apprecetas.recepies.ui.recipesmain

import android.widget.Toast
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffold
import com.lucasdev.apprecetas.general.ui.textApp.helpText.RecipesMainHelp
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.ui.common.RecipeCreateDialog
import com.lucasdev.apprecetas.recepies.ui.common.RecipeItem

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
    val context = LocalContext.current

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
    LaunchedEffect(recipeViewModel.snackbarMessage.collectAsState().value) {
        val message = recipeViewModel.snackbarMessage.value
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            recipeViewModel.clearSnackbarMessage()
        }
    }

    AppScaffold(
        userName = userName,
        navController = navController,
        onFabClick = { showCreateRecipeDialog = true },
        helpText = RecipesMainHelp.recipesMainHelp,
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
                    LazyColumn(Modifier.fillMaxSize().padding(bottom = 80.dp)) {
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




