package com.lucasdev.apprecetas.ingredients.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lucasdev.apprecetas.general.ui.dropDownSelector.DropdownSelector
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffold
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel


//todo durante espera loading
@Composable
fun IngredientsScreen(
    pantryIngredientsViewModel: PantryIngredientsViewModel,
    navController: NavHostController
) {
    val pantryIngredients = pantryIngredientsViewModel.pantryIngredients.collectAsState()
    val availableIngredients = pantryIngredientsViewModel.allIngredients.collectAsState()
    val userName = pantryIngredientsViewModel.userName.collectAsState()
    val errorMessage = pantryIngredientsViewModel.errorMessage.collectAsState()
    val categories = pantryIngredientsViewModel.categories.collectAsState()
    val editingIngredient=pantryIngredientsViewModel.selectedIngredientToEdit.collectAsState()
    val groupedIngredients = pantryIngredientsViewModel.groupedIngredients.collectAsState()
    val loading = pantryIngredientsViewModel.isLoading.collectAsState()


    //todo cambiar por viewmodel
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    AppScaffold(
        userName = userName.value,
        onFabClick = {
            showDialog = true
        },
        navController = navController,
        content = { paddingValues ->
            if (loading.value) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@AppScaffold
            }
            Column(modifier = Modifier.padding(paddingValues)) {
                Text("Pantalla de Ingredientes", modifier = Modifier.padding(16.dp))

                if (pantryIngredients.value.isEmpty()) {
                    Text("Tu despensa esta vacía", modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        groupedIngredients.value.forEach { section ->
                            item {
                                Text(
                                    text = section.category,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                                )
                            }
                            items(section.ingredients.chunked(3)) { rowItems ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { ingredient ->
                                        ItemIngredient(
                                            ingredient = ingredient,
                                            onLongPress = {
                                                pantryIngredientsViewModel.setSelectedIngredientToEdit(it)
                                                showEditDialog = true
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
                if (showDialog) {
                    AddPantryIngredientDialog(
                        categories = categories.value,
                        availableIngredients = availableIngredients.value,
                        onDismiss = { showDialog = false },
                        errorMessage = errorMessage.value,
                        onConfirm = { ingredient, quantity ->
                            pantryIngredientsViewModel.addOrUpdateIngredientInPantry(ingredient, quantity)
                            showDialog = false
                        }
                    )
                }
                if (showEditDialog && editingIngredient.value != null) {
                    EditPantryIngredientDialog(
                        ingredient = editingIngredient.value!!,
                        onDismiss = {
                            showEditDialog = false
                            pantryIngredientsViewModel.closeEditDialog()
                        },
                        onDelete = {
                            showDeleteDialog = true
                            showEditDialog = false
                        },
                        onSave = { updated ->
                            pantryIngredientsViewModel.updatedIngredientInPantry(updated)
                            showEditDialog = false
                        }
                    )
                }
               if (showDeleteDialog && editingIngredient.value != null) {
                   ConfirmDeleteDialog(
                       ingredient = editingIngredient.value!!,
                       onConfirm = {
                           pantryIngredientsViewModel.confirmDeleteSelectedIngredientInPantry()
                           showDeleteDialog = false
                       },
                       onDismiss = { showDeleteDialog = false }
                   )
               }
            }
        }
    )
}

@Composable
fun ItemIngredient(
    ingredient: PantryIngredientModel,
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: (PantryIngredientModel) -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() },
                    onLongPress = { onLongPress(ingredient) })
            }
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${ingredient.quantity} ${ingredient.unit.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

        }
    }
}
//todo gestionar error
@Composable
fun AddPantryIngredientDialog(
    categories: List<CategoryModel>,
    availableIngredients: List<IngredientModel>,
    onDismiss: () -> Unit,
    onConfirm: (IngredientModel, Double) -> Unit,
    errorMessage: String?,
) {
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }
    var query by remember { mutableStateOf("") }
    var selectedIngredient by remember { mutableStateOf<IngredientModel?>(null) }
    var quantity by remember { mutableStateOf("") }

    val filteredIngredients = availableIngredients.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
                it.name.contains(query, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir ingrediente") },
        text = {
            Column {


                DropdownSelector(
                    label = "Categoría",
                    options = categories,
                    selected = selectedCategory ?: categories.firstOrNull() ?: CategoryModel(""),
                    onSelected = {
                        selectedCategory = it
                        selectedIngredient = null
                        query = ""
                    },
                    labelMapper = { it.name }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        selectedIngredient = null
                    },
                    label = { Text("Buscar ingrediente") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedCategory != null
                )

                if (selectedCategory != null && selectedIngredient == null && query.isNotBlank()) {
                    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                        items(filteredIngredients) { ingredient ->
                            Text(
                                text = "${ingredient.name} (${ingredient.unit.name})",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedIngredient = ingredient
                                        query = ingredient.name
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedIngredient != null
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "¿No encuentras el ingrediente? Regístralo en 'Mis ingredientes'",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val qty = quantity.toDoubleOrNull()
                    if (selectedIngredient != null && qty != null && qty > 0) {
                        onConfirm(selectedIngredient!!, qty)
                    }
                },
                enabled = selectedIngredient != null && quantity.toDoubleOrNull() != null
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },

    )
}

@Composable
fun EditPantryIngredientDialog(
    ingredient: PantryIngredientModel,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (PantryIngredientModel) -> Unit
) {
    var quantity by remember { mutableStateOf(ingredient.quantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar ingrediente") },
        text = {
            Column {
                Text("Nombre: ${ingredient.name}")
                Text("Categoría: ${ingredient.category.name}")
                Text("Unidad: ${ingredient.unit.name}")
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updated = ingredient.copy(
                    quantity = quantity.toDoubleOrNull() ?: ingredient.quantity
                )
                onSave(updated)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Text("Borrar")
                }
            }
        }
    )
}

@Composable
fun ConfirmDeleteDialog(
    ingredient: PantryIngredientModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar eliminación") },
        text = {
            Column {
                Text("¿Seguro de que quieres eliminar el siguiente ingrediente?")
                Spacer(modifier = Modifier.height(8.dp))
                Text("• ${ingredient.name}")
                Text("• Cantidad: ${ingredient.quantity} ${ingredient.unit.name}")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Borrar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}




