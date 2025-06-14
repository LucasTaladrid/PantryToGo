package com.lucasdev.apprecetas.recepies.ui.favorites

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
import com.lucasdev.apprecetas.recepies.domain.usecase.MarkRecipeAsCookedUseCase
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
 * ViewModel responsible for managing the state and business logic
 * for the user's favorite and pending recipes.
 *
 * It handles loading, toggling, and syncing data between the UI and domain layer.
 */
@HiltViewModel
class MyFavoritesRecipesViewModel @Inject constructor(
    private val addToFavoritesUseCase: AddRecipeToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveRecipeFromFavoritesUseCase,
    private val addToPendingUseCase: AddRecipeToPendingUseCase,
    private val removeFromPendingUseCase: RemoveRecipeFromPendingUseCase,
    private val getFavoriteRecipesUseCase: GetFavoritesRecipesUseCase,
    private val getPendingRecipesUseCase: GetPendingRecipesUseCase,
    private val getUserRecipeUseCase: GetUserRecipeUseCase,
    private val getShoppingListsUseCase: GetShoppingListsUseCase,
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes: StateFlow<List<RecipeModel>> = _recipes

    private val _favoriteRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val favoriteRecipes: StateFlow<List<RecipeModel>> = _favoriteRecipes

    private val _pendingRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val pendingRecipes: StateFlow<List<RecipeModel>> = _pendingRecipes


    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Holds any error message to show in UI */
    private val _snackbarMessage = MutableStateFlow<String>("")
    val snackbarMessage: StateFlow<String> = _snackbarMessage

    /** Flag indicating if a recipe save operation is in progress */
    private val _isTogglingPending = MutableStateFlow(false)
    val isTogglingPending: StateFlow<Boolean> = _isTogglingPending

    /**
     * Initializes the ViewModel by loading the user's recipes.
     */
    init {
        viewModelScope.launch {
            loadRecipes()
        }
    }

    /**
     * Refreshes all recipe-related data: user, favorite, and pending recipes.
     */
    fun refresh() {
        viewModelScope.launch {

            loadRecipes()
            loadFavoriteRecipes()
            loadPendingRecipes()

        }
    }

    /**
     * Loads recipes created by the user and updates the state.
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
     * Loads the user's favorite recipes and updates the state.
     */
    private suspend fun loadFavoriteRecipes() {
        _favoriteRecipes.value = getFavoriteRecipesUseCase()
        Log.d("ViewModel", "Favoritos cargados: ${favoriteRecipes.value}")
    }

    /**
     * Loads the user's pending recipes and updates the state.
     */
    private suspend fun loadPendingRecipes() {
        _pendingRecipes.value = getPendingRecipesUseCase()
        Log.d("ViewModel", "Pendientes cargados: ${pendingRecipes.value}")
    }

    /**
     * Checks if a given recipe is marked as favorite.
     *
     * @param recipe The recipe to check.
     * @return True if it's a favorite, false otherwise.
     */
    private fun isFavorite(recipe: RecipeModel): Boolean {
        return _favoriteRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Checks if a given recipe is marked as pending.
     *
     * @param recipe The recipe to check.
     * @return True if it's pending, false otherwise.
     */
    private fun isPending(recipe: RecipeModel): Boolean {
        return _pendingRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Adds or removes a recipe from favorites based on its current state.
     *
     * @param recipe The recipe to toggle.
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

    /**
     * Clears the snackbar message.
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = ""
    }

}

