package com.lucasdev.apprecetas.ingredients.ui.myingredients

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.appButtons.AppButton
import com.lucasdev.apprecetas.general.ui.dropDownSelector.DropdownSelector
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffoldWithoutBottomBar
import com.lucasdev.apprecetas.general.ui.textApp.helpText.MyIngredientsHelp
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyIngredientsScreen(myIngredientsViewModel: MyIngredientsViewModel, back: () -> Unit) {
    val ingredientSections by myIngredientsViewModel.ingredientSections.collectAsState()
    val isDialogOpen by myIngredientsViewModel.isDialogOpen.collectAsState()
    val categories by myIngredientsViewModel.categories.collectAsState()
    val units by myIngredientsViewModel.units.collectAsState()
    val errorMessage by myIngredientsViewModel.errorMessage.collectAsState()
    val ingredientNames by myIngredientsViewModel.ingredientNames.collectAsState()
    val showOptionsDialog by myIngredientsViewModel.showOptionsDialog.collectAsState()
    val showEditDialog by myIngredientsViewModel.showEditDialog.collectAsState()
    val showDeleteConfirmation by myIngredientsViewModel.showDeleteConfirmation.collectAsState()
    val selectedIngredient by myIngredientsViewModel.selectedIngredient.collectAsState()
    val isAdmin by myIngredientsViewModel.isAdmin.collectAsState()
    val duplicateIngredient by myIngredientsViewModel.duplicateIngredient.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isAdmin) {
        myIngredientsViewModel.loadIngredientsForDisplay()
    }
    LaunchedEffect(myIngredientsViewModel.snackbarMessage.collectAsState().value) {
        val message = myIngredientsViewModel.snackbarMessage.value
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            myIngredientsViewModel.clearSnackbarMessage()
        }
    }
    AppScaffoldWithoutBottomBar(
        title = "Mis ingredientes",
        onBackClick = back,
        helpText = MyIngredientsHelp.myIngredientsHelp,
        content = { innerPadding ->

            if (ingredientSections.isEmpty() || ingredientSections.all { it.ingredients.isEmpty() }) {
                Text(
                    text = MyIngredientsHelp.myIngredientsHelp,
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(innerPadding)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            } else {
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    ingredientSections.forEachIndexed { index, section ->
                        if (index == 0 || section.category != ingredientSections[index - 1].category) {
                            item {
                                Text(
                                    text = section.category,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }
                        items(section.ingredients) { ingredient ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp, vertical = 4.dp)
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            myIngredientsViewModel.onIngredientLongPress(ingredient)
                                        }
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = ingredient.name)
                                Text(text = ingredient.unit.name)
                            }
                        }
                    }
                }
            }
        },
        onFabClick = { myIngredientsViewModel.showAddIngredientDialog() }
    )


    if (isDialogOpen) {
        AddIngredientDialog(
            categories = categories,
            units = units,
            ingredientNames = ingredientNames,
            errorMessage = errorMessage,
            onDismiss = { myIngredientsViewModel.hideAddIngredientDialog() },
            onConfirm = { name, category, unit ->
                myIngredientsViewModel.addNewIngredient(name, category, unit)
            }
        )
    }
    if (showEditDialog && selectedIngredient != null) {
        EditIngredientDialog(
            ingredient = selectedIngredient!!,
            categories = categories,
            units = units,
            onDismiss = { myIngredientsViewModel.hideDialog() },
            onConfirm = { category, unit ->
                myIngredientsViewModel.updateIngredient(category, unit)
            }
        )
    }
    if (showDeleteConfirmation && selectedIngredient != null) {
        AlertDialog(
            onDismissRequest = { myIngredientsViewModel.hideDialog() },
            title = { Text("Eliminar ingrediente") },
            text = { Text("¿Seguro que deseas eliminar ${selectedIngredient!!.name}?") },
            confirmButton = {
                TextButton(
                    onClick =  { myIngredientsViewModel.deleteIngredient() },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Sí, borrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick =  {myIngredientsViewModel.hideDialog() },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showOptionsDialog && selectedIngredient != null) {
        AlertDialog(
            onDismissRequest = { myIngredientsViewModel.hideDialog() },
            title = { Text("Opciones") },
            text = { Text("¿Qué quieres hacer con el ingrediente?\n${selectedIngredient!!.name}") },
            confirmButton = {
                TextButton(
                    onClick =  {  myIngredientsViewModel.showEditDialog()},
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Modificar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick =  { myIngredientsViewModel.showDeleteConfirmationDialog() },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
                ) {
                    Text("Eliminar")
                }
            }
        )
    }
    if (duplicateIngredient != null) {
        AlertDialog(
            onDismissRequest = { myIngredientsViewModel.dismissDuplicateDialog() },
            title = { Text("Este ingrediente ya existe") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre:" + duplicateIngredient!!.name)
                    Text("Categoría:" + duplicateIngredient!!.category.name)
                    Text("Unidad:" + duplicateIngredient!!.unit.name)
                }
            },
            confirmButton = {
                AppButton(
                    text = "Aceptar",
                    onClick = { myIngredientsViewModel.dismissDuplicateDialog() },
                    fullWidth = false,
                    modifier = Modifier.width(150.dp))
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientDialog(
    categories: List<CategoryModel>,
    units: List<UnitTypeModel>,
    ingredientNames: List<String>,
    onDismiss: () -> Unit,
    errorMessage: String?,
    onConfirm: (name: String, category: CategoryModel, unit: UnitTypeModel) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }
    var selectedUnit by remember { mutableStateOf<UnitTypeModel?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val suggestions = remember(name) {
        if (name.length >= 2) {
            ingredientNames.filter {
                it.contains(name, ignoreCase = true) && !it.equals(name, ignoreCase = true)
            }.take(5)
        } else {
            emptyList()
        }
    }

    val isFormValid = name.isNotBlank() && selectedCategory != null && selectedUnit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    selectedCategory?.let { category ->
                        selectedUnit?.let { unit ->
                            onConfirm(name.trim(), category, unit)
                        }
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isFormValid) colorResource(R.color.dark_orange)
                    else colorResource(R.color.personal_gray)
                )
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
            ) {
                Text("Cancelar")
            }
        },
        title = { Text("Nuevo ingrediente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                ExposedDropdownMenuBox(
                    expanded = expanded && suggestions.isNotEmpty(),
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            expanded = true
                        },
                        label = { Text("Nombre") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Sentences,
                            autoCorrectEnabled = true,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded && suggestions.isNotEmpty(),
                        onDismissRequest = { expanded = false }
                    ) {
                        suggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion) },
                                onClick = {
                                    name = suggestion
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                DropdownSelector(
                    label = "Categoría",
                    options = listOf(null) + categories,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it },
                    labelMapper = { it?.name ?: "Selecciona una categoría" }
                )

                DropdownSelector(
                    label = "Unidad",
                    options = listOf(null) + units,
                    selected = selectedUnit,
                    onSelected = { selectedUnit = it },
                    labelMapper = { it?.name ?: "Selecciona una unidad" }
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Si el nombre del ingrediente se autocompleta es que ya se encuentra dentro de la aplicaicón, si aún así necesitas añadirlo puedes modificar el nombre.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    )
}

@Composable
fun EditIngredientDialog(
    ingredient: IngredientModel,
    categories: List<CategoryModel>,
    units: List<UnitTypeModel>,
    onDismiss: () -> Unit,
    onConfirm: (category: CategoryModel, unit: UnitTypeModel) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ingredient.category) }
    var selectedUnit by remember { mutableStateOf(ingredient.unit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick =  { onConfirm(selectedCategory, selectedUnit) },
                colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
            ) {
                Text("Guardar cambios")
            }
        },
        dismissButton = {
            TextButton(
                onClick =  onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = colorResource(R.color.dark_orange))
            ) {
                Text("Cancelar")
            }
        },
        title = { Text("Editar ingrediente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ingredient.name,
                    onValueChange = {},
                    label = { Text("Nombre") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownSelector(
                    label = "Categoría",
                    options = categories,
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it },
                    labelMapper = { it.name }
                )

                DropdownSelector(
                    label = "Unidad",
                    options = units,
                    selected = selectedUnit,
                    onSelected = { selectedUnit = it },
                    labelMapper = { it.name }
                )
            }
        }
    )
}


