package com.lucasdev.apprecetas.general.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.lucasdev.apprecetas.general.ui.dropDownSelector.DropdownSelector
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel

@Composable
fun AddIngredientWithQuantityDialog(
    categories: List<CategoryModel>,
    availableIngredients: List<IngredientModel>,
    onDismiss: () -> Unit,
    onConfirm: (IngredientModel, Double) -> Unit,
    errorMessage: String?,
    title:String,
    existingIngredients: List<PantryIngredientModel>? = null
) {
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }
    var query by remember { mutableStateOf("") }
    var selectedIngredient by remember { mutableStateOf<IngredientModel?>(null) }
    var quantity by remember { mutableStateOf("") }
    var duplicateError by remember { mutableStateOf(false) }

    val filteredIngredients = availableIngredients.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
                it.name.contains(query, ignoreCase = true)
    }
    AlertDialog(
        containerColor = Color.White,
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
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
                if (duplicateError) {
                    Text(
                        text = "Ese ingrediente ya está en la receta",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

            }

        },
        confirmButton = {
            val isEnabled = selectedIngredient != null && quantity.toDoubleOrNull() != null
            TextButton(
                onClick = {
                    val qty = quantity.toDoubleOrNull()
                    if (selectedIngredient != null && qty != null && qty > 0) {
                        val alreadyExists = existingIngredients?.any {
                            it.ingredientId == selectedIngredient!!.id
                        } ?: false
                        if (alreadyExists) {
                            duplicateError = true
                        } else {
                            onConfirm(selectedIngredient!!, qty)
                        }
                    }
                },
                enabled = isEnabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isEnabled) colorResource(R.color.dark_orange)
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
    )
}