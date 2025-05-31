package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.PantryIngredientRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull


class PantryIngredientUseCasesTest {

    private lateinit var repository: PantryIngredientRepository

    private lateinit var getUserPantryIngredientsUseCase: GetUserPantryIngredientsUseCase
    private lateinit var addUserPantryIngredientUseCase: AddUserPantryIngredientUseCase
    private lateinit var updateUserPantryIngredientUseCase: UpdateUserPantryIngredientUseCase
    private lateinit var deleteUserPantryIngredientUseCase: DeleteUserPantryIngredientUseCase
    private lateinit var addIngredientsToPantryFromShoppingUseCase: AddIngredientsToPantryFromShoppingUseCase
    private lateinit var getUserPantryIngredientByIngredientIdUseCase: GetUserPantryIngredientByIngredientIdUseCase

    @Before
    fun setup() {
        repository = mock(PantryIngredientRepository::class.java)

        getUserPantryIngredientsUseCase = GetUserPantryIngredientsUseCase(repository)
        addUserPantryIngredientUseCase = AddUserPantryIngredientUseCase(repository)
        updateUserPantryIngredientUseCase = UpdateUserPantryIngredientUseCase(repository)
        deleteUserPantryIngredientUseCase = DeleteUserPantryIngredientUseCase(repository)
        addIngredientsToPantryFromShoppingUseCase = AddIngredientsToPantryFromShoppingUseCase(repository)
        getUserPantryIngredientByIngredientIdUseCase = GetUserPantryIngredientByIngredientIdUseCase(repository)
    }

    @Test
    fun `GetUserPantryIngredientsUseCase should return ingredients`() = runBlockingTest {
        val ingredients = listOf(PantryIngredientModel(id = "1"), PantryIngredientModel(id = "2"))
        `when`(repository.getIngredients()).thenReturn(ingredients)

        val result = getUserPantryIngredientsUseCase.invoke()

        assertEquals(ingredients, result)
        verify(repository).getIngredients()
    }

    @Test
    fun `AddUserPantryIngredientUseCase should add ingredient and return it`() = runBlockingTest {
        val ingredient = PantryIngredientModel(id = "1")
        `when`(repository.addIngredient(ingredient)).thenReturn(ingredient)

        val result = addUserPantryIngredientUseCase.invoke(ingredient)

        assertEquals(ingredient, result)
        verify(repository).addIngredient(ingredient)
    }

    @Test
    fun `UpdateUserPantryIngredientUseCase should update existing ingredient and return true`() = runBlockingTest {
        val ingredient = PantryIngredientModel(id = "1", quantity = 10.0)
        val currentIngredient = PantryIngredientModel(id = "1", quantity = 5.0)
        `when`(repository.getIngredientById(ingredient.id)).thenReturn(currentIngredient)
        `when`(repository.updateIngredient(currentIngredient.copy(quantity = ingredient.quantity))).thenReturn(true)

        val result = updateUserPantryIngredientUseCase.invoke(ingredient)

        assertTrue(result)
        verify(repository).getIngredientById(ingredient.id)
        verify(repository).updateIngredient(currentIngredient.copy(quantity = ingredient.quantity))
    }

    @Test
    fun `UpdateUserPantryIngredientUseCase should return false if ingredient does not exist`() = runBlockingTest {
        val ingredient = PantryIngredientModel(id = "1", quantity = 10.0)
        `when`(repository.getIngredientById(ingredient.id)).thenReturn(null)

        val result = updateUserPantryIngredientUseCase.invoke(ingredient)

        assertFalse(result)
        verify(repository).getIngredientById(ingredient.id)
        verify(repository, never()).updateIngredient(anyOrNull())

    }

    @Test
    fun `DeleteUserPantryIngredientUseCase should delete ingredient and return true`() = runBlockingTest {
        val id = "1"
        `when`(repository.deleteIngredient(id)).thenReturn(true)

        val result = deleteUserPantryIngredientUseCase.invoke(id)

        assertTrue(result)
        verify(repository).deleteIngredient(id)
    }

    @Test
    fun `AddIngredientsToPantryFromShoppingUseCase should add list of ingredients`() = runBlockingTest {
        val ingredients = listOf(PantryIngredientModel(id = "1"), PantryIngredientModel(id = "2"))
        `when`(repository.addIngredientsToPantryFromShopping(ingredients)).thenReturn(ingredients)

        val result = addIngredientsToPantryFromShoppingUseCase.invoke(ingredients)

        assertEquals(ingredients, result)
        verify(repository).addIngredientsToPantryFromShopping(ingredients)
    }

    @Test
    fun `GetUserPantryIngredientByIngredientIdUseCase should return ingredient when found`() = runBlockingTest {
        val ingredientId = "ingredient-1"
        val pantryIngredient = PantryIngredientModel(id = "1", ingredientId = ingredientId)
        `when`(repository.getIngredientById(ingredientId)).thenReturn(pantryIngredient)

        val result = getUserPantryIngredientByIngredientIdUseCase.invoke(ingredientId)

        assertEquals(pantryIngredient, result)
        verify(repository).getIngredientById(ingredientId)
    }

    @Test
    fun `GetUserPantryIngredientByIngredientIdUseCase should return null when not found`() = runBlockingTest {
        val ingredientId = "ingredient-1"
        `when`(repository.getIngredientById(ingredientId)).thenReturn(null)

        val result = getUserPantryIngredientByIngredientIdUseCase.invoke(ingredientId)

        assertNull(result)
        verify(repository).getIngredientById(ingredientId)
    }
}
