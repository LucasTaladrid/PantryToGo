package com.lucasdev.apprecetas.shopping.domain.repository

import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel

interface ShoppingListRepository {
    suspend fun getShoppingLists(): List<ShoppingListModel>
    suspend fun addShoppingList(list: ShoppingListModel): ShoppingListModel?
    suspend fun updateShoppingList(list: ShoppingListModel): Boolean
    suspend fun deleteShoppingList(id: String): Boolean
    suspend fun addIngredientToShoppingList(listId: String, item: ShoppingItemModel): Boolean
}