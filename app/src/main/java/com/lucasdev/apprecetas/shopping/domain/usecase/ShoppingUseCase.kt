package com.lucasdev.apprecetas.shopping.domain.usecase

import com.lucasdev.apprecetas.shopping.domain.model.ShoppingHistoryModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import com.lucasdev.apprecetas.shopping.domain.repository.ShoppingListRepository
import javax.inject.Inject

class AddShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(list: ShoppingListModel) = repository.addShoppingList(list)
}

class GetShoppingListsUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke() = repository.getShoppingLists()
}

class GetItemsForListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(listId: String) = repository.getItemsForList(listId)
}

class UpdateIngredientCheckedStatusUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(listId: String, itemId: String, checked: Boolean) =
        repository.updateIngredientCheckedStatus(listId, itemId, checked)
}

class AddIngredientToShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(listId: String, ingredient: ShoppingIngredientModel) =
        repository.addIngredientToShoppingList(listId, ingredient)
}

class DeleteItemFromShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(listId: String, itemId: String) =
        repository.deleteItemFromList(listId, itemId)
}

class UpdateItemInShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(listId: String, item: ShoppingIngredientModel) =
        repository.updateItemInShoppingList(listId, item)
}

class SaveShoppingHistoryUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(history: ShoppingHistoryModel): ShoppingHistoryModel? {
        return repository.saveShoppingHistory(history)
    }
}

class GetRecentShoppingHistoryUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(limit: Long = 5): List<ShoppingHistoryModel> {
        return repository.getRecentShoppingHistory(limit)
    }
}

class DeleteShoppingHistoryByIdUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(historyId: String): Boolean {
        return repository.deleteShoppingHistoryById(historyId)

    }
}

class GetItemsForHistoryUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(historyId: String): List<ShoppingIngredientModel> {
        return repository.getItemsForHistory(historyId)
    }
}

class CreateInitialShoppingListUseCase @Inject constructor(
    private val addShoppingListUseCase: AddShoppingListUseCase,
    private val getShoppingListsUseCase: GetShoppingListsUseCase
) {
    suspend operator fun invoke() {
        val lists = getShoppingListsUseCase()
        if (lists.isEmpty()) {
            val newList = ShoppingListModel(title = "Lista activa")
            addShoppingListUseCase(newList)
        }
    }
}



