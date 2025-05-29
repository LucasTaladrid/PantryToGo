package com.lucasdev.apprecetas.shopping.ui.myshoppinghistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingHistoryModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import com.lucasdev.apprecetas.shopping.domain.usecase.DeleteShoppingHistoryByIdUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetItemsForHistoryUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetRecentShoppingHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MyShoppingHistoryViewModel @Inject constructor(
    private val getRecentShoppingHistoryUseCase: GetRecentShoppingHistoryUseCase,
    private val deleteShoppingHistoryByIdUseCase: DeleteShoppingHistoryByIdUseCase,
    private val getItemsForHistoryUseCase: GetItemsForHistoryUseCase
) : ViewModel() {

    private val _history = MutableStateFlow<List<ShoppingHistoryModel>>(emptyList())
    val history: StateFlow<List<ShoppingHistoryModel>> = _history

    private val _historyItems = MutableStateFlow<Map<String, List<ShoppingIngredientModel>>>(emptyMap())
    val historyItems: StateFlow<Map<String, List<ShoppingIngredientModel>>> = _historyItems


    private val _expandedItems = MutableStateFlow(setOf<String>())
    val expandedItems: StateFlow<Set<String>> = _expandedItems

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Holds any error message to show in UI */
    private val _snackbarMessage =MutableStateFlow<String>("")
    val snackbarMessage :StateFlow<String> = _snackbarMessage

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                _history.value = getRecentShoppingHistoryUseCase()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar historial: ${e.message}"
            }
        }
    }
    private fun loadItemsForHistory(historyId: String) {
        viewModelScope.launch {
            val items = getItemsForHistoryUseCase(historyId)
            _historyItems.update { currentMap ->
                currentMap + (historyId to items)
            }
        }
    }



    fun toggleExpanded(id: String) {
        val isExpanded = _expandedItems.value.contains(id)

        _expandedItems.value = if (isExpanded) {
            _expandedItems.value - id
        } else {
            _expandedItems.value + id
        }


        if (!isExpanded) {
            loadItemsForHistory(id)
        }
    }


    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            try {
                deleteShoppingHistoryByIdUseCase(id)
                loadHistory()
                _snackbarMessage.emit("Lista eliminada")
            } catch (e: Exception) {
                _errorMessage.value = "No se pudo eliminar la lista: ${e.message}"
            }
        }
    }

    fun formatTimestamp(timestamp: Timestamp): String {
        val localDateTime = timestamp.toDate()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
    /**
     * Clears the snackbar message.
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = ""
    }



}
