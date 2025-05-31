package com.lucasdev.apprecetas.recipes.domain.usecase

import com.google.firebase.Timestamp
import com.lucasdev.apprecetas.ingredients.domain.model.CategoryModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.UnitTypeModel
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import com.lucasdev.apprecetas.recepies.domain.repository.RecipeRepository
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeToFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeToPendingUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.AddRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.DeleteRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetCommonRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetFavoritesRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetPendingRecipesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.GetUserRecipeUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.MarkRecipeAsCookedUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromFavoritesUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.RemoveRecipeFromPendingUseCase
import com.lucasdev.apprecetas.recepies.domain.usecase.UpdateRecipeUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class RecipeUseCasesTest {

    private lateinit var repository: RecipeRepository

    private val testRecipe = RecipeModel(
        id = "1",
        name = "Test Recipe",
        ingredients = listOf(
            PantryIngredientModel(
                id = "pi1",
                ingredientId = "i1",
                name = "Tomato",
                category = CategoryModel( name = "Vegetables"),
                unit = UnitTypeModel( name = "grams"),
                quantity = 2.0
            )
        ),
        steps = listOf("Step 1", "Step 2"),
        dateCreated = Timestamp.now()
    )


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = mock(RecipeRepository::class.java)
    }

    @Test
    fun `AddRecipeUseCase should call repository addRecipe`() = runBlocking {
        val useCase = AddRecipeUseCase(repository)
        `when`(repository.addRecipe(testRecipe)).thenReturn(testRecipe)

        val result = useCase.invoke(testRecipe)

        verify(repository).addRecipe(testRecipe)
        assert(result == testRecipe)
    }

    @Test
    fun `GetCommonRecipesUseCase should call repository getCommonRecipes`() = runBlocking {
        val useCase = GetCommonRecipesUseCase(repository)
        val recipes = listOf(testRecipe)
        `when`(repository.getCommonRecipes()).thenReturn(recipes)

        val result = useCase.invoke()

        verify(repository).getCommonRecipes()
        assert(result == recipes)
    }

    @Test
    fun `AddRecipeToFavoritesUseCase should call repository addToFavorites`() = runBlocking {
        val useCase = AddRecipeToFavoritesUseCase(repository)

        useCase.invoke(testRecipe)

        verify(repository).addToFavorites(testRecipe)
    }

    @Test
    fun `RemoveRecipeFromFavoritesUseCase should call repository removeFromFavorites`() = runBlocking {
        val useCase = RemoveRecipeFromFavoritesUseCase(repository)

        useCase.invoke(testRecipe)

        verify(repository).removeFromFavorites(testRecipe)
    }

    @Test
    fun `AddRecipeToPendingUseCase should call repository addToPending`() = runBlocking {
        val useCase = AddRecipeToPendingUseCase(repository)
        val shoppingListId = "shoppingList1"

        useCase.invoke(testRecipe, shoppingListId)

        verify(repository).addToPending(testRecipe, shoppingListId)
    }

    @Test
    fun `RemoveRecipeFromPendingUseCase should call repository removeFromPending`() = runBlocking {
        val useCase = RemoveRecipeFromPendingUseCase(repository)
        val shoppingListId = "shoppingList1"

        useCase.invoke(testRecipe, shoppingListId)

        verify(repository).removeFromPending(testRecipe, shoppingListId)
    }

    @Test
    fun `GetFavoritesRecipesUseCase should call repository getFavoriteRecipes`() = runBlocking {
        val useCase = GetFavoritesRecipesUseCase(repository)
        val favorites = listOf(testRecipe)
        `when`(repository.getFavoriteRecipes()).thenReturn(favorites)

        val result = useCase.invoke()

        verify(repository).getFavoriteRecipes()
        assert(result == favorites)
    }

    @Test
    fun `GetPendingRecipesUseCase should call repository getPendingRecipes`() = runBlocking {
        val useCase = GetPendingRecipesUseCase(repository)
        val pending = listOf(testRecipe)
        `when`(repository.getPendingRecipes()).thenReturn(pending)

        val result = useCase.invoke()

        verify(repository).getPendingRecipes()
        assert(result == pending)
    }

    @Test
    fun `GetUserRecipeUseCase should call repository getUserRecipes`() = runBlocking {
        val useCase = GetUserRecipeUseCase(repository)
        val userRecipes = listOf(testRecipe)
        `when`(repository.getUserRecipes()).thenReturn(userRecipes)

        val result = useCase.invoke()

        verify(repository).getUserRecipes()
        assert(result == userRecipes)
    }

    @Test
    fun `DeleteRecipeUseCase should call repository deleteRecipe`() = runBlocking {
        val useCase = DeleteRecipeUseCase(repository)
        val recipeId = "1"
        `when`(repository.deleteRecipe(recipeId)).thenReturn(true)

        val result = useCase.invoke(recipeId)

        verify(repository).deleteRecipe(recipeId)
        assert(result)
    }

    @Test
    fun `UpdateRecipeUseCase should call repository updateRecipe`() = runBlocking {
        val useCase = UpdateRecipeUseCase(repository)
        `when`(repository.updateRecipe(testRecipe)).thenReturn(true)

        val result = useCase.invoke(testRecipe)

        verify(repository).updateRecipe(testRecipe)
        assert(result)
    }

    @Test
    fun `MarkRecipeAsCookedUseCase should call repository markRecipeAsCooked`() = runBlocking {
        val useCase = MarkRecipeAsCookedUseCase(repository)

        useCase.invoke(testRecipe)

        verify(repository).markRecipeAsCooked(testRecipe)
    }
}
