package com.lucasdev.apprecetas.ingredients.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientSection
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientSection
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.usecase.AddUserPantryIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.DeleteUserPantryIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetCategoriesUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUnitTypeUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUserIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUserPantryIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.UpdateUserPantryIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PantryIngredientsViewModel @Inject constructor(
    private val getUserPantryIngredientsUseCase: GetUserPantryIngredientsUseCase,
    private val addUserPantryIngredientUseCase: AddUserPantryIngredientUseCase,
    private val updateUserPantryIngredientUseCase: UpdateUserPantryIngredientUseCase,
    private val deleteUserPantryIngredientUseCase: DeleteUserPantryIngredientUseCase,
    private val getUserIngredientUseCase: GetUserIngredientUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getUnitTypeUseCase: GetUnitTypeUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,

    ) : ViewModel() {
    var isAdmin: Boolean = false

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen

    private val _addIngredientSuccess = MutableStateFlow<Boolean?>(null)
    val addIngredientSuccess: StateFlow<Boolean?> = _addIngredientSuccess

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _pantryIngredients = MutableStateFlow<List<PantryIngredientModel>>(emptyList())
    val pantryIngredients: StateFlow<List<PantryIngredientModel>> = _pantryIngredients


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    private val _units = MutableStateFlow<List<UnitTypeModel>>(emptyList())
    val units: StateFlow<List<UnitTypeModel>> = _units

    private val _allIngredients = MutableStateFlow<List<IngredientModel>>(emptyList())
    var allIngredients: StateFlow<List<IngredientModel>> = _allIngredients

    private val _ingredientNames = MutableStateFlow<List<String>>(emptyList())
    val ingredientNames: StateFlow<List<String>> = _ingredientNames

    private val _ingredientSections = MutableStateFlow<List<IngredientSection>>(emptyList())
    val ingredientSections: StateFlow<List<IngredientSection>> = _ingredientSections

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedIngredientToEdit = MutableStateFlow<PantryIngredientModel?>(null)
    val selectedIngredientToEdit: StateFlow<PantryIngredientModel?> = _selectedIngredientToEdit

    private val _isEditDialogOpen = MutableStateFlow(false)
    val isEditDialogOpen: StateFlow<Boolean> = _isEditDialogOpen

    private val _groupedIngredients = MutableStateFlow<List<PantryIngredientSection>>(emptyList())
    val groupedIngredients: StateFlow<List<PantryIngredientSection>> = _groupedIngredients

    init {
        viewModelScope.launch {
            getUserName()
            loadIngredients()
            loadCategoriesAndUnits()
            loadUserPantryIngredients()
            pantryIngredients.collect {
                groupIngredients()
            }
        }
    }

    private fun loadUserPantryIngredients() {
        viewModelScope.launch {
            _isLoading.value = true
            val userIngredients = getUserPantryIngredientsUseCase()

            val ingredientsMap = _allIngredients.value.associateBy { it.id }

            val updatedIngredients = userIngredients.map { pantry ->
                val updatedUnit = ingredientsMap[pantry.ingredientId]?.unit ?: pantry.unit
                pantry.copy(unit = updatedUnit)
            }

            _pantryIngredients.value = updatedIngredients
            _isLoading.value = false
        }
    }

    private suspend fun loadIngredients() {
        val allIngredientsUser = getUserIngredientUseCase()
        val duplicates = allIngredientsUser.groupBy { it.name.lowercase() }
            .filter { it.value.size > 1 }

        if (duplicates.isNotEmpty()) {
            Log.w("PantryVM", "Duplicados detectados: ${duplicates.keys}")
        }

        val allIngredientsApp = getIngredientsUseCase()
        val combinedIngredients = (allIngredientsApp + allIngredientsUser)
            .distinctBy { it.name.lowercase() }

        _allIngredients.value = combinedIngredients
        _ingredientNames.value = combinedIngredients.map { it.name }

        _ingredientSections.value = allIngredientsUser
            .groupBy { it.category.name }
            .map { (cat, list) ->
                IngredientSection(
                    category = cat,
                    ingredients = list.sortedBy { it.name }
                )
            }
            .sortedBy { it.category }
    }

    private suspend fun loadCategoriesAndUnits() {
        val categoriesList = getCategoriesUseCase()
        val unitsList = getUnitTypeUseCase()

        _categories.value = categoriesList
        _units.value = unitsList
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


    private fun groupIngredients() {
        _groupedIngredients.value = pantryIngredients.value
            .groupBy { it.category.name }
            .toSortedMap()
            .map { (category, ingredients) ->
                PantryIngredientSection(category, ingredients.sortedBy { it.name })
            }
    }

    fun setSelectedIngredientToEdit(ingredient: PantryIngredientModel) {
        _selectedIngredientToEdit.value = ingredient
    }

    fun closeEditDialog() {
        _isEditDialogOpen.value = false
        _selectedIngredientToEdit.value = null
    }


    fun addOrUpdateIngredientInPantry(ingredient: IngredientModel, quantity: Double) {
        val existingIngredient = _pantryIngredients.value.find { it.ingredientId == ingredient.id }

        viewModelScope.launch {
            if (existingIngredient != null) {
                val updatedIngredient = existingIngredient.copy(
                    quantity = existingIngredient.quantity + quantity
                )
                val success = updateUserPantryIngredientUseCase(updatedIngredient)

                if (success) {

                    _pantryIngredients.value = _pantryIngredients.value.map {
                        if (it.id == updatedIngredient.id) updatedIngredient else it
                    }
                } else {
                    //todo log de error
                }

            } else {
                val newIngredient = PantryIngredientModel(
                    ingredientId = ingredient.id,
                    name = ingredient.name,
                    category = ingredient.category,
                    unit = ingredient.unit,
                    quantity = quantity
                )

                val addedIngredient = addUserPantryIngredientUseCase(newIngredient)

                _pantryIngredients.value = _pantryIngredients.value + addedIngredient
            }
        }
    }

    fun updatedIngredientInPantry(updated: PantryIngredientModel) {
        viewModelScope.launch {
            if (updated.quantity == 0.0) {
                deleteIngredient(updated.id)
            } else {
                val success = updateUserPantryIngredientUseCase(updated)
                if (success) {
                    _pantryIngredients.value = _pantryIngredients.value.map {
                        if (it.id == updated.id) updated else it
                    }
                    closeEditDialog()
                } else {
               //todo log de error
                }
            }
        }
    }

    fun confirmDeleteSelectedIngredientInPantry() {
        _selectedIngredientToEdit.value?.let { ingredient ->
            deleteIngredient(ingredient.id)
            closeEditDialog()
        }
    }

    fun deleteIngredient(id: String) {
        viewModelScope.launch {
            val success = deleteUserPantryIngredientUseCase(id)
            if (success) {
                loadUserPantryIngredients()
                closeEditDialog()
            } else {
                //todo log de error
            }

        }
    }
}

