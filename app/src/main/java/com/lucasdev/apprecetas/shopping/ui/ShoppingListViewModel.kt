package com.lucasdev.apprecetas.shopping.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.usecase.AddIngredientsToPantryFromShoppingUseCase
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import com.lucasdev.apprecetas.shopping.domain.usecase.AddShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.DeleteShoppingListUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.UpdateShoppingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getShoppingLists: GetShoppingListsUseCase,
    private val addShoppingList: AddShoppingListUseCase,
    private val updateShoppingList: UpdateShoppingListUseCase,
    private val deleteShoppingList: DeleteShoppingListUseCase,
    private val addIngredientsToPantry: AddIngredientsToPantryFromShoppingUseCase
) : ViewModel() {

    private val _shoppingLists = MutableStateFlow<List<ShoppingListModel>>(emptyList())
    val shoppingLists = _shoppingLists.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    init {
        loadLists()
        getUserName()
    }

    fun loadLists() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _shoppingLists.value = getShoppingLists()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
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

    fun addList(list: ShoppingListModel) {
        viewModelScope.launch {
            val success = addShoppingList(list)
            if (success) loadLists()
            else _error.value = "Error al guardar la lista"
        }
    }

    fun updateList(list: ShoppingListModel) {
        viewModelScope.launch {
            val success = updateShoppingList(list)
            if (success) loadLists()
            else _error.value = "Error al actualizar la lista"
        }
    }

    fun deleteList(id: String) {
        viewModelScope.launch {
            val success = deleteShoppingList(id)
            if (success) loadLists()
            else _error.value = "Error al eliminar la lista"
        }
    }

    fun finalizePurchase(list: ShoppingListModel) {
        val (checked, unchecked) = list.items.partition { it.checked }

        // Crear nuevo modelo sin los ingredientes marcados
        val updatedList = list.copy(items = unchecked)

        viewModelScope.launch {
            val updateSuccess = updateShoppingList(updatedList)
            if (updateSuccess) {
                if (checked.isNotEmpty()) {
                    val pantryItems = checked.map {
                        PantryIngredientModel(
                            ingredientId = it.ingredientId,
                            name = it.name,
                            category =it.category,
                            unit = it.unit,
                            quantity = it.quantity
                        )
                    }
                    addToInventory(pantryItems) // necesitas este use case implementado
                }
                loadLists()
            } else {
                _error.value = "Error al finalizar la compra"
            }
        }
    }

    fun addToInventory(ingredients: List<PantryIngredientModel>) {
        viewModelScope.launch {
            try {
                val addedIngredients = addIngredientsToPantry(ingredients) // Añadir todos los ingredientes de una vez
                // Aquí puedes realizar cualquier otra acción con los ingredientes añadidos
            } catch (e: Exception) {
                _error.value = "Error al añadir ingredientes a la despensa: ${e.message}"
            }
        }
    }

}
