package com.lucasdev.apprecetas.recepies.ui

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val updateRecipeUseCase: UpdateRecipeUseCase
) : ViewModel() {

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

    private val _recipeIngredients = mutableStateListOf<PantryIngredientModel>()
    val recipeIngredients: List<PantryIngredientModel> = _recipeIngredients

    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes: StateFlow<List<RecipeModel>> = _recipes

    private val _favoriteRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val favoriteRecipes: StateFlow<List<RecipeModel>> = _favoriteRecipes

    private val _pendingRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val pendingRecipes: StateFlow<List<RecipeModel>> = _pendingRecipes

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _showOptionsDialog = MutableStateFlow(false)
    val showOptionsDialog: StateFlow<Boolean> = _showOptionsDialog

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

    private val _showDeleteConfirmation = MutableStateFlow(false)
    val showDeleteConfirmation: StateFlow<Boolean> = _showDeleteConfirmation

    private val _selectedRecipe = MutableStateFlow<RecipeModel?>(null)
    val selectedRecipe: StateFlow<RecipeModel?> = _selectedRecipe

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var steps by mutableStateOf("")

    private var recipeName by mutableStateOf("")

    init {
        viewModelScope.launch {
            loadIngredients()
            loadCategoriesAndUnits()
            loadRecipes()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            loadIngredients()
            loadCategoriesAndUnits()
            loadRecipes()
            loadFavoriteRecipes()
            loadPendingRecipes()

        }
    }

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

    private suspend fun loadFavoriteRecipes() {
        _favoriteRecipes.value = getFavoriteRecipesUseCase()
        Log.d("ViewModel", "Favoritos cargados: ${favoriteRecipes.value}")
    }

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

    fun toggleFavorite(recipe: RecipeModel) {
        viewModelScope.launch {
            if (isFavorite(recipe)) {
                removeFromFavoritesUseCase(recipe)
            } else {
                addToFavoritesUseCase(recipe)
            }
            loadFavoriteRecipes()
        }
    }

    fun togglePending(recipe: RecipeModel) {
        viewModelScope.launch {
            if (isPending(recipe)) {
                removeFromPendingUseCase(recipe)
            } else {
                addToPendingUseCase(recipe)
            }
            loadPendingRecipes()
        }
    }

    fun onNameChange(new: String) {
        recipeName = new
    }

    fun addOrUpdateIngredient(item: PantryIngredientModel) {
        val idx = _recipeIngredients.indexOfFirst { it.ingredientId == item.ingredientId }
        if (idx >= 0) {
            val existing = _recipeIngredients[idx]
            _recipeIngredients[idx] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            _recipeIngredients += item
        }
    }

    fun removeIngredient(item: PantryIngredientModel) {
        _recipeIngredients.remove(item)
    }

    fun onStepsChange(text: String) {
        steps = text
    }

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
            } else {
                _errorMessage.value = "Error al guardar la receta"
            }
        }
    }

    //todo lógico para borrar y modificar recetas
    fun updateIngredient(updated: PantryIngredientModel) {
        val idx = _recipeIngredients.indexOfFirst { it.ingredientId == updated.ingredientId }
        if (idx >= 0) _recipeIngredients[idx] = updated
    }


    fun onRecipeLongClick(recipe: RecipeModel) {
        _selectedRecipe.value = recipe
        _showOptionsDialog.value = true
    }

    fun editSelectedRecipe() {
        _selectedRecipe.value?.let { recipe ->
            _recipeIngredients.clear()
            _recipeIngredients.addAll(recipe.ingredients)
            recipeName = recipe.name
            steps = recipe.steps.joinToString("\n") // O mantener steps como lista, como prefieras
        }
        _showOptionsDialog.value = false
        _showEditDialog.value = true
    }


    fun confirmDeleteSelectedRecipe() {
        _showOptionsDialog.value = false
        _showDeleteConfirmation.value = true
    }

    fun deleteSelectedRecipe() {
        viewModelScope.launch {
            _selectedRecipe.value?.let { recipe ->
                val success = deleteRecipeUseCase(recipe.id)
                if (success) {
                    clearDialogs()
                } else {
                    _errorMessage.value = "Error al eliminar la receta"
                }
            }
        }
    }

    fun clearDialogs() {
        _showOptionsDialog.value = false
        _showEditDialog.value = false
        _showDeleteConfirmation.value = false
        _selectedRecipe.value = null
        loadRecipes()
    }

    fun resetRecipeForm() {
        _recipeIngredients.clear()
        recipeName = ""
        steps = ""
        _errorMessage.value = null
    }

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
            } else {
                _errorMessage.value = "Error al actualizar la receta"
            }
        }
    }



}