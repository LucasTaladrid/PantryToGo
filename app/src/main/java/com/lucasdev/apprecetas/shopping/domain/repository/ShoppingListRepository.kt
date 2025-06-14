package com.lucasdev.apprecetas.shopping.domain.repository

import com.lucasdev.apprecetas.shopping.domain.model.ShoppingHistoryModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel

interface ShoppingListRepository {
    suspend fun getShoppingLists(): List<ShoppingListModel>
    suspend fun getItemsForList(listId: String): List<ShoppingIngredientModel>
    suspend fun addShoppingList(list: ShoppingListModel): ShoppingListModel?
    suspend fun updateIngredientCheckedStatus(listId: String, itemId: String, checked: Boolean): Boolean
    suspend fun addIngredientToShoppingList(listId: String, item: ShoppingIngredientModel): Boolean
    suspend fun deleteItemFromList(listId: String, itemId: String): Boolean
    suspend fun updateItemInShoppingList(listId: String, item: ShoppingIngredientModel): Boolean
    suspend fun saveShoppingHistory(history: ShoppingHistoryModel): ShoppingHistoryModel?
    suspend fun getRecentShoppingHistory(limit: Long = 5): List<ShoppingHistoryModel>
    suspend fun deleteShoppingHistoryById(historyId: String): Boolean
    suspend fun getItemsForHistory(historyId: String): List<ShoppingIngredientModel>

}