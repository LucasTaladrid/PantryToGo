package com.lucasdev.apprecetas.ingredients.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PantryIngredientFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid ?: "anon"

    private fun userInventoryRef() = db.collection("users")
        .document(uid)
        .collection("inventory")

    suspend fun getInventory(): List<PantryIngredientModel> = suspendCoroutine { cont ->
        userInventoryRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { doc ->

                    val obj = doc.toObject(PantryIngredientModel::class.java)

                    obj.copy(id = doc.id)
                }
                cont.resume(list)
            }
            .addOnFailureListener {
                cont.resume(emptyList())
            }
    }

    suspend fun getIngredientById(id: String): PantryIngredientModel? {

        val docRef = userInventoryRef().document(id)
        val document = docRef.get().await()

        return if (document.exists()) {
            document.toObject(PantryIngredientModel::class.java)?.copy(id = document.id)
        } else {
            null
        }
    }

    suspend fun addIngredientToInventory(userIngredient: PantryIngredientModel): PantryIngredientModel =
        suspendCoroutine { cont ->
            userInventoryRef()
                .add(userIngredient)
                .addOnSuccessListener { docRef ->
                    val newIngredientWithId = userIngredient.copy(
                        id = docRef.id
                    )
                    cont.resume(newIngredientWithId)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }

    suspend fun updateIngredientInventory(ingredient: PantryIngredientModel): Boolean {

        val docRef = userInventoryRef()
            .document(ingredient.id)


        return try {
            docRef.update("quantity", ingredient.quantity).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    suspend fun deleteIngredientFromInventory(id: String): Boolean = suspendCoroutine { cont ->
        userInventoryRef()
            .document(id)
            .delete()
            .addOnSuccessListener { cont.resume(true) }
            .addOnFailureListener { cont.resume(false) }
    }

}