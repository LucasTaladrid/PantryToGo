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
        val uid = Firebase.auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("users").document(uid)
            .collection("shoppingLists")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()

        // Obtenemos las listas de compras con sus items
        return snapshot.documents.mapNotNull { document ->
            val shoppingList = document.toObject(ShoppingListModel::class.java)?.copy(id = document.id)

            shoppingList?.let {
                // Accedemos a la colección 'items' dentro del documento de cada lista de compras
                val itemsSnapshot = db.collection("users").document(uid)
                    .collection("shoppingLists")
                    .document(document.id)
                    .collection("items") // Aquí está la colección de items dentro del documento
                    .get()
                    .await()

                // Convertimos los documentos de los items en ShoppingItemModel
                val items = itemsSnapshot.documents.mapNotNull { itemDocument ->
                    itemDocument.toObject(ShoppingItemModel::class.java)?.copy(ingredientId = itemDocument.id)
                }

                // Devolvemos la lista de la compra con los items añadidos
                it.copy(items = items)
            }
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




    suspend fun addIngredientToShoppingListItemCollection(listId: String, ingredient: ShoppingItemModel): Boolean {
        return try {
            // Obtenemos la referencia a la subcolección "items" de la lista de compras del usuario
            val itemsRef = shoppingListItemsRef(listId)

            // Verificar si el ingrediente ya está en la subcolección "items"
            val snapshot = itemsRef.whereEqualTo("name", ingredient.name).get().await()

            if (snapshot.isEmpty) {
                // Si el ingrediente no existe, lo añadimos a la subcolección "items"
                itemsRef.add(ingredient).await()
                true
            } else {
                // Si el ingrediente ya existe, obtenemos el documento y actualizamos la cantidad
                val existingItemDoc = snapshot.documents.first()
                val existingItem = existingItemDoc.toObject(ShoppingItemModel::class.java)!!

                // Actualizamos la cantidad del ingrediente en la subcolección
                val updatedIngredient = existingItem.copy(quantity = existingItem.quantity + ingredient.quantity)
                itemsRef.document(existingItemDoc.id).set(updatedIngredient).await()

                true
            }
        } catch (e: Exception) {
            false
        }
    }



}

