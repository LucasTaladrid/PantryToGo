package com.lucasdev.apprecetas.ingredients.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.ingredients.domain.model.PantryIngredientModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PantryIngredientFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid ?: "anon"

    private fun userPantryRef() = db.collection("users")
        .document(uid)
        .collection("inventory")

    suspend fun getPantry(): List<PantryIngredientModel> = suspendCoroutine { cont ->
        userPantryRef().get()
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

        val docRef = userPantryRef().document(id)
        val document = docRef.get().await()
        return if (document.exists()) {
            document.toObject(PantryIngredientModel::class.java)?.copy(id = document.id)
        } else {
            null
        }
    }

    suspend fun getIngredientByIngredientId(ingredientId: String): PantryIngredientModel? {
        return suspendCoroutine { cont ->
            userPantryRef()
                .whereEqualTo("ingredientId", ingredientId)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val doc = querySnapshot.documents.firstOrNull()
                    if (doc != null) {
                        val ingredient =
                            doc.toObject(PantryIngredientModel::class.java)?.copy(id = doc.id)
                        cont.resume(ingredient)
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
    }

    suspend fun updateIngredientReferencesInPantries(updatedIngredient: IngredientModel) {
        try {
            val usersSnapshot = db.collection("users").get().await()

            for (userDoc in usersSnapshot.documents) {
                val inventoryRef = userDoc.reference.collection("inventory")

                val matchingDocs = inventoryRef
                    .whereEqualTo("ingredientId", updatedIngredient.id)
                    .get()
                    .await()

                for (doc in matchingDocs.documents) {
                    val updateMap = mapOf(
                        "category" to updatedIngredient.category,
                        "unit" to updatedIngredient.unit
                    )
                    doc.reference.update(updateMap).await()
                }
            }

            Log.d("PantryIngredientDataSource", "Updated ingredient references in all pantries.")
        } catch (e: Exception) {
            Log.e("PantryIngredientDataSource", "Error updating references in pantries", e)
        }
    }

    suspend fun addIngredientToPantry(userIngredient: PantryIngredientModel): PantryIngredientModel =
        suspendCoroutine { cont ->
            val ingredientDocRef = userPantryRef().document(userIngredient.ingredientId)

            ingredientDocRef.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val existingQuantity = snapshot.getDouble("quantity") ?: 0.0
                        val updatedQuantity = existingQuantity + userIngredient.quantity

                        ingredientDocRef.update("quantity", updatedQuantity)
                            .addOnSuccessListener {
                                val updatedIngredient = userIngredient.copy(
                                    id = ingredientDocRef.id,
                                    quantity = updatedQuantity
                                )
                                cont.resume(updatedIngredient)
                            }
                            .addOnFailureListener { cont.resumeWithException(it) }

                    } else {
                        ingredientDocRef.set(userIngredient)
                            .addOnSuccessListener {
                                val newIngredient = userIngredient.copy(id = ingredientDocRef.id)
                                cont.resume(newIngredient)
                            }
                            .addOnFailureListener { cont.resumeWithException(it) }
                    }
                }
                .addOnFailureListener { cont.resumeWithException(it) }
        }

    //todo por probar su funcionamiento
    //todo este método será para añadir de golpe muchos ingredientes que vienen de la shopping list
    suspend fun addIngredientsToPantry(userIngredients: List<PantryIngredientModel>): List<PantryIngredientModel> =
        suspendCoroutine { cont ->
            val batch = db.batch()
            val ingredientsWithIds = mutableListOf<PantryIngredientModel>()

            userIngredients.forEach { ingredient ->
                val newDocRef = userPantryRef().document()
                val ingredientWithId = ingredient.copy(id = newDocRef.id)

                batch.set(newDocRef, ingredientWithId)
                ingredientsWithIds.add(ingredientWithId)
            }

            batch.commit()
                .addOnSuccessListener {
                    cont.resume(ingredientsWithIds)
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }

    suspend fun updateIngredientPantry(ingredient: PantryIngredientModel): Boolean {

        val docRef = userPantryRef()
            .document(ingredient.id)
        return try {
            docRef.update("quantity", ingredient.quantity).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteIngredientFromPantry(id: String): Boolean = suspendCoroutine { cont ->

        userPantryRef()
            .document(id)
            .delete()
            .addOnSuccessListener { cont.resume(true) }
            .addOnFailureListener { cont.resume(false) }
    }

    suspend fun deleteIngredientFromPantries(ingredientId: String) {
        val pantryRef = userPantryRef()

        try {
            val pantrySnapshot = pantryRef
                .whereEqualTo("ingredientId", ingredientId)
                .get()
                .await()

            for (doc in pantrySnapshot.documents) {
                doc.reference.delete().await()
            }

            Log.d("IngredientFirebaseDataSource", "Deleted ingredient from pantry: $ingredientId")

        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error deleting ingredient from pantry", e)
        }
    }

    suspend fun deleteIngredientFromAllUserPantries(ingredientId: String) {
        try {
            val usersSnapshot = db.collection("users").get().await()

            for (userDoc in usersSnapshot.documents) {
                val inventoryRef = userDoc.reference.collection("inventory")
                val pantrySnapshot = inventoryRef
                    .whereEqualTo("ingredientId", ingredientId)
                    .get()
                    .await()

                for (pantryDoc in pantrySnapshot.documents) {
                    pantryDoc.reference.delete().await()
                }
            }

            Log.d(
                "IngredientFirebaseDataSource",
                "Deleted ingredient from all pantries: $ingredientId"
            )

        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error deleting from all user pantries", e)
        }
    }


}