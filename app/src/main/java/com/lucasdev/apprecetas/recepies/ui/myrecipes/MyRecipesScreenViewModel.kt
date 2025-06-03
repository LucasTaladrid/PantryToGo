package com.lucasdev.apprecetas.recepies.ui.myrecipes

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientSection
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetCategoriesUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetIngredientsUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUnitTypeUseCase
import com.lucasdev.apprecetas.ingredients.domain.usecase.GetUserIngredientUseCase
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeToFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeToPendingUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.DeleteRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetFavoritesRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetPendingRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetUserRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromPendingUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.UpdateRecipeUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the logic and UI state of the MyRecipes screen.
 *
 * Handles loading, creating, editing, and deleting user recipes,
 * as well as managing ingredients, categories, and recipe status (favorites/pending).
 */
@HiltViewModel
class MyRecipesScreenViewModel @Inject constructor(
    private val getUserIngredientUseCase: GetUserIngredientUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getUnitTypeUseCase: GetUnitTypeUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addRecipeUseCase: AddRecipeUseCase,
    private val addToFavoritesUseCase: AddRecipeToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveRecipeFromFavoritesUseCase,
    private val addToPendingUseCase: AddRecipeToPendingUseCase,
    private val removeFromPendingUseCase: RemoveRecipeFromPendingUseCase,
    private val getFavoriteRecipesUseCase: GetFavoritesRecipesUseCase,
    private val getPendingRecipesUseCase: GetPendingRecipesUseCase,
    private val getUserRecipeUseCase: GetUserRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    private val getShoppingListsUseCase: GetShoppingListsUseCase
) : ViewModel() {

    /** StateFlow of available categories for ingredients */
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    /** StateFlow of unit types used for ingredients */
    private val _units = MutableStateFlow<List<UnitTypeModel>>(emptyList())
    val units: StateFlow<List<UnitTypeModel>> = _units

    /** StateFlow containing all ingredients (from user and app) */
    private val _allIngredients = MutableStateFlow<List<IngredientModel>>(emptyList())
    var allIngredients: StateFlow<List<IngredientModel>> = _allIngredients

    /** StateFlow of all ingredient names for quick lookup or suggestions */
    private val _ingredientNames = MutableStateFlow<List<String>>(emptyList())
    val ingredientNames: StateFlow<List<String>> = _ingredientNames

    /** StateFlow of grouped ingredient sections by category */
    private val _ingredientSections = MutableStateFlow<List<IngredientSection>>(emptyList())
    val ingredientSections: StateFlow<List<IngredientSection>> = _ingredientSections

    /** Mutable list of ingredients currently in a recipe being created or edited */
    private val _recipeIngredients = mutableStateListOf<PantryIngredientModel>()
    val recipeIngredients: List<PantryIngredientModel> = _recipeIngredients

    /** StateFlow of all recipes created by the user */
    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes: StateFlow<List<RecipeModel>> = _recipes

    /** StateFlow of recipes marked as favorites */
    private val _favoriteRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val favoriteRecipes: StateFlow<List<RecipeModel>> = _favoriteRecipes

    /** StateFlow of recipes marked as pending (to be made) */
    private val _pendingRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val pendingRecipes: StateFlow<List<RecipeModel>> = _pendingRecipes

    /** Indicates whether a recipe is being saved */
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    /** Controls visibility of the options dialog for a selected recipe */
    private val _showOptionsDialog = MutableStateFlow(false)
    val showOptionsDialog: StateFlow<Boolean> = _showOptionsDialog

    /** Controls visibility of the edit dialog */
    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

    /** Controls visibility of the delete confirmation dialog */
    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation

    /** Currently selected recipe for viewing, editing or deletion */
    private val _selectedRecipe = MutableStateFlow<RecipeModel?>(null)
    val selectedRecipe: StateFlow<RecipeModel?> = _selectedRecipe

    /** Stores an error message for display in the UI */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Indicates whether data is currently being loaded */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Temporarily holds the recipe steps during creation or editing */
    private var steps by mutableStateOf("")

    /** Temporarily holds the recipe name during creation or editing */
    private var recipeName by mutableStateOf("")

    /** Holds any error message to show in UI */
    private val _snackbarMessage =MutableStateFlow<String>("")
    val snackbarMessage :StateFlow<String> = _snackbarMessage

    /** Flag indicating if a recipe save operation is in progress */
    private val _isTogglingPending = MutableStateFlow(false)
    val isTogglingPending: StateFlow<Boolean> = _isTogglingPending




    init {
        viewModelScope.launch {
            loadIngredients()
            loadCategoriesAndUnits()
            loadRecipes()
        }
    }

    /**
     * Reloads all relevant data including ingredients, categories, and user recipes.
     */
    fun refresh() {
        viewModelScope.launch {
            loadIngredients()
            loadCategoriesAndUnits()
            loadRecipes()
            loadFavoriteRecipes()
            loadPendingRecipes()

        }
    }

    /**
     * Loads recipes created by the current user.
     */
    private fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getUserRecipeUseCase()
                _recipes.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar recetas"
                Log.e("RecipeViewModel", "loadRecipes error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads and combines ingredients from the app and user pantry.
     */
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


    /**
     * Loads ingredient categories and unit types.
     */
    private suspend fun loadCategoriesAndUnits() {
        val categoriesList = getCategoriesUseCase()
        val unitsList = getUnitTypeUseCase()

        _categories.value = categoriesList
        _units.value = unitsList
    }

    /**
     * Loads user's favorite recipes.
     */
    private suspend fun loadFavoriteRecipes() {
        _favoriteRecipes.value = getFavoriteRecipesUseCase()
        Log.d("ViewModel", "Favoritos cargados: ${favoriteRecipes.value}")
    }

    /**
     * Loads recipes marked as pending.
     */
    private suspend fun loadPendingRecipes() {
        _pendingRecipes.value = getPendingRecipesUseCase()
        Log.d("ViewModel", "Pendientes cargados: ${pendingRecipes.value}")
    }

    private fun isFavorite(recipe: RecipeModel): Boolean {
        return _favoriteRecipes.value.any { it.id == recipe.id }
    }

    private fun isPending(recipe: RecipeModel): Boolean {
        return _pendingRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Toggles favorite status of a recipe.
     */
    fun toggleFavorite(recipe: RecipeModel) {
        viewModelScope.launch {
            if (isFavorite(recipe)) {
                removeFromFavoritesUseCase(recipe)
                _snackbarMessage.emit("Receta eliminada de favoritos")
            } else {
                addToFavoritesUseCase(recipe)
                _snackbarMessage.emit("Receta añadida a favoritos")
            }
            loadFavoriteRecipes()
        }
    }

    /**
     * Toggles the pending status of a recipe.
     * Adds to pending if not pending; removes otherwise.
     *
     * @param recipe Recipe to toggle pending status.
     */
    fun togglePending(recipe: RecipeModel) {
        viewModelScope.launch {
            if(_isTogglingPending.value) return@launch
            _isTogglingPending.value = true
            try{
                val shoppingLists = getShoppingListsUseCase()
                val activeShoppingList = shoppingLists.firstOrNull()
                if (isPending(recipe) && activeShoppingList!=null) {
                    removeFromPendingUseCase(recipe,activeShoppingList.id)
                    _snackbarMessage.emit("Receta eliminada de pendientes")
                } else {
                    if(activeShoppingList!=null)
                        addToPendingUseCase(recipe,activeShoppingList.id)
                    _snackbarMessage.emit("Receta añadida a pendientes")
                }
                loadPendingRecipes()
            }finally {
                _isTogglingPending.value = false
            }

        }
    }

    /** Updates the recipe name */
    fun onNameChange(new: String) {
        recipeName = new
    }

    /**
     * Adds or updates an ingredient in the current recipe.
     */
    fun addOrUpdateIngredient(item: PantryIngredientModel) {
        val existingIndex = _recipeIngredients.indexOfFirst { it.ingredientId == item.ingredientId }
        if (existingIndex >= 0) {
            _recipeIngredients[existingIndex] = item.copy(
                quantity = item.quantity
            ).also {
                _recipeIngredients[existingIndex] = it.copy()
            }
        } else {
            _recipeIngredients += item
        }
    }

    /**
     * Removes an ingredient from the current recipe.
     */
    fun removeIngredient(item: PantryIngredientModel) {
        _recipeIngredients.remove(item)
    }

    /** Updates the steps string for the current recipe */
    fun onStepsChange(text: String) {
        steps = text
    }

    /**
     * Creates a new recipe if validation passes.
     */
    fun createRecipe(recipeModel: RecipeModel, onSuccess: () -> Unit) {

        if (recipeModel.name.isBlank()
            || recipeModel.ingredients.isEmpty()
            || recipeModel.steps.all { it.isBlank() }
        ) {
            _errorMessage.value = "Rellena nombre, ingredientes y descripción de pasos"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            val saved = addRecipeUseCase(recipeModel)
            _isSaving.value = false

            if (saved != null) {
                onSuccess()
                loadRecipes()
                _snackbarMessage.emit("Receta creada")
            } else {
                _errorMessage.value = "Error al guardar la receta"
            }
        }
    }

    /** Updates a specific ingredient in the current recipe */
    fun updateIngredient(updated: PantryIngredientModel) {
        val idx = _recipeIngredients.indexOfFirst { it.ingredientId == updated.ingredientId }
        if (idx >= 0) _recipeIngredients[idx] = updated
    }

    /** Handles long click on a recipe to show options */
    fun onRecipeLongClick(recipe: RecipeModel) {
        _selectedRecipe.value = recipe
        _showOptionsDialog.value = true
    }
    /** Loads selected recipe into edit form */
    fun editSelectedRecipe() {
        _selectedRecipe.value?.let { recipe ->
            _recipeIngredients.clear()
            _recipeIngredients.addAll(recipe.ingredients)
            recipeName = recipe.name
            steps = recipe.steps.joinToString("\n")
        }
        _showOptionsDialog.value = false
        _showEditDialog.value = true
    }

    /** Prepares delete confirmation dialog */
    fun confirmDeleteSelectedRecipe() {
        _showOptionsDialog.value = false
        _showDeleteConfirmation.value = true
    }

    /**
     * Deletes the selected recipe if it exists.
     */
    fun deleteSelectedRecipe() {
        viewModelScope.launch {
            _selectedRecipe.value?.let { recipe ->
                val success = deleteRecipeUseCase(recipe.id)
                if (success) {
                    clearDialogs()
                    _snackbarMessage.emit("Receta eliminada")
                } else {
                    _errorMessage.value = "Error al eliminar la receta"
                }
            }
        }
    }

    /** Closes all dialogs and clears selection */
    fun clearDialogs() {
        _showOptionsDialog.value = false
        _showEditDialog.value = false
        _showDeleteConfirmation.value = false
        _selectedRecipe.value = null
        loadRecipes()
    }
    //todo, implement in the future and test
    /** Resets the recipe creation form */
    fun resetRecipeForm() {
        _recipeIngredients.clear()
        recipeName = ""
        steps = ""
        _errorMessage.value = null
    }

    /**
     * Updates an existing recipe with new name, ingredients, and steps.
     */
    fun updateRecipe(onSuccess: () -> Unit) {
        val current = _selectedRecipe.value ?: return

        val updatedRecipe = current.copy(
            name = recipeName,
            ingredients = recipeIngredients,
            steps = steps.lines().filter { it.isNotBlank() }
        )
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            val result = updateRecipeUseCase(updatedRecipe)
            _isSaving.value = false

            if (result) {
                onSuccess()
                clearDialogs()
                loadRecipes()
                _snackbarMessage.emit("Receta actualizada")
            } else {
                _errorMessage.value = "Error al actualizar la receta"
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