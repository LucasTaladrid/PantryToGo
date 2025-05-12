package com.lucasdev.apprecetas.shopping.domain.usecase

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
){
    suspend operator fun invoke(listId: String, itemId: String, checked: Boolean) =repository.updateIngredientCheckedStatus(listId, itemId, checked)
}

class AddIngredientToShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository) {
    suspend operator fun invoke(listId: String, ingredient: ShoppingIngredientModel) =
        repository.addIngredientToShoppingList(listId, ingredient)
}

class DeleteItemFromShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository){
    suspend operator fun invoke(listId: String, itemId: String) = repository.deleteItemFromList(listId, itemId)
}

class UpdateItemInShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository){
    suspend operator fun invoke(listId: String, item: ShoppingIngredientModel) = repository.updateItemInShoppingList(listId, item)
}

