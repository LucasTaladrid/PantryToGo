package com.lucasdev.apprecetas.recepies.ui.pending

import android.annotation.SuppressLint
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


        init {
            viewModelScope.launch {
                loadRecipes()
            }
        }

        fun refresh() {
            viewModelScope.launch {
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
                val shoppingLists = getShoppingListsUseCase()
                val activeShoppingList = shoppingLists.firstOrNull()
                if (isPending(recipe) && activeShoppingList!=null) {
                    removeFromPendingUseCase(recipe,activeShoppingList.id)
                } else {
                    if(activeShoppingList!=null)
                    addToPendingUseCase(recipe,activeShoppingList.id)
                }
                loadPendingRecipes()
                refresh()
            }
        }

    fun markAsCooked(recipe: RecipeModel) {
        viewModelScope.launch {
            markRecipeAsCookedUseCase(recipe)
        }
        refresh()
    }

    }
