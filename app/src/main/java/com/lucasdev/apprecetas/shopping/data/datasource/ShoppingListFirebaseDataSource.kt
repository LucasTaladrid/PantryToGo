package com.lucasdev.apprecetas.shopping.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.shopping.domain.model.ShoppingListModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ShoppingListFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore

    suspend fun getShoppingLists(): List<ShoppingListModel> {
        val uid = Firebase.auth.currentUser?.uid ?: return emptyList()
        val snapshot = db.collection("users").document(uid)
            .collection("shoppingLists").orderBy("date", Query.Direction.DESCENDING)
            .get().await()

        return snapshot.documents.mapNotNull { it.toObject(ShoppingListModel::class.java)?.copy(id = it.id) }
    }

    suspend fun addShoppingList(list: ShoppingListModel): Boolean {
        val uid = Firebase.auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(uid)
                .collection("shoppingLists").add(list).await()
            true
        } catch (e: Exception) {
            false
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
        val uid = Firebase.auth.currentUser?.uid ?: return false
        return try {
            db.collection("users").document(uid)
                .collection("shoppingLists").document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
