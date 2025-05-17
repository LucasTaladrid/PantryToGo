package com.lucasdev.apprecetas.recepies.ui

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
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getUserIngredientUseCase: GetUserIngredientUseCase,
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getUnitTypeUseCase: GetUnitTypeUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getRecipeUseCase: GetRecipeUseCase,
    private val addRecipeUseCase: AddRecipeUseCase
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

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

    private val _isSaving = MutableStateFlow(false)
    val isSaving:  StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var steps by mutableStateOf("")

    private var recipeName by mutableStateOf("")


    init {
        viewModelScope.launch {
            getUserName()
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

        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getRecipeUseCase()
                _recipes.value=result
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





    fun onNameChange(new: String) {
        recipeName = new
    }

    /** UI llama a esto para añadir o acumular un ingrediente con cantidad */
    fun addOrUpdateIngredient(item: PantryIngredientModel) {
        val idx = _recipeIngredients.indexOfFirst { it.ingredientId == item.ingredientId }
        if (idx >= 0) {
            val existing = _recipeIngredients[idx]
            _recipeIngredients[idx] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            _recipeIngredients += item
        }
    }

    /** UI llama a esto para eliminar un ingrediente de la receta */
    fun removeIngredient(item: PantryIngredientModel) {
        _recipeIngredients.remove(item)
    }

    /** UI llama a esto cuando cambia el campo “pasos” */
    fun onStepsChange(text: String) {
        steps = text
    }

    /** Lanza el caso de uso de “añadir receta” */
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
}

