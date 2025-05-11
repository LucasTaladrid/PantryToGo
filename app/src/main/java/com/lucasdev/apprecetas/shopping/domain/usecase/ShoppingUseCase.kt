package com.lucasdev.apprecetas.shopping.domain.usecase

import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel
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
class UpdateShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(list: ShoppingListModel) = repository.updateShoppingList(list)
}

class DeleteShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteShoppingList(id)
}
class AddIngredientToShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository) {
    suspend operator fun invoke(listId: String, ingredient: ShoppingItemModel) =
        repository.addIngredientToShoppingList(listId, ingredient)
}

class DeleteItemFromShoppingListUseCase @Inject constructor(
    private val repository: ShoppingListRepository){
    suspend operator fun invoke(listId: String, itemId: String) = repository.deleteItemFromList(listId, itemId)
}

