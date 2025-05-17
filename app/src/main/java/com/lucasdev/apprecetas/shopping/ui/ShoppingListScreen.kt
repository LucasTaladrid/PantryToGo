package com.lucasdev.apprecetas.shopping.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.lucasdev.apprecetas.general.ui.appButtons.AppButton
import com.lucasdev.apprecetas.general.ui.appTextFields.AppOutlinedTextField
import com.lucasdev.apprecetas.general.ui.appTextFields.AppTextField
import com.lucasdev.apprecetas.general.ui.dropDownSelector.DropdownSelector
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffold
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.ui.PantryIngredientsViewModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel


@Composable
fun ShoppingListScreen(
    shoppingListViewModel: ShoppingListViewModel,
    pantryIngredientsViewModel: PantryIngredientsViewModel,
    navController: NavHostController
) {
    val userName = shoppingListViewModel.userName.collectAsState()
    val loading = shoppingListViewModel.isLoading.collectAsState()
    val error = shoppingListViewModel.errorMessage.collectAsState()
    val sections = shoppingListViewModel.shoppingItemSections.collectAsState()
    val ingredients = shoppingListViewModel.ingredients.collectAsState()
    val categories = shoppingListViewModel.categories.collectAsState()
    val activeListId = shoppingListViewModel.activeListId.collectAsState().value
    val lifecycleOwner = LocalLifecycleOwner.current

    //todo cambiar por viewmodel
    var selectedItem by remember { mutableStateOf<ShoppingIngredientModel?>(null) }
    var showEditDeleteChoiceDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                shoppingListViewModel.refreshShoppingList()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppScaffold(
        userName = userName.value,
        onFabClick = { showAddDialog = true },
        navController = navController,
        content = { padding ->

            if (loading.value) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@AppScaffold
            }

            if (error.value != null) {
                Text(
                    text = error.value ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text(
                    text = "Lista de la Compra",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                if (sections.value.any { it.items.isNotEmpty() }) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        sections.value.forEach { section ->
                            item {
                                Text(
                                    text = section.category,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                )
                            }

                            items(section.items) { item ->
                                ShoppingItemRow(
                                    item = item,
                                    onCheckedChange = { isChecked ->
                                        if (activeListId != null) {
                                            shoppingListViewModel.toggleItemChecked(
                                                activeListId,
                                                item.id,
                                                isChecked
                                            )
                                        }
                                    },
                                    onLongPress = {
                                        selectedItem = it
                                        showEditDeleteChoiceDialog = true
                                    }
                                )
                            }
                        }
                    }
                    AppButton(
                        onClick = {
                            shoppingListViewModel.moveCheckedItemsToPantry(pantryIngredientsViewModel)
                        },
                        text = "Finalizar Compra",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(16.dp)
                    )

                } else {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                                append("¡Hola, está es tú lista de la compra!\n\n")
                            }
                            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                                append("Aquí podrás gestionar fácilmente los ingredientes que necesitas comprar.\n\n")
                                append("Usa el botón ➕ para añadir un nuevo ingrediente a tu lista.\n")
                                append("Marca los ingredientes que ya hayas comprado.\n")
                                append(" Cuando termines, pulsa 'Finalizar Compra' y los ingredientes marcados se moverán automáticamente a tu despensa.")
                                append("Puedes consular las últimas compras en la pantalla 'Últimas compras'.")

                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                }
            }

            if (showAddDialog) {
                AddShoppingListIngredientDialog(
                    categories = categories.value,
                    availableIngredients = ingredients.value,
                    onDismiss = { showAddDialog = false },
                    errorMessage = error.value,
                    onConfirm = { ingredient, quantity ->
                        shoppingListViewModel.addIngredientToList(ingredient, quantity)
                        showAddDialog = false
                    }
                )
            }
            if (showEditDeleteChoiceDialog && selectedItem != null) {
                AlertDialog(
                    onDismissRequest = { showEditDeleteChoiceDialog = false },
                    title = { Text("¿Qué deseas hacer?") },
                    text = {
                        Text("Selecciona una acción para:\n\n • ${selectedItem!!.name}\n • Cantidad: ${selectedItem!!.quantity} ${selectedItem!!.unit.name}")
                    },
                    confirmButton = {

                        TextButton(onClick = {
                            showEditDeleteChoiceDialog = false
                            showEditDialog = true
                        }) {
                            Text("Modificar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showEditDeleteChoiceDialog = false
                            showDeleteConfirmDialog = true
                        }) {
                            Text("Eliminar")
                        }
                    }
                )
            }
            if (showEditDialog && selectedItem != null) {
                EditShoppingItemDialog(
                    item = selectedItem!!,
                    onDismiss = { showEditDialog = false },
                    onSave = { updated ->
                        shoppingListViewModel.updateItem(updated)
                        showEditDialog = false
                    },
                    onRequestDelete = {
                        showEditDialog = false
                        showDeleteConfirmDialog = true
                    }
                )
            }

            if (showDeleteConfirmDialog && selectedItem != null) {
                ConfirmDeleteDialog(
                    ingredient = selectedItem!!,
                    onConfirm = {
                        shoppingListViewModel.deleteItem(selectedItem!!)
                        showDeleteConfirmDialog = false
                    },
                    onDismiss = { showDeleteConfirmDialog = false }
                )
            }
        }
    )
}


@Composable
fun ShoppingItemRow(
    item: ShoppingIngredientModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onLongPress: (ShoppingIngredientModel) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress(item) }
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${item.quantity} ${item.unit.name}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AddShoppingListIngredientDialog(
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

    val showIngredientSearch = selectedCategory != null
    val showQuantityInput = selectedIngredient != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir ingrediente a la lista") },
        text = {
            Column {
                // Dropdown con opción inicial no seleccionable
                DropdownSelector(
                    label = "Categoría",
                    options = listOf(null) + categories,
                    selected = selectedCategory,
                    onSelected = {
                        if (it != null) {
                            selectedCategory = it
                            selectedIngredient = null
                            query = ""
                        }
                    },
                    labelMapper = { it?.name ?: "Selecciona una categoría" }
                )

                Spacer(Modifier.height(12.dp))

                // Buscador de ingredientes
                AppOutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        selectedIngredient = null
                    },
                    label = "Buscar ingrediente",
                    enabled = showIngredientSearch
                )

                if (showIngredientSearch && selectedIngredient == null && query.isNotBlank()) {
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

                // Cantidad
                AppOutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "Cantidad",
                    keyboardType = KeyboardType.Number,
                    enabled = showQuantityInput
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
            Row {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.weight(1f))
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
            }
        }
    )
}


@Composable
fun EditShoppingItemDialog(
    item: ShoppingIngredientModel,
    onDismiss: () -> Unit,
    onRequestDelete: () -> Unit,
    onSave: (ShoppingIngredientModel) -> Unit
) {
    var quantity by remember { mutableStateOf(item.quantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar ítem") },
        text = {
            Column {
                Text("Nombre: ${item.name}")
                Text("Categoría: ${item.category.name}")
                Text("Unidad: ${item.unit.name}")
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
            Button (

                onClick = {
                    val updated = item.copy(
                        quantity = quantity.toDoubleOrNull() ?: item.quantity
                    )
                    onSave(updated)
                }
            ){
                Text("Confirmar")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onRequestDelete) {
                    Text("Borrar")
                }
            }
        }
    )
}
@Composable
fun ConfirmDeleteDialog(
    ingredient: ShoppingIngredientModel,
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



