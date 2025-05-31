package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`


class IngredientUseCasesTest {
    private lateinit var repository: IngredientRepository

    private lateinit var getIngredientsUseCase: GetIngredientsUseCase
    private lateinit var getCommonIngredientsUseCase: GetCommonIngredientsUseCase
    private lateinit var addIngredientUseCase: AddIngredientUseCase
    private lateinit var updateIngredientUseCase: UpdateIngredientUseCase
    private lateinit var deleteIngredientUseCase: DeleteIngredientUseCase
    private lateinit var getUserIngredientUseCase: GetUserIngredientUseCase

    @Before
    fun setup() {
        repository = mock(IngredientRepository::class.java)

        getIngredientsUseCase = GetIngredientsUseCase(repository)
        getCommonIngredientsUseCase = GetCommonIngredientsUseCase(repository)
        addIngredientUseCase = AddIngredientUseCase(repository)
        updateIngredientUseCase = UpdateIngredientUseCase(repository)
        deleteIngredientUseCase = DeleteIngredientUseCase(repository)
        getUserIngredientUseCase = GetUserIngredientUseCase(repository)
    }

    @Test
    fun `GetIngredientsUseCase should return ingredients`() = runBlockingTest {
        val mockIngredients = listOf(
            IngredientModel(id = "1", name = "Tomato"),
            IngredientModel(id = "2", name = "Salt")
        )
        `when`(repository.getIngredients()).thenReturn(mockIngredients)

        val result = getIngredientsUseCase.invoke()

        assertEquals(mockIngredients, result)
        verify(repository).getIngredients()
    }

    @Test
    fun `GetCommonIngredientsUseCase should return common ingredients`() = runBlockingTest {
        val mockIngredients = listOf(
            IngredientModel(id = "1", name = "Oil"),
            IngredientModel(id = "2", name = "Water")
        )
        `when`(repository.getCommonIngredients()).thenReturn(mockIngredients)

        val result = getCommonIngredientsUseCase.invoke()

        assertEquals(mockIngredients, result)
        verify(repository).getCommonIngredients()
    }

    @Test
    fun `AddIngredientUseCase should add an ingredient`() = runBlockingTest {
        val ingredient = IngredientModel(id = "3", name = "Pepper")

        addIngredientUseCase.invoke(ingredient)

        verify(repository).addIngredient(ingredient)
    }

    @Test
    fun `UpdateIngredientUseCase should update an ingredient`() = runBlockingTest {
        val ingredient = IngredientModel(id = "3", name = "Paprika")

        updateIngredientUseCase.invoke(ingredient)

        verify(repository).updateIngredient(ingredient)
    }

    @Test
    fun `DeleteIngredientUseCase should delete an ingredient by ID`() = runBlockingTest {
        val id = "3"

        deleteIngredientUseCase.invoke(id)

        verify(repository).deleteIngredient(id)
    }

    @Test
    fun `GetUserIngredientUseCase should return user ingredients`() = runBlockingTest {
        val mockUserIngredients = listOf(
            IngredientModel(id = "10", name = "UserSalt"),
            IngredientModel(id = "11", name = "UserSugar")
        )
        `when`(repository.getUserIngredients()).thenReturn(mockUserIngredients)

        val result = getUserIngredientUseCase.invoke()

        assertEquals(mockUserIngredients, result)
        verify(repository).getUserIngredients()
    }
}
