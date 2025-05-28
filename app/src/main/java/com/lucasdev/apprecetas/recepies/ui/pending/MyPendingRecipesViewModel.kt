package com.lucasdev.apprecetas.recepies.ui.pending

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeToFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeToPendingUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetFavoritesRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetPendingRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.MarkRecipeAsCookedUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromPendingUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state and logic related to user's pending recipes.
 *
 * It handles loading, marking, and toggling recipes between favorites and pending lists.
 */
@HiltViewModel
class MyPendingRecipesViewModel @Inject constructor(
    private val addToFavoritesUseCase: AddRecipeToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveRecipeFromFavoritesUseCase,
    private val addToPendingUseCase: AddRecipeToPendingUseCase,
    private val removeFromPendingUseCase: RemoveRecipeFromPendingUseCase,
    private val getFavoriteRecipesUseCase: GetFavoritesRecipesUseCase,
    private val getPendingRecipesUseCase: GetPendingRecipesUseCase,
    private val getShoppingListsUseCase: GetShoppingListsUseCase,
    private val markRecipeAsCookedUseCase: MarkRecipeAsCookedUseCase
) : ViewModel() {

    /** State holding the list of ingredient categories. */
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    /** State holding the list of currently loaded pending recipes. */
    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes: StateFlow<List<RecipeModel>> = _recipes

    /** State holding the user's favorite recipes. */
    private val _favoriteRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val favoriteRecipes: StateFlow<List<RecipeModel>> = _favoriteRecipes

    /** State holding the recipes marked as pending. */
    private val _pendingRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val pendingRecipes: StateFlow<List<RecipeModel>> = _pendingRecipes

    /** State representing the current error message, if any. */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Loading state indicator for async operations. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    init {
        viewModelScope.launch {
            loadRecipes()
        }
    }

    /**
     * Refreshes the recipes data, including all types (pending, favorites, etc.).
     */
    fun refresh() {
        viewModelScope.launch {
            loadRecipes()
            loadFavoriteRecipes()
            loadPendingRecipes()

        }
    }

    /**
     * Loads the user's pending recipes.
     */
    private fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getPendingRecipesUseCase()
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
     * Loads the user's favorite recipes.
     */
    private suspend fun loadFavoriteRecipes() {
        _favoriteRecipes.value = getFavoriteRecipesUseCase()
        Log.d("ViewModel", "Favoritos cargados: ${favoriteRecipes.value}")
    }

    /**
     * Loads the user's pending recipes.
     */
    private suspend fun loadPendingRecipes() {
        _pendingRecipes.value = getPendingRecipesUseCase()
        Log.d("ViewModel", "Pendientes cargados: ${pendingRecipes.value}")
    }

    /**
     * Checks if a recipe is in the favorites list.
     * @param recipe The recipe to check.
     * @return True if the recipe is in favorites.
     */
    private fun isFavorite(recipe: RecipeModel): Boolean {
        return _favoriteRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Checks if a recipe is in the pending list.
     * @param recipe The recipe to check.
     * @return True if the recipe is pending.
     */
    private fun isPending(recipe: RecipeModel): Boolean {
        return _pendingRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Toggles a recipe's favorite status.
     * @param recipe The recipe to toggle.
     */
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

    /**
     * Toggles a recipe's pending status. Adds or removes it from the current active shopping list.
     * @param recipe The recipe to toggle.
     */
    fun togglePending(recipe: RecipeModel) {
        viewModelScope.launch {
            val shoppingLists = getShoppingListsUseCase()
            val activeShoppingList = shoppingLists.firstOrNull()
            if (isPending(recipe) && activeShoppingList != null) {
                removeFromPendingUseCase(recipe, activeShoppingList.id)
            } else {
                if (activeShoppingList != null)
                    addToPendingUseCase(recipe, activeShoppingList.id)
            }
            loadPendingRecipes()
            refresh()
        }
    }

    /**
     * Marks a recipe as cooked, which likely removes it from the pending list or flags it.
     * @param recipe The recipe to mark as cooked.
     */
    fun markAsCooked(recipe: RecipeModel) {
        viewModelScope.launch {
            markRecipeAsCookedUseCase(recipe)
        }
        refresh()
    }

}
