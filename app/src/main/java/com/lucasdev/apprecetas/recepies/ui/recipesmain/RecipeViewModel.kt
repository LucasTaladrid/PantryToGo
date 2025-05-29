package com.lucasdev.apprecetas.recepies.ui.recipesmain

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import com.lucasdev.apprecetas.recepies.domain.usecase.GetFavoritesRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetPendingRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetCommonRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromPendingUseCase
import com.lucasdev.apprecetas.shopping.domain.usecase.GetShoppingListsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * ViewModel responsible for handling recipe-related data and operations.
 *
 * This ViewModel manages loading and storing recipes, ingredients, categories, units,
 * and user data. It also handles adding/removing recipes from favorites and pending lists,
 * as well as creating new recipes.
 *
 * @property getUserIngredientUseCase Use case to fetch user's custom ingredients.
 * @property getIngredientsUseCase Use case to fetch app default ingredients.
 * @property getUnitTypeUseCase Use case to fetch unit types for ingredients.
 * @property getCategoriesUseCase Use case to fetch ingredient categories.
 * @property getCommonRecipesUseCase Use case to fetch all common recipes.
 * @property addRecipeUseCase Use case to add a new recipe.
 * @property addToFavoritesUseCase Use case to add a recipe to favorites.
 * @property removeFromFavoritesUseCase Use case to remove a recipe from favorites.
 * @property addToPendingUseCase Use case to add a recipe to pending list.
 * @property removeFromPendingUseCase Use case to remove a recipe from pending list.
 * @property getFavoriteRecipesUseCase Use case to fetch favorite recipes.
 * @property getPendingRecipesUseCase Use case to fetch pending recipes.
 * @property getShoppingListsUseCase Use case to fetch shopping lists.
 */
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getUserIngredientUseCase: GetUserIngredientUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getUnitTypeUseCase: GetUnitTypeUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCommonRecipesUseCase: GetCommonRecipesUseCase,
    private val addRecipeUseCase: AddRecipeUseCase,
    private val addToFavoritesUseCase: AddRecipeToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveRecipeFromFavoritesUseCase,
    private val addToPendingUseCase: AddRecipeToPendingUseCase,
    private val removeFromPendingUseCase: RemoveRecipeFromPendingUseCase,
    private val getFavoriteRecipesUseCase: GetFavoritesRecipesUseCase,
    private val getPendingRecipesUseCase: GetPendingRecipesUseCase,
    private val getShoppingListsUseCase: GetShoppingListsUseCase
) : ViewModel() {

    /** User's display name */
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    /** List of ingredient categories */
    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    /** List of unit types for ingredients */
    private val _units = MutableStateFlow<List<UnitTypeModel>>(emptyList())
    val units: StateFlow<List<UnitTypeModel>> = _units

    /** Combined list of all ingredients (app + user) */
    private val _allIngredients = MutableStateFlow<List<IngredientModel>>(emptyList())
    var allIngredients: StateFlow<List<IngredientModel>> = _allIngredients

    /** List of ingredient names for autocomplete or search */
    private val _ingredientNames = MutableStateFlow<List<String>>(emptyList())
    val ingredientNames: StateFlow<List<String>> = _ingredientNames

    /** Ingredients grouped by category for UI sections */
    private val _ingredientSections = MutableStateFlow<List<IngredientSection>>(emptyList())
    val ingredientSections: StateFlow<List<IngredientSection>> = _ingredientSections

    /** Ingredients currently selected for a recipe */
    private val _recipeIngredients = mutableStateListOf<PantryIngredientModel>()
    val recipeIngredients: List<PantryIngredientModel> = _recipeIngredients

    /** List of all available recipes */
    private val _recipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val recipes: StateFlow<List<RecipeModel>> = _recipes

    /** List of user's favorite recipes */
    private val _favoriteRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val favoriteRecipes: StateFlow<List<RecipeModel>> = _favoriteRecipes

    /** List of user's pending recipes */
    private val _pendingRecipes = MutableStateFlow<List<RecipeModel>>(emptyList())
    val pendingRecipes: StateFlow<List<RecipeModel>> = _pendingRecipes

    /** Flag indicating if a recipe save operation is in progress */
    private val _isSaving = MutableStateFlow(false)
    val isSaving:  StateFlow<Boolean> = _isSaving

    /** Holds any error message to show in UI */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** Flag indicating if data loading is in progress */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Current steps/description for the recipe being created/edited */
    private var steps by mutableStateOf("")

    /** Current recipe name being edited or created */
    private var recipeName by mutableStateOf("")

    /** Holds any error message to show in UI */
    private val _snackbarMessage =MutableStateFlow<String>("")
    val snackbarMessage :StateFlow<String> = _snackbarMessage


    init {
        viewModelScope.launch {
            getUserName()
            loadIngredients()
            loadCategoriesAndUnits()
            loadRecipes()
            loadFavoriteRecipes()
            loadPendingRecipes()
        }
    }

    /**
     * Refreshes all data, useful for pull-to-refresh or manual reload.
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
     * Loads all common recipes and updates the state.
     */
    private fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getCommonRecipesUseCase()
                _recipes.value=result
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar recetas"
                Log.e("RecipeViewModel", "loadRecipes error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Loads user's ingredients combined with app default ingredients,
     * removes duplicates, and prepares ingredient sections by category.
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
     * Checks if a recipe is marked as favorite.
     *
     * @param recipe Recipe to check.
     * @return True if recipe is favorite, false otherwise.
     */
    private fun isFavorite(recipe: RecipeModel): Boolean {
        return _favoriteRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Checks if a recipe is marked as pending.
     *
     * @param recipe Recipe to check.
     * @return True if recipe is pending, false otherwise.
     */
    private fun isPending(recipe: RecipeModel): Boolean {
        return _pendingRecipes.value.any { it.id == recipe.id }
    }

    /**
     * Toggles the favorite status of a recipe.
     * Adds to favorites if not favorite; removes otherwise.
     *
     * @param recipe Recipe to toggle favorite status.
     */
    fun toggleFavorite(recipe: RecipeModel) {
        viewModelScope.launch {
            if (isFavorite(recipe)) {
                removeFromFavoritesUseCase(recipe)
                _snackbarMessage.emit("Receta quitada de favoritos")
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
            val shoppingLists = getShoppingListsUseCase()
            val activeShoppingList = shoppingLists.firstOrNull()
            if (isPending(recipe) && activeShoppingList!=null) {
                removeFromPendingUseCase(recipe,activeShoppingList.id)
                _snackbarMessage.emit("Receta quitada de pendientes")
            } else {
                if(activeShoppingList!=null)
                    addToPendingUseCase(recipe,activeShoppingList.id)
                _snackbarMessage.emit("Receta añadida a pendientes")
            }
            loadPendingRecipes()
        }
    }

    /**
     * Fetches the current user's display name from Firestore and updates the state.
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
                        "Error al obtener nombre de usuario: ${e.message}"
                    )
                }
        }
    }

    /**
     * Updates the recipe name during creation/editing.
     *
     * @param new New name for the recipe.
     */
    fun onNameChange(new: String) {
        recipeName = new
    }

    /**
     * Adds a new ingredient or updates the quantity if it already exists in the current recipe.
     *
     * @param item Ingredient to add or update.
     */
    fun addOrUpdateIngredient(item: PantryIngredientModel) {
        val idx = _recipeIngredients.indexOfFirst { it.ingredientId == item.ingredientId }
        if (idx >= 0) {
            val existing = _recipeIngredients[idx]
            _recipeIngredients[idx] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            _recipeIngredients += item
        }
    }

    /**
     * Removes an ingredient from the current recipe.
     *
     * @param item Ingredient to remove.
     */
    fun removeIngredient(item: PantryIngredientModel) {
        _recipeIngredients.remove(item)
    }

    /**
     * Updates the recipe steps/description during creation/editing.
     *
     * @param text New steps text.
     */
    fun onStepsChange(text: String) {
        steps = text
    }

    /**
     * Creates a new recipe after validating mandatory fields.
     *
     * @param recipeModel Recipe data to save.
     * @param onSuccess Callback invoked on successful save.
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
                _snackbarMessage.emit("Receta guardada")
                onSuccess()
                loadRecipes()
            } else {
                _snackbarMessage.emit( "Error al guardar la receta")
            }
        }
    }

    /**
     * Updates an existing ingredient in the current recipe.
     *
     * @param updated Updated ingredient information.
     */
    fun updateIngredient(updated: PantryIngredientModel) {
        val idx = _recipeIngredients.indexOfFirst { it.ingredientId == updated.ingredientId }
        if (idx >= 0) _recipeIngredients[idx] = updated
    }

    /**
     * Clears the snackbar message.
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = ""
    }

}

