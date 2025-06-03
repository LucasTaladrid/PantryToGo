package com.lucasdev.apprecetas.shopping.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingHistoryModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingIngredientModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.round

/**
 * Firebase data source for shopping lists.
 */
class ShoppingListFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid ?: "anon"
    private val MIN_QUANTITY = 0.00001
    private val unitToDecimals = mapOf(
        "unidad" to 4,
        "kg" to 4,
        "L" to 4
    )

    private fun roundTo(value: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return round(value * factor) / factor
    }

    private fun getDecimalsForUnit(unit: String?): Int {
        return unit?.let { unitToDecimals[it] } ?: 2
    }

    /**
     * Returns a reference to the shopping list items collection for a given list ID.
     * @param listId The ID of the shopping list.
     * @return A reference to the items collection.
     */
    private fun shoppingListItemsRef(listId: String) =
        db.collection("users").document(uid).collection("shoppingLists")
            .document(listId)
            .collection("items")

    /**
     * Retrieves a list of shopping lists for the current user.
     * @return A list of [ShoppingListModel] objects.
     */
    suspend fun getShoppingLists(): List<ShoppingListModel> {
        val snapshot = db.collection("users").document(uid)
            .collection("shoppingLists")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.toObject(ShoppingListModel::class.java)?.copy(id = document.id)
        }
    }

    /**
     * Retrieves a list of shopping list items for a given list ID.
     * @param listId The ID of the shopping list.
     * @return A list of [ShoppingIngredientModel] objects.
     */
    suspend fun getItemsForList(listId: String): List<ShoppingIngredientModel> {
        val itemsSnapshot = shoppingListItemsRef(listId).get().await()

        return itemsSnapshot.documents.mapNotNull { itemDocument ->
            itemDocument.toObject(ShoppingIngredientModel::class.java)?.copy(id = itemDocument.id)
        }

    }

    /**
     * Updates the checked status of an ingredient in a shopping list.
     * @param listId The ID of the shopping list.
     * @param itemId The ID of the ingredient item.
     * @param checked The new checked status.
     * @return True if the update was successful, false otherwise.
     */
    suspend fun updateIngredientCheckedStatus(
        listId: String,
        itemId: String,
        checked: Boolean
    ): Boolean {
        return try {
            val itemRef = shoppingListItemsRef(listId).document(itemId)

            Log.d(
                "updateCheck",
                "Actualizando itemId: $itemId en lista: $listId con valor: $checked"
            )

            itemRef.update("checked", checked).await()
            true
        } catch (e: Exception) {
            Log.e(
                "ShoppingListFirebaseDataSource",
                "Error al actualizar el estado del item: ${e.message}"
            )
            false
        }
    }

    /**
     * Updates the references of an ingredient in all shopping lists.
     * @param updatedIngredient The updated ingredient.
     * @return True if the update was successful, false otherwise.
     */
    suspend fun updateIngredientReferencesInShoppingLists(updatedIngredient: IngredientModel) {
        try {
            val usersSnapshot = db.collection("users").get().await()

            for (userDoc in usersSnapshot.documents) {
                val shoppingListsRef = userDoc.reference.collection("shoppingLists")
                val shoppingListsSnapshot = shoppingListsRef.get().await()

                for (listDoc in shoppingListsSnapshot.documents) {
                    val itemsRef = listDoc.reference.collection("items")

                    val matchingItems = itemsRef
                        .whereEqualTo("ingredientId", updatedIngredient.id)
                        .get()
                        .await()

                    for (itemDoc in matchingItems.documents) {
                        val updateMap = mapOf(
                            "category" to updatedIngredient.category,
                            "unit" to updatedIngredient.unit,
                            "name" to updatedIngredient.name
                        )
                        itemDoc.reference.update(updateMap).await()
                    }
                }
            }

            Log.d("ShoppingListDataSource", "Updated ingredient references in all shopping lists.")
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error updating references in shopping lists", e)
        }
    }

    /**
     * Adds a new shopping list to the user's account.
     * @param list The shopping list to add.
     * @return The added shopping list with its ID, or null if the addition failed.
     */
    suspend fun addShoppingList(list: ShoppingListModel): ShoppingListModel? {
        val uid = Firebase.auth.currentUser?.uid ?: return null
        val docRef = Firebase.firestore.collection("users").document(uid)
            .collection("shoppingLists").document()
        val listWithId = list.copy(id = docRef.id)
        return try {
            docRef.set(listWithId).await()
            listWithId

        } catch (e: Exception) {
            null
        }
    }

    /**
     * Deletes an item from a shopping list.
     * @param listId The ID of the shopping list.
     * @param itemId The ID of the item to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    suspend fun deleteItemFromList(listId: String, itemId: String): Boolean {
        return try {
            shoppingListItemsRef(listId).document(itemId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Adds or updates an ingredient in a shopping list.
     * @param listId The ID of the shopping list.
     * @param ingredient The ingredient to add or update.
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun addIngredientToShoppingListItemCollection(
        listId: String,
        ingredient: ShoppingIngredientModel
    ): Boolean {
        return try {
            if (ingredient.quantity < MIN_QUANTITY) {
                Log.d(
                    "SLDataSource",
                    "Quantity ${ingredient.quantity} < $MIN_QUANTITY: not touching Firestore."
                )
                return true
            }
            val docRef = shoppingListItemsRef(listId)
                .document(ingredient.ingredientId)
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {

                val existing = snapshot.toObject(ShoppingIngredientModel::class.java)!!
                val currentUnit = existing.unit
                val rawSum = existing.quantity + ingredient.quantity
                val decimals = getDecimalsForUnit(currentUnit.toString())
                val roundedSum = roundTo(rawSum, decimals)


                if (roundedSum < MIN_QUANTITY) {
                    docRef.delete().await()
                } else {

                    val updated = existing.copy(quantity = roundedSum)
                    docRef.set(updated.copy(id = ingredient.ingredientId)).await()
                }
            } else {

                val newUnit = ingredient.unit
                val decimals = getDecimalsForUnit(newUnit.toString())
                val roundedQuantity = roundTo(ingredient.quantity, decimals)
                val newItem = ingredient.copy(
                    id = ingredient.ingredientId,
                    quantity = roundedQuantity
                )
                docRef.set(newItem).await()
            }
            true
        } catch (e: Exception) {
            Log.e(
                "ShoppingListFirebaseDataSource",
                "Error adding/updating ingredient: ${e.message}"
            )
            false
        }
    }

    /**
     * Updates an item in a shopping list.
     * @param listId The ID of the shopping list.
     * @param item The item to update.
     * @return True if the update was successful, false otherwise.
     */
    suspend fun updateItemInShoppingList(
        listId: String,
        item: ShoppingIngredientModel
    ): Boolean {
        return try {
            if (item.quantity < MIN_QUANTITY) {
                Log.d(
                    "ShoppingListDataSource",
                    "Quantity ${item.quantity} < $MIN_QUANTITY: document not modified."
                )
                return true
            }

            val decimals = getDecimalsForUnit(item.unit.toString())
            val roundedQuantity = roundTo(item.quantity, decimals)

            if (roundedQuantity < MIN_QUANTITY) {
                Log.d(
                    "ShoppingListDataSource",
                    "Rounded quantity $roundedQuantity < $MIN_QUANTITY: operation ignored."
                )
                return true
            }

            val roundedItem = item.copy(quantity = roundedQuantity)
            shoppingListItemsRef(listId)
                .document(item.id)
                .set(roundedItem)
                .await()

            true
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error updating item: ${e.message}")
            false
        }
    }


    /**
     * Saves a shopping history to the user's account.
     * @param history The shopping history to save.
     * @param maxHistory The maximum number of histories to keep.
     * @return The saved shopping history with its ID, or null if the save failed.
     */
    suspend fun saveShoppingHistory(
        history: ShoppingHistoryModel,
        maxHistory: Int = 5
    ): ShoppingHistoryModel? {
        val collectionRef = db.collection("users")
            .document(uid)
            .collection("shoppingHistory")

        val newDocRef = collectionRef.document()
        val historyWithoutItems = history.copy(id = newDocRef.id, items = emptyList())

        return try {
            newDocRef.set(historyWithoutItems).await()

            val itemsCollection = newDocRef.collection("items")
            history.items.forEach { item ->
                val docRef = itemsCollection.document()
                val itemWithId = item.copy(id = docRef.id)
                docRef.set(itemWithId).await()
            }

            val allHistories = collectionRef
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents

            if (allHistories.size > maxHistory) {
                val historiesToDelete = allHistories.drop(maxHistory)
                historiesToDelete.forEach { it.reference.delete().await() }
            }

            history.copy(id = newDocRef.id)
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error al guardar historial: ${e.message}")
            null
        }
    }

    /**
     * Retrieves the most recent shopping histories for the current user.
     * @param limit The maximum number of histories to retrieve.
     * @return A list of [ShoppingHistoryModel] objects.
     */
    suspend fun getRecentShoppingHistory(limit: Long = 5): List<ShoppingHistoryModel> {
        return db.collection("users")
            .document(uid)
            .collection("shoppingHistory")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(ShoppingHistoryModel::class.java) }
    }

    /**
     * Deletes a shopping history by its ID.
     * @param historyId The ID of the shopping history to delete.
     * @return True if the deletion was successful, false otherwise.
     */
    suspend fun deleteShoppingHistoryById(historyId: String): Boolean {
        return try {
            db.collection("users")
                .document(uid)
                .collection("shoppingHistory")
                .document(historyId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error al eliminar historial: ${e.message}")
            false
        }
    }

    /**
     * Retrieves the items for a specific shopping history.
     * @param historyId The ID of the shopping history.
     * @return A list of [ShoppingIngredientModel] objects.
     */
    suspend fun getItemsForHistory(historyId: String): List<ShoppingIngredientModel> {
        return try {
            db.collection("users")
                .document(uid)
                .collection("shoppingHistory")
                .document(historyId)
                .collection("items")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(ShoppingIngredientModel::class.java)?.copy(id = it.id) }
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error al obtener items del historial: ${e.message}")
            emptyList()
        }
    }

    /**
     * Subtracts ingredients from a shopping list.
     * @param listId The ID of the shopping list.
     * @param ingredients The ingredients to subtract.
     */
    suspend fun subtractIngredientsFromShoppingList(
        listId: String,
        ingredients: List<ShoppingIngredientModel>
    ) {
        for (ingredient in ingredients) {
            try {
                if (ingredient.quantity < MIN_QUANTITY) {
                    Log.d(
                        "SLDataSource",
                        "Attempt to subtract ${ingredient.quantity} < $MIN_QUANTITY: ignored."
                    )
                    continue
                }

                val docRef = shoppingListItemsRef(listId)
                    .document(ingredient.ingredientId)
                val snapshot = docRef.get().await()

                if (snapshot.exists()) {
                    val existing = snapshot.toObject(ShoppingIngredientModel::class.java)!!
                    val currentUnit = existing.unit
                    val rawDifference = existing.quantity - ingredient.quantity

                    val decimals = getDecimalsForUnit(currentUnit.toString())
                    val roundedDifference = roundTo(rawDifference, decimals)

                    if (roundedDifference < MIN_QUANTITY) {
                        docRef.delete().await()
                    } else {
                        val updated = existing.copy(quantity = roundedDifference)
                        docRef.set(updated).await()
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "ShoppingListFirebaseDataSource",
                    "Error subtracting ingredient ${ingredient.ingredientId}: ${e.message}"
                )
            }
        }
    }

}

