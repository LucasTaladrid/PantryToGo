package com.lucasdev.apprecetas.recepies.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.appTextFields.AppOutlinedTextField
import com.lucasdev.apprecetas.general.ui.dialogs.AddIngredientWithQuantityDialog
import com.lucasdev.apprecetas.general.ui.dropDownSelector.DropdownSelector
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeItem(
    recipe: RecipeModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    isPending: Boolean,
    onToggleFavorite: () -> Unit,
    onTogglePending: () -> Unit,
    onMarkAsCooked: (() -> Unit)? = null
) {


    Column(
        modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onToggleExpand)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { onToggleFavorite() }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) colorResource(id = R.color.personal_pink) else LocalContentColor.current
                )
            }

            IconButton(onClick = { onTogglePending() }) {
                Icon(
                    imageVector = if (isPending) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Pendiente",
                    tint = if (isPending) colorResource(id = R.color.dark_orange) else LocalContentColor.current
                )
            }

        }

        AnimatedVisibility(visible = isExpanded) {
            Column(Modifier.padding(top = 12.dp)) {
                Spacer(Modifier.height(8.dp))
                Text("Ingredientes:", style = MaterialTheme.typography.titleSmall)
                recipe.ingredients.forEach { ing ->
                    Text("- ${ing.name}: ${ing.quantity} ${ing.unit.name}")
                }

                Spacer(Modifier.height(12.dp))
                Text("Pasos:", style = MaterialTheme.typography.titleSmall)
                recipe.steps.forEachIndexed { idx, step ->
                    Text("${idx + 1}. $step")
                    Spacer(Modifier.height(4.dp))
                }
                if (isPending && onMarkAsCooked != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = onMarkAsCooked, colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange),contentColor = Color.White)) {
                            Text("Marcar como cocinada")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeItemPress(
    recipe: RecipeModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    isPending: Boolean,
    onToggleFavorite: () -> Unit,
    onTogglePending: () -> Unit,
    onLongPress: () -> Unit
) {


    Column(
        modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onToggleExpand, onLongClick = onLongPress)
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { onToggleFavorite() }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color(0xFFE91E63) else LocalContentColor.current
                )
            }

            IconButton(onClick = { onTogglePending() }) {
                Icon(
                    imageVector = if (isPending) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Pendiente",
                    tint = if (isPending) Color(0xFFFF9800) else LocalContentColor.current
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(Modifier.padding(top = 12.dp)) {
                Spacer(Modifier.height(8.dp))
                Text("Ingredientes:", style = MaterialTheme.typography.titleSmall)
                recipe.ingredients.forEach { ing ->
                    Text("- ${ing.name}: ${ing.quantity} ${ing.unit.name}")
                }

                Spacer(Modifier.height(12.dp))
                Text("Pasos:", style = MaterialTheme.typography.titleSmall)
                recipe.steps.forEachIndexed { idx, step ->
                    Text("${idx + 1}. $step")
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun RecipeCreateDialog(
    initialRecipe: RecipeModel? = null,
    categories: List<CategoryModel>,
    ingredients: List<IngredientModel>,
    errorMessage: String?,
    isSaving: Boolean,
    onAddIngredient: (PantryIngredientModel) -> Unit,
    onRemoveIngredient: (PantryIngredientModel) -> Unit,
    onCreateRecipe: (String, List<PantryIngredientModel>, String, () -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var recipeName by remember { mutableStateOf(initialRecipe?.name ?: "") }
    var steps by remember { mutableStateOf(initialRecipe?.steps?.joinToString("\n") ?: "") }
    val recipeIngredients = remember {
        mutableStateListOf<PantryIngredientModel>().apply {
            initialRecipe?.ingredients?.let { addAll(it) }
        }
    }

    var showIngredientDialog by remember { mutableStateOf(false) }
    var ingredientToEdit by remember { mutableStateOf<PantryIngredientModel?>(null) }
    var newQuantity by remember { mutableStateOf("") }

    if (showIngredientDialog) {
        AddIngredientWithQuantityDialog(
            title = "Añadir ingrediente a la receta",
            categories = categories,
            availableIngredients = ingredients,
            existingIngredients = recipeIngredients,
            onDismiss = { showIngredientDialog = false },
            onConfirm = { ingredient, quantity ->
                val item = PantryIngredientModel(
                    ingredientId = ingredient.id,
                    name = ingredient.name,
                    quantity = quantity,
                    unit = ingredient.unit,
                    category = ingredient.category
                )
                recipeIngredients.add(item)
                onAddIngredient(item)
                showIngredientDialog = false
            },
            errorMessage = null
        )
    }

    if (ingredientToEdit != null) {
        AlertDialog(
            onDismissRequest = { ingredientToEdit = null },
            title = {
                Column {
                    Text("Editar cantidad")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${ingredientToEdit?.name} (${ingredientToEdit?.unit?.name})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            },
            text = {
                OutlinedTextField(
                    value = newQuantity,
                    onValueChange = { newQuantity = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updated =
                            ingredientToEdit?.copy(quantity = newQuantity.toDoubleOrNull() ?: 0.0)
                        if (updated != null) {
                            recipeIngredients.remove(ingredientToEdit)
                            recipeIngredients.add(updated)
                        }
                        ingredientToEdit = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { ingredientToEdit = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Cancelar")
                }
                TextButton(
                    onClick = {
                        onCreateRecipe(recipeName, recipeIngredients.toList(), steps, onDismiss)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange)),
                    enabled = recipeName.isNotBlank() && recipeIngredients.isNotEmpty() && steps.isNotBlank() && !isSaving
                ) {
                    Text(if (isSaving) "Guardando..." else if (initialRecipe != null) "Actualizar receta" else "Crear receta")
                }
            }
        },
        title = {
            Text(if (initialRecipe != null) "Editar receta" else "Crear receta")
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = recipeName,
                    onValueChange = { recipeName = it },
                    label = { Text("Nombre de la receta") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Pasos de preparación") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Text("Ingredientes", style = MaterialTheme.typography.titleMedium)

                recipeIngredients.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            "${item.name}: ${item.quantity} ${item.unit.name}",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            ingredientToEdit = item
                            newQuantity = item.quantity.toString()
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar cantidad")
                        }
                        IconButton(onClick = {
                            recipeIngredients.remove(item)
                            onRemoveIngredient(item)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { showIngredientDialog = true },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                ) {
                    Text("Añadir ingredientes")
                }

                if (!errorMessage.isNullOrEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    )
}




