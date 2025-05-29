package com.lucasdev.apprecetas.ingredients.ui.pantry

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
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUserPantryIngredientByIngredientIdUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUserPantryIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.UpdateUserPantryIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the user's pantry ingredients.
 *
 * This ViewModel handles loading, adding, updating, deleting, and grouping pantry ingredients,
 * as well as managing related data such as categories, units, and ingredient sections.
 *
 * @property getUserPantryIngredientsUseCase Use case to fetch pantry ingredients for the user.
 * @property addUserPantryIngredientUseCase Use case to add a pantry ingredient for the user.
 * @property updateUserPantryIngredientUseCase Use case to update a pantry ingredient.
 * @property deleteUserPantryIngredientUseCase Use case to delete a pantry ingredient.
 * @property getUserIngredientUseCase Use case to fetch user-defined ingredients.
 * @property getIngredientsUseCase Use case to fetch default app ingredients.
 * @property getUnitTypeUseCase Use case to fetch units of measurement.
 * @property getCategoriesUseCase Use case to fetch ingredient categories.
 * @property getUserPantryIngredientByIngredientIdUseCase Use case to fetch a pantry ingredient by ingredient ID.
 */
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
    private val getUserPantryIngredientByIngredientIdUseCase: GetUserPantryIngredientByIngredientIdUseCase
    ) : ViewModel() {

    /** Flag indicating whether the current user has admin privileges. */
    var isAdmin: Boolean = false

    /** State representing whether the add ingredient dialog is open. */
    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen

    /** State representing whether adding an ingredient was successful or not. */
    private val _addIngredientSuccess = MutableStateFlow<Boolean?>(null)
    val addIngredientSuccess: StateFlow<Boolean?> = _addIngredientSuccess

    /** State holding the user's name. */
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    /** State holding the list of pantry ingredients. */
    private val _pantryIngredients = MutableStateFlow<List<PantryIngredientModel>>(emptyList())
    val pantryIngredients: StateFlow<List<PantryIngredientModel>> = _pantryIngredients

    /** State indicating whether data is loading. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** State holding the list of ingredient categories. */
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    /** State holding the list of unit types. */
    private val _units = MutableStateFlow<List<UnitTypeModel>>(emptyList())
    val units: StateFlow<List<UnitTypeModel>> = _units

    /** State holding all ingredients (both user and app ingredients). */
    private val _allIngredients = MutableStateFlow<List<IngredientModel>>(emptyList())
    var allIngredients: StateFlow<List<IngredientModel>> = _allIngredients

    /** State holding only ingredient names (for search or display). */
    private val _ingredientNames = MutableStateFlow<List<String>>(emptyList())
    val ingredientNames: StateFlow<List<String>> = _ingredientNames

    /** State holding ingredients grouped by category for UI sections. */
    private val _ingredientSections = MutableStateFlow<List<IngredientSection>>(emptyList())
    val ingredientSections: StateFlow<List<IngredientSection>> = _ingredientSections

    /** State holding the current error message, if any. */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** State holding the pantry ingredient currently selected for editing. */
    private val _selectedIngredientToEdit = MutableStateFlow<PantryIngredientModel?>(null)
    val selectedIngredientToEdit: StateFlow<PantryIngredientModel?> = _selectedIngredientToEdit

    /** State indicating whether the edit dialog is open. */
    private val _isEditDialogOpen = MutableStateFlow(false)
    val isEditDialogOpen: StateFlow<Boolean> = _isEditDialogOpen

    /** State holding pantry ingredients grouped by category for UI display. */
    private val _groupedIngredients = MutableStateFlow<List<PantryIngredientSection>>(emptyList())
    val groupedIngredients: StateFlow<List<PantryIngredientSection>> = _groupedIngredients

    /** Holds any error message to show in UI */
    private val _snackbarMessage =MutableStateFlow<String>("")
    val snackbarMessage :StateFlow<String> = _snackbarMessage

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

    /**
     * Refreshes pantry data by reloading ingredients, pantry items,
     * categories, and units.
     */
    fun refreshPantry() {
        viewModelScope.launch {
            loadIngredients()
            loadUserPantryIngredients()
            loadCategoriesAndUnits()
            pantryIngredients.collect {
                groupIngredients()
            }
        }
    }

    /**
     * Loads the pantry ingredients for the current user,
     * updating units and categories based on the latest ingredient data.
     */
    private fun loadUserPantryIngredients() {
        viewModelScope.launch {
            _isLoading.value = true
            val userIngredients = getUserPantryIngredientsUseCase()

            val ingredientsMap = _allIngredients.value.associateBy { it.id }

            val updatedIngredients = userIngredients.map { pantry ->
                val baseIngredient = ingredientsMap[pantry.ingredientId]
                val updatedUnit = baseIngredient?.unit ?: pantry.unit
                val updatedCategory = baseIngredient?.category ?: pantry.category

                pantry.copy(
                    unit = updatedUnit,
                    category = updatedCategory
                )
            }

            _pantryIngredients.value = updatedIngredients
            _isLoading.value = false
        }
    }

    /**
     * Loads both user-defined and app default ingredients,
     * detects duplicates, combines them and updates the state accordingly.
     */
    private suspend fun loadIngredients() {
        val allIngredientsUser = getUserIngredientUseCase()
        val duplicates = allIngredientsUser.groupBy { it.name.lowercase() }
            .filter { it.value.size > 1 }

        if (duplicates.isNotEmpty()) {
            Log.w("PantryVM", "Duplicates detected: ${duplicates.keys}")
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

    /**
     * Loads the ingredient categories and unit types from their respective use cases.
     */
    private suspend fun loadCategoriesAndUnits() {
        val categoriesList = getCategoriesUseCase()
        val unitsList = getUnitTypeUseCase()

        _categories.value = categoriesList
        _units.value = unitsList
    }

    /**
     * Retrieves the current user's name from Firebase Firestore
     * and updates the state.
     */
    private fun getUserName() {
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
                        "Error fetching user name: ${e.message}"
                    )
                }
        }
    }

    /**
     * Groups pantry ingredients by their category for UI display.
     */
    private fun groupIngredients() {
        _groupedIngredients.value = pantryIngredients.value
            .groupBy { it.category.name }
            .toSortedMap()
            .map { (category, ingredients) ->
                PantryIngredientSection(category, ingredients.sortedBy { it.name })
            }
    }

    /**
     * Sets the pantry ingredient selected for editing.
     *
     * @param ingredient The pantry ingredient to edit.
     */
    fun setSelectedIngredientToEdit(ingredient: PantryIngredientModel) {
        _selectedIngredientToEdit.value = ingredient
    }

    /**
     * Closes the edit dialog and clears the selected ingredient.
     */
    fun closeEditDialog() {
        _isEditDialogOpen.value = false
        _selectedIngredientToEdit.value = null
    }

    /**
     * Adds a new ingredient to the pantry or updates the quantity if it already exists.
     *
     * @param ingredient The ingredient to add or update.
     * @param quantity The quantity to add.
     */
    fun addOrUpdateIngredientInPantry(ingredient: IngredientModel, quantity: Double) {
        viewModelScope.launch {
            val existingIngredient = getUserPantryIngredientByIngredientIdUseCase(ingredient.id)

            if (existingIngredient != null) {
                val updatedIngredient = existingIngredient.copy(
                    quantity = existingIngredient.quantity + quantity

                )
                _snackbarMessage.emit("Ingrediente actualizado")
                val success = updateUserPantryIngredientUseCase(updatedIngredient)
                if (success) {
                    _pantryIngredients.value = _pantryIngredients.value.map {
                        if (it.id == updatedIngredient.id) updatedIngredient else it
                    }
                } else {
                    Log.e("PantryIngredientsViewModel", "Failed to update existing ingredient")
                }
            } else {
                val newIngredient = PantryIngredientModel(
                    ingredientId = ingredient.id,
                    name = ingredient.name,
                    category = ingredient.category,
                    unit = ingredient.unit,
                    quantity = quantity
                )
                _snackbarMessage.emit("Ingrediente añadido")

                val addedIngredient = addUserPantryIngredientUseCase(newIngredient)
                _pantryIngredients.value = _pantryIngredients.value + addedIngredient
            }
        }
    }

    /**
     * Updates an existing pantry ingredient.
     * If the quantity is zero, deletes the ingredient instead.
     *
     * @param updated The updated pantry ingredient model.
     */
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
                    _snackbarMessage.emit("Ingrediente actualizado")
                } else {
                    Log.e("PantryIngredientsViewModel", "Failed to update ingredient")
                }
            }
        }
    }

    /**
     * Confirms deletion of the currently selected pantry ingredient.
     */
    fun confirmDeleteSelectedIngredientInPantry() {
        _selectedIngredientToEdit.value?.let { ingredient ->
            deleteIngredient(ingredient.id)
            closeEditDialog()
        }
    }

    /**
     * Deletes a pantry ingredient by its ID.
     *
     * @param id The ID of the ingredient to delete.
     */
    fun deleteIngredient(id: String) {
        viewModelScope.launch {
            val success = deleteUserPantryIngredientUseCase(id)
            if (success) {
                loadUserPantryIngredients()
                closeEditDialog()
                _snackbarMessage.emit("Ingrediente eliminado")
            } else {
                Log.e("PantryIngredientsViewModel", "Failed to delete ingredient")
            }
        }
    }

    /**
     * Clears the snackbar message.
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = ""
    }
}
