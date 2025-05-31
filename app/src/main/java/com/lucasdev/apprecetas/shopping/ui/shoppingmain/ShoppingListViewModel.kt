package com.lucasdev.apprecetas.shopping.ui.shoppingmain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetCategoriesUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.ui.pantry.PantryIngredientsViewModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingHistoryModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemSection
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import com.lucasdev.apprecetas.shopping.domain.usecase.AddIngredientToShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.AddShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.DeleteItemFromShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetItemsForListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.SaveShoppingHistoryUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.UpdateIngredientCheckedStatusUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.UpdateItemInShoppingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the shopping list screen.
 * @property getShoppingLists Use case to retrieve shopping lists.
 * @property addShoppingList Use case to add a new shopping list.
 * @property addIngredientToShoppingList Use case to add an ingredient to a shopping list.
 * @property getIngredientsUseCase Use case to retrieve ingredients.
 * @property getCategoriesUseCase Use case to retrieve categories.
 * @property getItemsForList Use case to retrieve items for a specific list.
 * @property updateIngredientCheckedStatus Use case to update the checked status of an ingredient.
 * @property deleteItemFromShoppingListUseCase Use case to delete an item from a shopping list.
 * @property updateItemInShoppingListUseCase Use case to update an item in a shopping list.
 * @property saveShoppingHistoryUseCase Use case to save a shopping history.
 */
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getShoppingLists: GetShoppingListsUseCase,
    private val addShoppingList: AddShoppingListUseCase,
    private val addIngredientToShoppingList: AddIngredientToShoppingListUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getItemsForList: GetItemsForListUseCase,
    private val updateIngredientCheckedStatus: UpdateIngredientCheckedStatusUseCase,
    private val deleteItemFromShoppingListUseCase: DeleteItemFromShoppingListUseCase,
    private val updateItemInShoppingListUseCase: UpdateItemInShoppingListUseCase,
    private val saveShoppingHistoryUseCase: SaveShoppingHistoryUseCase,
) : ViewModel() {
    /** State holding the list of ingredients. */
    private val _ingredients = MutableStateFlow<List<IngredientModel>>(emptyList())
    val ingredients: StateFlow<List<IngredientModel>> = _ingredients

    /** State holding the list of active list items. */
    private val _activeListItems = MutableStateFlow<List<ShoppingIngredientModel>>(emptyList())
    val activeListItems: StateFlow<List<ShoppingIngredientModel>> = _activeListItems

    /** State holding the list of categories. */
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    /** State holding the list of shopping lists. */
    private val _shoppingLists = MutableStateFlow<List<ShoppingListModel>>(emptyList())
    val shoppingLists: StateFlow<List<ShoppingListModel>> = _shoppingLists

    /** State indicating whether data is loading. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** State holding any error message, if any. */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** State holding the user's name. */
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    /** State holding the ID of the active shopping list. */
    private val _activeListId = MutableStateFlow<String?>(null)
    val activeListId: StateFlow<String?> = _activeListId

    /** State holding the list of shopping item sections. */
    private val _shoppingItemSections = MutableStateFlow<List<ShoppingItemSection>>(emptyList())
    val shoppingItemSections: StateFlow<List<ShoppingItemSection>> = _shoppingItemSections

    /** Holds any error message to show in UI */
    private val _snackbarMessage =MutableStateFlow<String>("")
    val snackbarMessage :StateFlow<String> = _snackbarMessage



    init {
        loadLists()
        getUserName()
        ensureActiveListExists()
        loadActiveListItems()
        loadIngredientsAndCategories()
    }

    /**
     * Refreshes the shopping list data by loading ingredients and categories.
     */
    fun refreshShoppingList() {
        viewModelScope.launch {
            loadLists()
            loadIngredientsAndCategories()
        }
    }

    /**
     * Loads ingredients and categories from the use cases.
     */
    private fun loadIngredientsAndCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _ingredients.value = getIngredientsUseCase().sortedBy { it.name }
                _categories.value = getCategoriesUseCase().sortedBy { it.name }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Ensures that there is an active shopping list. If none exists, creates a new one.
     */
    private fun ensureActiveListExists() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lists = getShoppingLists()
                if (lists.isEmpty()) {
                    val newList = ShoppingListModel(
                        title = "Lista activa"
                    )
                    val createdList = addShoppingList(newList)
                    if (createdList != null) {
                        _shoppingLists.value = listOf(createdList)
                        _activeListId.value = createdList.id
                        loadActiveListItems()
                    } else {
                        _errorMessage.value = "No se pudo crear la lista activa"
                    }
                } else {
                    val active = lists.first()
                    _shoppingLists.value = lists
                    _activeListId.value = active.id
                    loadActiveListItems()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads the list of shopping lists from the use case.
     */
    private fun loadLists() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val lists = getShoppingLists().sortedByDescending { it.date }
                _shoppingLists.value = lists

                val active = lists.firstOrNull()
                if (active != null) {
                    _activeListId.value = active.id
                    loadActiveListItems()
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads the active list items from the use case.
     */
    private fun loadActiveListItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val activeList = _shoppingLists.value.firstOrNull()
                if (activeList != null && activeList.id.isNotEmpty()) {
                    val items = getItemsForList(activeList.id)

                    val grouped =
                        items.groupBy { it.category.name }.map { (category, itemsInCategory) ->
                                ShoppingItemSection(category = category,
                                    items = itemsInCategory.sortedBy { it.name.lowercase() })
                            }.sortedBy { it.category.lowercase() }

                    _activeListItems.value = items
                    _shoppingItemSections.value = grouped
                }
            } catch (e: Exception) {
                _snackbarMessage.emit("Error al cargar los ingredientes: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Retrieves the user's name from Firebase Firestore.
     */
    private fun getUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firestoreName = document.getString("name")
                        if (!firestoreName.isNullOrEmpty()) {
                            _userName.value = firestoreName
                        }
                    }
                }.addOnFailureListener { e ->
                    Log.e(
                        "IngredientsViewModel", "Error al obtener nombre de usuario: ${e.message}"
                    )
                }
        }
    }

    /**
     * Adds an ingredient to the active shopping list.
     */
    fun addIngredientToList(ingredient: IngredientModel, quantity: Double) {
        viewModelScope.launch {
            var activeList = _shoppingLists.value.firstOrNull()

            if (activeList == null || activeList.id.isEmpty()) {
                ensureActiveListExists()

                repeat(10) {
                    activeList = _shoppingLists.value.firstOrNull()
                    if (activeList != null && activeList!!.id.isNotEmpty()) return@repeat
                    kotlinx.coroutines.delay(100)
                }
            }

            if (activeList == null || activeList!!.id.isEmpty()) {
                _snackbarMessage.emit("No se pudo obtener una lista activa")
                return@launch
            }

            val item = ShoppingIngredientModel(
                ingredientId = ingredient.id,
                name = ingredient.name,
                category = ingredient.category,
                unit = ingredient.unit,
                quantity = quantity
            )

            try {
                val success = addIngredientToShoppingList(activeList!!.id, item)
                if (!success) {
                    _snackbarMessage.emit( "No se pudo añadir el ingrediente")
                } else {
                    loadLists()
                    loadActiveListItems()
                }
            } catch (e: Exception) {
                _snackbarMessage.emit("Error al añadir el ingrediente: ${e.message}")
            }
        }
    }

    /**
     * Updates the checked status of an ingredient in the shopping list.
     */
    fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                val success = updateIngredientCheckedStatus(listId, itemId, isChecked)
                if (success) {
                    val updatedItems = _activeListItems.value.map {
                        if (it.id == itemId) it.copy(checked = isChecked) else it
                    }
                    _activeListItems.value = updatedItems

                    val grouped = updatedItems.groupBy { it.category.name }
                        .map { (category, itemsInCategory) ->
                            ShoppingItemSection(category = category,
                                items = itemsInCategory.sortedBy { it.name.lowercase() })
                        }.sortedBy { it.category.lowercase() }

                    _shoppingItemSections.value = grouped
                } else {
                    _snackbarMessage.emit("No se pudo actualizar el ítem")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Clears the snackbar message.
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = ""
    }

    /**
     * Moves checked items from the pantry to the shopping list.
     */
    fun moveCheckedItemsToPantry(pantryIngredientsViewModel: PantryIngredientsViewModel) {
        viewModelScope.launch {
            val activeList = _shoppingLists.value.firstOrNull()
            if (activeList == null) {
                _errorMessage.value = "No hay lista activa"
                return@launch
            }

            val checkedItems = _activeListItems.value.filter { it.checked }

            if (checkedItems.isEmpty()) {
                _snackbarMessage.emit("No hay elementos marcados")

                return@launch
            }
            val historyEntry = ShoppingHistoryModel(
                title = "Transferencia del ${activeList.date.toDate().toLocaleString()}",
                date = Timestamp.now(),
                items = checkedItems
            )

            try {
                saveShoppingHistoryUseCase(historyEntry)
            } catch (e: Exception) {
                Log.e("ShoppingListViewModel", "Error guardando historial: ${e.message}")
            }

            checkedItems.forEach { item ->
                val ingredient = IngredientModel(
                    id = item.ingredientId,
                    name = item.name,
                    category = item.category,
                    unit = item.unit
                )
                pantryIngredientsViewModel.addOrUpdateIngredientInPantry(ingredient, item.quantity)
                Log.e(
                    "ShoppingListViewModel",
                    "Ingrediente a trasnferir ${ingredient.name} Cantidad a transferir ${item.quantity}"
                )
            }
            checkedItems.forEach { item ->
                deleteItemFromShoppingListUseCase(activeList.id, item.id)
            }

            loadActiveListItems()
            _snackbarMessage.emit("Compra finalizada. Ingredientes movidos a la despensa")

            _errorMessage.value = null
        }
    }

    /**
     * Updates an item in the shopping list.
     */
    fun updateItem(item: ShoppingIngredientModel) {
        viewModelScope.launch {
            try {
                val success = updateItemInShoppingListUseCase(
                    _activeListId.value ?: return@launch, item
                )
                if (success) {
                    loadActiveListItems()
                } else {
                    _errorMessage.value = "No se pudo actualizar el item"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error actualizando item: ${e.message}"
            }
        }
    }

    /**
     * Deletes an item from the shopping list.
     */
    fun deleteItem(item: ShoppingIngredientModel) {
        viewModelScope.launch {
            try {
                val success = deleteItemFromShoppingListUseCase(
                    _activeListId.value ?: return@launch, item.id
                )
                if (success) {
                    loadActiveListItems()
                } else {
                    _errorMessage.value = "No se pudo actualizar el item"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar el item: ${e.message}"

            }
        }
    }
}






