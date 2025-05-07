package com.lucasdev.apprecetas.ingredients.ui


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

    private val _ingredientSections = MutableStateFlow<List<IngredientSection>>(emptyList())
    val ingredientSections: StateFlow<List<IngredientSection>> = _ingredientSections

    private val _isDialogOpen = MutableStateFlow(false)
    val isDialogOpen: StateFlow<Boolean> = _isDialogOpen

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    private val _units = MutableStateFlow<List<UnitTypeModel>>(emptyList())
    val units: StateFlow<List<UnitTypeModel>> = _units

    private var allIngredients = emptyList<IngredientModel>()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _showOptionsDialog = MutableStateFlow(false)
    val showOptionsDialog: StateFlow<Boolean> = _showOptionsDialog

    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

    private val _selectedIngredient = MutableStateFlow<IngredientModel?>(null)
    val selectedIngredient: StateFlow<IngredientModel?> = _selectedIngredient

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _ingredientNames = MutableStateFlow<List<String>>(emptyList())
    val ingredientNames: StateFlow<List<String>> = _ingredientNames


    init {
        viewModelScope.launch {
            loadIngredients()
            loadCategoriesAndUnits()
            loadIngredientsForDisplay()
            checkIfUserIsAdmin()
        }
    }
    //Functions that i need to init in this screen
    private fun checkIfUserIsAdmin() {
        viewModelScope.launch {
            _isAdmin.value = isAdminUseCase()

        }

    }
    private suspend fun loadCategoriesAndUnits() {
        val categoriesList = getCategoriesUseCase()
        val unitsList = getUnitTypeUseCase()

        _categories.value = categoriesList
        _units.value = unitsList
    }

    private suspend fun loadIngredients() {
        val allIngredientsUser = getUserIngredientUseCase()
        val allIngredientsApp = getIngredientsUseCase()
        this.allIngredients = allIngredientsApp +allIngredientsUser

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

    //Functions to open and close dialogs
    fun showAddIngredientDialog() {
        _isDialogOpen.value = true
    }

    fun hideAddIngredientDialog() {
        _isDialogOpen.value = false
        _errorMessage.value = null
    }

    fun hideDialog() {
        _showEditDialog.value = false
        _showOptionsDialog.value = false
        _showDeleteConfirmation.value = false
    }

    fun showEditDialog() {
        _showOptionsDialog.value = false
        _showEditDialog.value = true
    }

    fun showDeleteConfirmationDialog() {
        _showOptionsDialog.value = false
        _showDeleteConfirmation.value = true
    }

    fun onIngredientLongPress(ingredient: IngredientModel) {
        _selectedIngredient.value = ingredient
        _showOptionsDialog.value = true
    }


    //Functions to add, delete and update ingredients

    fun addNewIngredient(name: String, category: CategoryModel, unit: UnitTypeModel) {

        val alreadyExists = allIngredients.any { it.name.equals(name, ignoreCase = true) }

        if (alreadyExists) {
            _errorMessage.value = "El ingrediente ya existe"
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

    fun updateIngredient(category: CategoryModel, unit: UnitTypeModel) {
        viewModelScope.launch {
            _selectedIngredient.value?.let { ingredient ->
                val updatedIngredient = ingredient.copy(category = category, unit = unit)
                val success = updateIngredientUseCase(updatedIngredient)
                if (success) {
                    loadIngredientsForDisplay()
                } else {
                    _errorMessage.value = "Error al actualizar el ingrediente"
                }
            }
            _showEditDialog.value = false
        }
    }

}
