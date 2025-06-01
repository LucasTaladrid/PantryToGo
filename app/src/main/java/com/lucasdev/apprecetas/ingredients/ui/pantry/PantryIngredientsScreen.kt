package com.lucasdev.apprecetas.ingredients.ui.pantry

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.dialogs.AddIngredientWithQuantityDialog
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffold
import com.lucasdev.apprecetas.general.ui.textApp.helpText.PantryIngredientHelp
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel


//todo durante espera loading
@Composable
fun PantryIngredientScreen(
    pantryIngredientsViewModel: PantryIngredientsViewModel,
    navController: NavHostController
) {
    val pantryIngredients = pantryIngredientsViewModel.pantryIngredients.collectAsState()
    val availableIngredients = pantryIngredientsViewModel.allIngredients.collectAsState()
    val userName = pantryIngredientsViewModel.userName.collectAsState()
    val errorMessage = pantryIngredientsViewModel.errorMessage.collectAsState()
    val categories = pantryIngredientsViewModel.categories.collectAsState()
    val editingIngredient = pantryIngredientsViewModel.selectedIngredientToEdit.collectAsState()
    val groupedIngredients = pantryIngredientsViewModel.groupedIngredients.collectAsState()
    val loading = pantryIngredientsViewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                pantryIngredientsViewModel.refreshPantry()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(pantryIngredientsViewModel.snackbarMessage.collectAsState().value) {
        val message = pantryIngredientsViewModel.snackbarMessage.value
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            pantryIngredientsViewModel.clearSnackbarMessage()
        }
    }

    AppScaffold(
        userName = userName.value,
        helpText = PantryIngredientHelp.pantryHelp,
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
                Text(
                    text = "Despensa",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                if (pantryIngredients.value.isEmpty()) {
                    Text(
                        text = PantryIngredientHelp.pantryHelp,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
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
                                                pantryIngredientsViewModel.setSelectedIngredientToEdit(
                                                    it
                                                )
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
                    AddIngredientWithQuantityDialog(
                        title = "Añadir ingrediente a la lista",
                        categories = categories.value,
                        availableIngredients = availableIngredients.value,
                        onDismiss = { showDialog = false },
                        errorMessage = errorMessage.value,
                        onConfirm = { ingredient, quantity ->
                            pantryIngredientsViewModel.addOrUpdateIngredientInPantry(
                                ingredient,
                                quantity
                            )
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
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress(ingredient) }
                )
            }
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
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
            )
        }
    }
}

@Composable
fun EditPantryIngredientDialog(
    ingredient: PantryIngredientModel,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (PantryIngredientModel) -> Unit
) {
    var quantity by remember { mutableStateOf(ingredient.quantity.toString()) }
    val isQuantityValid = quantity.toDoubleOrNull() != null

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
            Button(
                onClick = {
                    val updated = ingredient.copy(
                        quantity = quantity.toDoubleOrNull() ?: ingredient.quantity
                    )
                    onSave(updated)
                },
                enabled = isQuantityValid,
                colors = ButtonColors(
                    containerColor = colorResource(R.color.orange),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                )
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
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
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
            ) { Text("Borrar") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
            ) { Text("Cancelar") }
        }
    )
}




