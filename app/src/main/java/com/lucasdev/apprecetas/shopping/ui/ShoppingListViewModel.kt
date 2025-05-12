package com.lucasdev.apprecetas.shopping.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.usecase.AddIngredientsToPantryFromShoppingUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetCategoriesUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.UpdateUserPantryIngredientUseCase
import com.lucasdev.apprecetas.ingredients.ui.PantryIngredientsViewModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemSection
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import com.lucasdev.apprecetas.shopping.domain.usecase.AddIngredientToShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.AddShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.DeleteItemFromShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.DeleteShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetItemsForListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.UpdateIngredientCheckedStatusUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.UpdateShoppingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getShoppingLists: GetShoppingListsUseCase,
    private val addShoppingList: AddShoppingListUseCase,
    private val addIngredientToShoppingList: AddIngredientToShoppingListUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getItemsForList: GetItemsForListUseCase,
    private val updateIngredientCheckedStatus: UpdateIngredientCheckedStatusUseCase,
    private val updateUserPantryIngredientUseCase: UpdateUserPantryIngredientUseCase,
    private val deleteItemFromShoppingListUseCase: DeleteItemFromShoppingListUseCase,
) : ViewModel() {

    private val _ingredients = MutableStateFlow<List<IngredientModel>>(emptyList())
    val ingredients: StateFlow<List<IngredientModel>> = _ingredients

    private val _activeListItems = MutableStateFlow<List<ShoppingItemModel>>(emptyList())
    val activeListItems: StateFlow<List<ShoppingItemModel>> = _activeListItems

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    private val _shoppingLists = MutableStateFlow<List<ShoppingListModel>>(emptyList())
    val shoppingLists: StateFlow<List<ShoppingListModel>> = _shoppingLists

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _activeListId = MutableStateFlow<String?>(null)
    val activeListId: StateFlow<String?> = _activeListId

    private val _shoppingItemSections = MutableStateFlow<List<ShoppingItemSection>>(emptyList())
    val shoppingItemSections: StateFlow<List<ShoppingItemSection>> = _shoppingItemSections





    init {
        loadLists()
        getUserName()
        ensureActiveListExists()
        loadActiveListItems()
        loadIngredientsAndCategories()
    }
    fun refreshShoppingList() {
        viewModelScope.launch {
            loadLists()
            loadIngredientsAndCategories()
        }
    }
    private fun loadIngredientsAndCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _ingredients.value = getIngredientsUseCase()
                _categories.value = getCategoriesUseCase()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun ensureActiveListExists() {
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

    fun loadLists() {
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

    fun loadActiveListItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val activeList = _shoppingLists.value.firstOrNull()
                if (activeList != null && activeList.id.isNotEmpty()) {
                    val items = getItemsForList(activeList.id)

                    val grouped = items
                        .groupBy { it.category.name}
                        .map { (category, itemsInCategory) ->
                            ShoppingItemSection(
                                category = category,
                                items = itemsInCategory.sortedBy { it.name.lowercase() }
                            )
                        }
                        .sortedBy { it.category.lowercase() }

                    _activeListItems.value = items // por si lo necesitas sin agrupar
                    _shoppingItemSections.value = grouped
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar los ingredientes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun getUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val firestoreName = document.getString("name")
                        if (!firestoreName.isNullOrEmpty()) {
                            _userName.value = firestoreName
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(
                        "IngredientsViewModel",
                        "Error al obtener nombre de usuario: ${e.message}"
                    )
                }
        }
    }

    fun addIngredientToList(ingredient: IngredientModel, quantity: Double) {
        viewModelScope.launch {
            var activeList = _shoppingLists.value.firstOrNull()

            // Si no hay lista, la creamos y esperamos hasta que esté disponible
            if (activeList == null || activeList.id.isEmpty()) {
                ensureActiveListExists()

                // Esperar a que se propague la lista (máximo 10 intentos con 100ms de retraso)
                repeat(10) {
                    activeList = _shoppingLists.value.firstOrNull()
                    if (activeList != null && activeList!!.id.isNotEmpty()) return@repeat
                    kotlinx.coroutines.delay(100)
                }
            }

            if (activeList == null || activeList!!.id.isEmpty()) {
                _errorMessage.value = "No se pudo obtener una lista activa"
                return@launch
            }

            val item = ShoppingItemModel(
                ingredientId = ingredient.id,
                name = ingredient.name,
                category = ingredient.category,
                unit = ingredient.unit,
                quantity = quantity
            )

            try {
                val success = addIngredientToShoppingList(activeList!!.id, item)
                if (!success) {
                    _errorMessage.value = "No se pudo añadir el ingrediente"
                } else {
                    // Opcional: recargar lista si quieres que se refleje al instante
                    loadLists()
                    loadActiveListItems()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al añadir el ingrediente: ${e.message}"
            }
        }
    }

    fun toggleItemChecked(listId: String, itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                val success = updateIngredientCheckedStatus(listId, itemId, isChecked)
                if (success) {
                    // Actualiza lista plana
                    val updatedItems = _activeListItems.value.map {
                        if (it.id == itemId) it.copy(checked = isChecked) else it
                    }
                    _activeListItems.value = updatedItems

                    // Vuelve a agrupar por categoría
                    val grouped = updatedItems
                        .groupBy { it.category.name }
                        .map { (category, itemsInCategory) ->
                            ShoppingItemSection(
                                category = category,
                                items = itemsInCategory.sortedBy { it.name.lowercase() }
                            )
                        }
                        .sortedBy { it.category.lowercase() }

                    _shoppingItemSections.value = grouped
                } else {
                    _errorMessage.value = "No se pudo actualizar el ítem"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }


    fun moveCheckedItemsToPantry(pantryIngredientsViewModel: PantryIngredientsViewModel) {
        viewModelScope.launch {
            val activeList = _shoppingLists.value.firstOrNull()
            if (activeList == null) {
                _errorMessage.value = "No hay lista activa"
                return@launch
            }

            val checkedItems = _activeListItems.value.filter { it.checked }

            if (checkedItems.isEmpty()) {
                _errorMessage.value = "No hay elementos marcados"
                return@launch
            }

            checkedItems.forEach { item ->
                val ingredient = IngredientModel(
                    id = item.ingredientId,
                    name = item.name,
                    category = item.category,
                    unit = item.unit
                )
                pantryIngredientsViewModel.addOrUpdateIngredientInPantry(ingredient, item.quantity)
                Log.e("ShoppingListViewModel", "Ingrediente a trasnferir ${ingredient.name} Cantidad a transferir ${item.quantity}")


            }

            // (Opcional) Borrar los ingredientes movidos de la lista de compra
            checkedItems.forEach { item ->
               deleteItemFromShoppingListUseCase(activeList.id, item.id)
            }

            loadActiveListItems()
        }
    }





}
