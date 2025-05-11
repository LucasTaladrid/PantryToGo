package com.lucasdev.apprecetas.shopping.data.datasource

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingItemModel
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

    suspend fun getItemsForList(listId: String): List<ShoppingItemModel> {
        val itemsSnapshot = shoppingListItemsRef(listId).get().await()

        return itemsSnapshot.documents.mapNotNull { itemDocument ->
            itemDocument.toObject(ShoppingItemModel::class.java)?.copy(id = itemDocument.id)
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

    suspend fun updateShoppingList(list: ShoppingListModel): Boolean {
        val uid = Firebase.auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(uid)
                .collection("shoppingLists").document(list.id)
                .set(list).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteShoppingList(id: String): Boolean {
        return try {
            db.collection("users").document(uid)
                .collection("shoppingLists").document(id).delete().await()
            true
        } catch (e: Exception) {
            false
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

    suspend fun addIngredientToShoppingListItemCollection(listId: String, ingredient: ShoppingItemModel): Boolean {
        return try {
            val itemsRef = shoppingListItemsRef(listId)

            // Verificar si ya existe un documento con este ingrediente
            val snapshot = itemsRef.whereEqualTo("ingredientId", ingredient.id).get().await()

            if (snapshot.isEmpty) {
                // Si no existe, lo añadimos como un nuevo documento
                val addedDocRef = itemsRef.add(ingredient.copy(id = "")).await()
                val generatedId = addedDocRef.id
                addedDocRef.update("id", generatedId).await()
                true
            } else {
                // Si el ingrediente ya existe, actualizamos su cantidad
                val existingItemDoc = snapshot.documents.first()
                val existingItem = existingItemDoc.toObject(ShoppingItemModel::class.java)!!

                // Actualizamos la cantidad del ingrediente
                val updatedIngredient = existingItem.copy(quantity = existingItem.quantity + ingredient.quantity)
                itemsRef.document(existingItemDoc.id).set(updatedIngredient).await()
                true
            }
        } catch (e: Exception) {
            Log.e("ShoppingListFirebaseDataSource", "Error al añadir o actualizar ingrediente en la lista de compras: ${e.message}")
            false
        }
    }


}

