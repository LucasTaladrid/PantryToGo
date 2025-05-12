package com.lucasdev.apprecetas.shopping.data.repository

import com.lucasdev.apprecetas.shopping.data.datasource.ShoppingListFirebaseDataSource
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import com.lucasdev.apprecetas.shopping.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dataSource: ShoppingListFirebaseDataSource
) : ShoppingListRepository {
   override suspend fun getShoppingLists() = dataSource.getShoppingLists()

    override suspend fun getItemsForList(listId: String): List<ShoppingIngredientModel> =dataSource.getItemsForList(listId)

    override suspend fun addShoppingList(list: ShoppingListModel): ShoppingListModel? = dataSource.addShoppingList(list)

    override suspend fun updateIngredientCheckedStatus(listId: String, itemId: String, checked: Boolean): Boolean =
        dataSource.updateIngredientCheckedStatus(listId, itemId, checked)

    override suspend fun addIngredientToShoppingList(listId: String, item: ShoppingIngredientModel): Boolean =
        dataSource.addIngredientToShoppingListItemCollection(listId, item)

    override suspend fun deleteItemFromList(listId: String, itemId: String): Boolean =
        dataSource.deleteItemFromList(listId, itemId)

    override suspend fun updateItemInShoppingList(listId: String, item: ShoppingIngredientModel): Boolean =
        dataSource.updateItemInShoppingList(listId, item)



}


