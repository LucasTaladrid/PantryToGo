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

/**
 * ViewModel for the shopping history screen.
 * @property getRecentShoppingHistoryUseCase Use case to retrieve recent shopping history.
 * @property deleteShoppingHistoryByIdUseCase Use case to delete a shopping history by ID.
 * @property getItemsForHistoryUseCase Use case to retrieve items for a specific history ID.
 */
@HiltViewModel
class MyShoppingHistoryViewModel @Inject constructor(
    private val getRecentShoppingHistoryUseCase: GetRecentShoppingHistoryUseCase,
    private val deleteShoppingHistoryByIdUseCase: DeleteShoppingHistoryByIdUseCase,
    private val getItemsForHistoryUseCase: GetItemsForHistoryUseCase
) : ViewModel() {
    /** Holds the list of shopping history items */
    private val _history = MutableStateFlow<List<ShoppingHistoryModel>>(emptyList())
    val history: StateFlow<List<ShoppingHistoryModel>> = _history
    /** Holds the items for each history ID */
    private val _historyItems = MutableStateFlow<Map<String, List<ShoppingIngredientModel>>>(emptyMap())
    val historyItems: StateFlow<Map<String, List<ShoppingIngredientModel>>> = _historyItems

    /** Holds the currently expanded item IDs */
    private val _expandedItems = MutableStateFlow(setOf<String>())
    val expandedItems: StateFlow<Set<String>> = _expandedItems

    /** Holds any error message to show in UI */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Holds any error message to show in UI */
    private val _snackbarMessage =MutableStateFlow<String>("")
    val snackbarMessage :StateFlow<String> = _snackbarMessage

    init {
        loadHistory()
    }

    /**
     * Loads the shopping history from the use case.
     */
    private fun loadHistory() {
        viewModelScope.launch {
            try {
                _history.value = getRecentShoppingHistoryUseCase()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar historial: ${e.message}"
            }
        }
    }

    /**
     * Loads items for a specific history ID.
     */
    private fun loadItemsForHistory(historyId: String) {
        viewModelScope.launch {
            val items = getItemsForHistoryUseCase(historyId)
            _historyItems.update { currentMap ->
                currentMap + (historyId to items)
            }
        }
    }


    /**
     * Toggles the expansion state of an item.
     */
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


    /**
     * Deletes a shopping history item by its ID.
     */
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

    /**
     * Formats a timestamp to a readable date string.
     */
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
