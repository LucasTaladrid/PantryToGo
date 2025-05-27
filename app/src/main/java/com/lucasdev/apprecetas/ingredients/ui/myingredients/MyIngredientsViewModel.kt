package com.lucasdev.apprecetas.ingredients.ui.myingredients


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientSection
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.usecase.AddIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.DeleteIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetCategoriesUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUnitTypeUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUserIngredientUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.UpdateIngredientUseCase
import com.lucasdev.apprecetas.users.domain.usecase.IsAdminUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * ViewModel responsible for managing the user's ingredients.
 * Handles loading, adding, updating, and deleting ingredients,
 * as well as managing UI state such as dialogs and error messages.
 */
@HiltViewModel
class MyIngredientsViewModel @Inject constructor(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val addIngredientUseCase: AddIngredientUseCase,
    private val deleteIngredientUseCase: DeleteIngredientUseCase,
    private val updateIngredientUseCase: UpdateIngredientUseCase,
    private val getUnitTypeUseCase: GetUnitTypeUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getUserIngredientUseCase: GetUserIngredientUseCase,
    private val isAdminUseCase: IsAdminUseCase
) : ViewModel() {

    /** Sections of ingredients grouped by category for display */
    private val _ingredientSections = MutableStateFlow<List<IngredientSection>>(emptyList())
    val ingredientSections: StateFlow<List<IngredientSection>> = _ingredientSections

    /** State of whether the add ingredient dialog is open */
    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen

    /** List of available categories */
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    /** List of available unit types */
    private val _units = MutableStateFlow<List<UnitTypeModel>>(emptyList())
    val units: StateFlow<List<UnitTypeModel>> = _units

    /** Cache of all ingredients (app + user) */
    private var allIngredients = emptyList<IngredientModel>()

    /** Error message to be displayed in the UI, if any */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Controls the visibility of the options dialog (edit/delete) */
    private val _showOptionsDialog = MutableStateFlow(false)
    val showOptionsDialog: StateFlow<Boolean> = _showOptionsDialog

    /** Controls the visibility of the delete confirmation dialog */
    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation

    /** Controls the visibility of the edit ingredient dialog */
    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

    /** Currently selected ingredient for edit/delete operations */
    private val _selectedIngredient = MutableStateFlow<IngredientModel?>(null)
    val selectedIngredient: StateFlow<IngredientModel?> = _selectedIngredient

    /** Indicates if the current user has admin privileges */
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    /** List of all ingredient names, used to check duplicates */
    private val _ingredientNames = MutableStateFlow<List<String>>(emptyList())
    val ingredientNames: StateFlow<List<String>> = _ingredientNames

    /** Ingredient that was detected as duplicate when trying to add */
    private val _duplicateIngredient = MutableStateFlow<IngredientModel?>(null)
    val duplicateIngredient: StateFlow<IngredientModel?> = _duplicateIngredient

    init {
        viewModelScope.launch {
            loadIngredients()
            loadCategoriesAndUnits()
            loadIngredientsForDisplay()
            checkIfUserIsAdmin()
        }
    }

    /** Checks if the current user has admin rights */
    private fun checkIfUserIsAdmin() {
        viewModelScope.launch {
            _isAdmin.value = isAdminUseCase()
        }
    }

    /** Loads categories and unit types from the repository */
    private suspend fun loadCategoriesAndUnits() {
        val categoriesList = getCategoriesUseCase()
        val unitsList = getUnitTypeUseCase()
        _categories.value = categoriesList
        _units.value = unitsList
    }

    /**
     * Loads all ingredients belonging to the user and the app,
     * groups them by category, and updates the UI state.
     */
    private suspend fun loadIngredients() {
        val allIngredientsUser = getUserIngredientUseCase()
        val allIngredientsApp = getIngredientsUseCase()
        this.allIngredients = allIngredientsApp + allIngredientsUser

        _ingredientNames.value = allIngredients.map { it.name }

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
     * Loads ingredients for display, showing either all ingredients (admin)
     * or only user ingredients, grouped by category.
     */
    suspend fun loadIngredientsForDisplay() {
        val isAdmin = isAdminUseCase()

        val ingredients = if (isAdmin) {
            getIngredientsUseCase()
        } else {
            getUserIngredientUseCase()
        }

        _ingredientSections.value = ingredients
            .groupBy { it.category.name }
            .map { (cat, list) ->
                IngredientSection(
                    category = cat,
                    ingredients = list.sortedBy { it.name }
                )
            }
            .sortedBy { it.category }
    }

    /** Opens the dialog for adding a new ingredient */
    fun showAddIngredientDialog() {
        _isDialogOpen.value = true
    }

    /** Closes the add ingredient dialog and resets errors */
    fun hideAddIngredientDialog() {
        _isDialogOpen.value = false
        _errorMessage.value = null
    }

    /** Closes all open dialogs */
    fun hideDialog() {
        _showEditDialog.value = false
        _showOptionsDialog.value = false
        _showDeleteConfirmation.value = false
    }

    /** Shows the edit ingredient dialog */
    fun showEditDialog() {
        _showOptionsDialog.value = false
        _showEditDialog.value = true
    }

    /** Shows the confirmation dialog for deleting an ingredient */
    fun showDeleteConfirmationDialog() {
        _showOptionsDialog.value = false
        _showDeleteConfirmation.value = true
    }

    /** Dismisses the duplicate ingredient warning dialog */
    fun dismissDuplicateDialog() {
        _duplicateIngredient.value = null
    }

    /** Handles a long press on an ingredient item to show options */
    fun onIngredientLongPress(ingredient: IngredientModel) {
        _selectedIngredient.value = ingredient
        _showOptionsDialog.value = true
    }

    /**
     * Attempts to add a new ingredient after checking for duplicates.
     * Shows an error if ingredient already exists.
     */
    fun addNewIngredient(name: String, category: CategoryModel, unit: UnitTypeModel) {
        val duplicate = allIngredients.find { it.name.equals(name, ignoreCase = true) }

        if (duplicate != null) {
            _duplicateIngredient.value = duplicate
            _errorMessage.value = "Ingredient already exists"
            return
        }

        viewModelScope.launch {
            val ingredient = IngredientModel(
                name = name,
                category = category,
                unit = unit
            )
            val success = addIngredientUseCase(ingredient)
            if (success) {
                hideAddIngredientDialog()
                loadIngredients()
                loadIngredientsForDisplay()
            }
        }
    }

    /** Deletes the currently selected ingredient */
    fun deleteIngredient() {
        viewModelScope.launch {
            _selectedIngredient.value?.let { ingredient ->
                deleteIngredientUseCase(ingredient.id)
                loadIngredients()
                loadIngredientsForDisplay()
            }
        }
        _showDeleteConfirmation.value = false
    }

    /**
     * Updates the selected ingredient with new category and unit,
     * then reloads the displayed ingredients or shows error on failure.
     */
    fun updateIngredient(category: CategoryModel, unit: UnitTypeModel) {
        viewModelScope.launch {
            _selectedIngredient.value?.let { ingredient ->
                val updatedIngredient = ingredient.copy(category = category, unit = unit)
                val success = updateIngredientUseCase(updatedIngredient)
                if (success) {
                    loadIngredientsForDisplay()
                } else {
                    _errorMessage.value = "Failed to update ingredient"
                }
            }
            _showEditDialog.value = false
        }
    }
}
