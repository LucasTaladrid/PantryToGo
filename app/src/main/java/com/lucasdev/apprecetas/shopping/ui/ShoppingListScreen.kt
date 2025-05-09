package com.lucasdev.apprecetas.shopping.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffold
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel

@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel,
    onNavigate: (String) -> Unit
) {
    val userName = viewModel.userName.collectAsState()
    val shoppingLists = viewModel.shoppingLists.collectAsState()
    val loading = viewModel.loading.collectAsState()
    val error = viewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val currentList = shoppingLists.value.firstOrNull()

    AppScaffold(
        userName = userName.value,
        onFabClick = { showAddDialog = true },
        onNavigate = onNavigate,
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

        currentList?.let { list ->
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

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(list.items) { item ->
                        ShoppingItemRow(
                            item = item,
                            onCheckedChange = { isChecked ->
                                val updatedItem = item.copy(checked = isChecked)
                                val updatedList = list.copy(
                                    items = list.items.map { i -> if (i.ingredientId == item.ingredientId) updatedItem else i }
                                )
                                viewModel.updateList(updatedList)
                            }
                        )
                    }
                }

                Button(
                    onClick = { viewModel.finalizePurchase(list) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Finalizar Compra")
                }
            }
        } ?: run {
            Text(
                text = "No hay listas de la compra activas.",
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            )
        }

        // Aquí podrías incluir el diálogo para añadir ingrediente
        if (showAddDialog) {
            // ShoppingItemDialog(onDismiss = { showAddDialog = false }, onSave = { ... })
        }
    }
    )
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItemModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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

