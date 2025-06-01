package com.lucasdev.apprecetas.shopping.ui.myshoppinghistory

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lucasdev.apprecetas.general.ui.scaffold.AppScaffoldWithoutBottomBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.textApp.helpText.MyShoppingHistoryHelp

@Composable
fun MyShoppingHistoryScreen(
    myShoppingHistoryViewModel: MyShoppingHistoryViewModel,
    back: () -> Unit
) {
    val history by myShoppingHistoryViewModel.history.collectAsState()
    val expandedItems by myShoppingHistoryViewModel.expandedItems.collectAsState()
    val historyItems by myShoppingHistoryViewModel.historyItems.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(myShoppingHistoryViewModel.snackbarMessage.collectAsState().value) {
        val message = myShoppingHistoryViewModel.snackbarMessage.value
        if (message.isNotEmpty()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            myShoppingHistoryViewModel.clearSnackbarMessage()
        }
    }
    AppScaffoldWithoutBottomBar(
        title = "Últimas compras",
        onBackClick = back,
        helpText = MyShoppingHistoryHelp.myShoppingHistoryHelp,
        content = { innerPadding ->
            if (history.isEmpty()) {
                Text(
                    text = MyShoppingHistoryHelp.myShoppingHistoryHelp,
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
                    items(history) { historyItem ->
                        val isExpanded = expandedItems.contains(historyItem.id)

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .clickable { myShoppingHistoryViewModel.toggleExpanded(historyItem.id) }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = myShoppingHistoryViewModel.formatTimestamp(historyItem.date),
                                style = MaterialTheme.typography.titleMedium
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                val itemsForThisHistory = historyItems[historyItem.id].orEmpty()

                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    itemsForThisHistory.forEach { item ->
                                        Text(
                                            text = "- ${item.name}: ${item.quantity} ${item.unit.name}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Spacer(modifier = Modifier.weight(1f))
                                        OutlinedButton(
                                            onClick = {
                                                myShoppingHistoryViewModel.deleteHistoryItem(
                                                    historyItem.id
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(id = R.color.orange),
                                                contentColor = colorResource(id = R.color.white)
                                            )
                                        ) {
                                            Text("Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    )
}

