package com.lucasdev.apprecetas.shopping.domain.repository

import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel

interface ShoppingListRepository {
    suspend fun getShoppingLists(): List<ShoppingListModel>
    suspend fun getItemsForList(listId: String): List<ShoppingItemModel>
    suspend fun addShoppingList(list: ShoppingListModel): ShoppingListModel?
    suspend fun updateShoppingList(list: ShoppingListModel): Boolean
    suspend fun updateIngredientCheckedStatus(listId: String, itemId: String, checked: Boolean): Boolean
    suspend fun deleteShoppingList(id: String): Boolean
    suspend fun addIngredientToShoppingList(listId: String, item: ShoppingItemModel): Boolean
    suspend fun deleteItemFromList(listId: String, itemId: String): Boolean

}