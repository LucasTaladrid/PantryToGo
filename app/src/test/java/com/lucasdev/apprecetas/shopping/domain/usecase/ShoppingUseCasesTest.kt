package com.lucasdev.apprecetas.shopping.domain.usecase

import com.lucasdev.apprecetas.shopping.domain.model.*
import com.lucasdev.apprecetas.shopping.domain.repository.ShoppingListRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class ShoppingUseCasesTest {

    private lateinit var repository: ShoppingListRepository

    private val testList = ShoppingListModel(id = "list123", title = "Weekly Groceries")
    private val testItem = ShoppingIngredientModel(id = "item123", ingredientId = "ing456", name = "Tomato", quantity = 2.0)
    private val testHistory = ShoppingHistoryModel(id = "hist789", title = "Past Groceries", items = listOf(testItem))

    @Before
    fun setup() {
        repository = mock()
    }

    @Test
    fun `AddShoppingListUseCase should call repository addShoppingList`() = runBlockingTest {
        val useCase = AddShoppingListUseCase(repository)
        useCase.invoke(testList)
        verify(repository).addShoppingList(testList)
    }

    @Test
    fun `GetShoppingListsUseCase should return shopping lists`() = runBlockingTest {
        val expectedLists = listOf(testList)
        whenever(repository.getShoppingLists()).thenReturn(expectedLists)

        val useCase = GetShoppingListsUseCase(repository)
        val result = useCase.invoke()

        assert(result == expectedLists)
        verify(repository).getShoppingLists()
    }

    @Test
    fun `GetItemsForListUseCase should return items for list`() = runBlockingTest {
        val expectedItems = listOf(testItem)
        whenever(repository.getItemsForList(testList.id)).thenReturn(expectedItems)

        val useCase = GetItemsForListUseCase(repository)
        val result = useCase.invoke(testList.id)

        assert(result == expectedItems)
        verify(repository).getItemsForList(testList.id)
    }

    @Test
    fun `UpdateIngredientCheckedStatusUseCase should call repository update checked status`() = runBlockingTest {
        val useCase = UpdateIngredientCheckedStatusUseCase(repository)
        useCase.invoke(testList.id, testItem.id, true)

        verify(repository).updateIngredientCheckedStatus(testList.id, testItem.id, true)
    }

    @Test
    fun `AddIngredientToShoppingListUseCase should call repository add item`() = runBlockingTest {
        val useCase = AddIngredientToShoppingListUseCase(repository)
        useCase.invoke(testList.id, testItem)

        verify(repository).addIngredientToShoppingList(testList.id, testItem)
    }

    @Test
    fun `DeleteItemFromShoppingListUseCase should call repository delete item`() = runBlockingTest {
        val useCase = DeleteItemFromShoppingListUseCase(repository)
        useCase.invoke(testList.id, testItem.id)

        verify(repository).deleteItemFromList(testList.id, testItem.id)
    }

    @Test
    fun `UpdateItemInShoppingListUseCase should call repository update item`() = runBlockingTest {
        val useCase = UpdateItemInShoppingListUseCase(repository)
        useCase.invoke(testList.id, testItem)

        verify(repository).updateItemInShoppingList(testList.id, testItem)
    }

    @Test
    fun `SaveShoppingHistoryUseCase should call repository save history`() = runBlockingTest {
        whenever(repository.saveShoppingHistory(testHistory)).thenReturn(testHistory)

        val useCase = SaveShoppingHistoryUseCase(repository)
        val result = useCase.invoke(testHistory)

        assert(result == testHistory)
        verify(repository).saveShoppingHistory(testHistory)
    }

    @Test
    fun `GetRecentShoppingHistoryUseCase should return recent history`() = runBlockingTest {
        val expected = listOf(testHistory)
        whenever(repository.getRecentShoppingHistory(5)).thenReturn(expected)

        val useCase = GetRecentShoppingHistoryUseCase(repository)
        val result = useCase.invoke()

        assert(result == expected)
        verify(repository).getRecentShoppingHistory(5)
    }

    @Test
    fun `DeleteShoppingHistoryByIdUseCase should call repository delete history`() = runBlockingTest {
        whenever(repository.deleteShoppingHistoryById(testHistory.id)).thenReturn(true)

        val useCase = DeleteShoppingHistoryByIdUseCase(repository)
        val result = useCase.invoke(testHistory.id)

        assert(result)
        verify(repository).deleteShoppingHistoryById(testHistory.id)
    }

    @Test
    fun `GetItemsForHistoryUseCase should return history items`() = runBlockingTest {
        val expectedItems = listOf(testItem)
        whenever(repository.getItemsForHistory(testHistory.id)).thenReturn(expectedItems)

        val useCase = GetItemsForHistoryUseCase(repository)
        val result = useCase.invoke(testHistory.id)

        assert(result == expectedItems)
        verify(repository).getItemsForHistory(testHistory.id)
    }
}
