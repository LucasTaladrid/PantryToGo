package com.lucasdev.apprecetas.ingredients.domain.usecase

import com.google.common.base.Verify.verify
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.repository.IngredientRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GetIngredientsUseCaseTest {

    private val repository: IngredientRepository = mock()
    private lateinit var getIngredientsUseCase: GetIngredientsUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getIngredientsUseCase = GetIngredientsUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return list of ingredients`() = runTest {

        val expectedIngredients = listOf(
            IngredientModel(id = "1", name = "Tomate", category = mock(), unit = mock()),
            IngredientModel(id = "2", name = "Cebolla", category = mock(), unit = mock())
        )
        whenever(repository.getIngredients()).thenReturn(expectedIngredients)

        // When
        val result = getIngredientsUseCase()

        // Then
        assertEquals(expectedIngredients, result)
        verify(repository).getIngredients()
    }
}
