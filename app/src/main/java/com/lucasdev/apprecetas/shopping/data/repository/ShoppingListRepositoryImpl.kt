package com.lucasdev.apprecetas.shopping.data.repository

import com.lucasdev.apprecetas.shopping.data.datasource.ShoppingListFirebaseDataSource
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import com.lucasdev.apprecetas.shopping.domain.repository.ShoppingListRepository
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dataSource: ShoppingListFirebaseDataSource
) : ShoppingListRepository {
   override suspend fun getShoppingLists() = dataSource.getShoppingLists()
    override suspend fun getItemsForList(listId: String): List<ShoppingItemModel> =dataSource.getItemsForList(listId)
    override suspend fun addShoppingList(list: ShoppingListModel): ShoppingListModel? = dataSource.addShoppingList(list)
    override suspend fun updateShoppingList(list: ShoppingListModel) = dataSource.updateShoppingList(list)
    override suspend fun updateIngredientCheckedStatus(listId: String, itemId: String, checked: Boolean): Boolean =
        dataSource.updateIngredientCheckedStatus(listId, itemId, checked)
    override suspend fun deleteShoppingList(id: String) = dataSource.deleteShoppingList(id)
    override suspend fun addIngredientToShoppingList(listId: String, item: ShoppingItemModel): Boolean =
        dataSource.addIngredientToShoppingListItemCollection(listId, item)

    }


