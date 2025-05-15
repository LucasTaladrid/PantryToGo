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

class ShoppingListFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid ?: "anon"

    private fun shoppingListItemsRef(listId: String) = db.collection("users").document(uid).collection("shoppingLists")
        .document(listId)
        .collection("items")

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

    suspend fun getItemsForList(listId: String): List<ShoppingIngredientModel> {
        val itemsSnapshot = shoppingListItemsRef(listId).get().await()

        return itemsSnapshot.documents.mapNotNull { itemDocument ->
            itemDocument.toObject(ShoppingIngredientModel::class.java)?.copy(id = itemDocument.id)
        }

    }

    suspend fun updateIngredientCheckedStatus(listId: String, itemId: String, checked: Boolean): Boolean {
        return try {
            val itemRef = shoppingListItemsRef(listId).document(itemId)

            Log.d("updateCheck", "Actualizando itemId: $itemId en lista: $listId con valor: $checked")

            itemRef.update("checked", checked).await()
            true
        } catch (e: Exception) {
            Log.e("ShoppingListFirebaseDataSource", "Error al actualizar el estado del item: ${e.message}")
            false
        }
    }

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

    suspend fun deleteItemFromList(listId: String, itemId: String): Boolean {
        return try {
            shoppingListItemsRef(listId).document(itemId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addIngredientToShoppingListItemCollection(listId: String, ingredient: ShoppingIngredientModel): Boolean {
        return try {
            val docRef = shoppingListItemsRef(listId).document(ingredient.ingredientId)

            val snapshot = docRef.get().await()

            if (snapshot.exists()) {
                val existing = snapshot.toObject(ShoppingIngredientModel::class.java)
                val updated = existing!!.copy(quantity = existing.quantity + ingredient.quantity)
                docRef.set(updated.copy(id = ingredient.ingredientId)).await()
            } else {
                docRef.set(ingredient.copy(id = ingredient.ingredientId)).await()
            }

            true
        } catch (e: Exception) {
            Log.e("ShoppingListFirebaseDataSource", "Error en addOrUpdateIngredientById: ${e.message}")
            false
        }
    }

    suspend fun updateItemInShoppingList(listId: String, item: ShoppingIngredientModel): Boolean {
        return try {
            shoppingListItemsRef(listId)
                .document(item.id)
                .set(item)
                .await()
            true
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error al actualizar item: ${e.message}")
            false
        }
    }

    //todo comprobar que funciona que se almacenen solo 5 historiales, cambiar casos de uso y repositorio
    suspend fun saveShoppingHistory(history: ShoppingHistoryModel, maxHistory: Int = 5): ShoppingHistoryModel? {
        val collectionRef = db.collection("users")
            .document(uid)
            .collection("shoppingHistory")

        val newDocRef = collectionRef.document()
        val historyWithoutItems = history.copy(id = newDocRef.id, items = emptyList()) // Guardamos solo metadatos

        return try {
            // 1. Guarda la metadata sin los items
            newDocRef.set(historyWithoutItems).await()

            // 2. Guarda los items en una subcolección
            val itemsCollection = newDocRef.collection("items")
            history.items.forEach { item ->
                val docRef = itemsCollection.document()
                val itemWithId = item.copy(id = docRef.id)
                docRef.set(itemWithId).await()
            }

            // 3. Elimina historiales antiguos si hay más de maxHistory
            val allHistories = collectionRef
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents

            if (allHistories.size > maxHistory) {
                val historiesToDelete = allHistories.drop(maxHistory)
                historiesToDelete.forEach { it.reference.delete().await() }
            }

            history.copy(id = newDocRef.id) // retornamos con el ID asignado
        } catch (e: Exception) {
            Log.e("ShoppingListDataSource", "Error al guardar historial: ${e.message}")
            null
        }
    }


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





}

