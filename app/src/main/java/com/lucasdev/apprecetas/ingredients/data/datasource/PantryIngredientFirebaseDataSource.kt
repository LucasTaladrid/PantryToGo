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

/**
 * Data source class to manage pantry-related ingredient operations for the currently authenticated user.
 */
class PantryIngredientFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val uid = Firebase.auth.currentUser?.uid ?: "anon"

    /**
     * Reference to the current user's pantry (inventory) collection.
     */
    private fun userPantryRef() = db.collection("users")
        .document(uid)
        .collection("inventory")

    /**
     * Retrieves all pantry ingredients for the current user.
     *
     * @return A list of [PantryIngredientModel] representing the user's pantry.
     */
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

    /**
     * Retrieves a pantry ingredient by its document ID.
     *
     * @param id The Firestore document ID.
     * @return The [PantryIngredientModel] if found, or null.
     */
    suspend fun getIngredientById(id: String): PantryIngredientModel? {

        val docRef = userPantryRef().document(id)
        val document = docRef.get().await()
        return if (document.exists()) {
            document.toObject(PantryIngredientModel::class.java)?.copy(id = document.id)
        } else {
            null
        }
    }

    /**
     * Retrieves a pantry ingredient using the ingredientId reference to an IngredientModel.
     *
     * @param ingredientId The ID of the ingredient (not the pantry doc ID).
     * @return The matching [PantryIngredientModel], or null if not found.
     */
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

    /**
     * Updates ingredient metadata (category, unit) in all user pantries when an ingredient is modified.
     *
     * @param updatedIngredient The updated [IngredientModel].
     */
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

    /**
     * Adds an ingredient to the user's pantry. If it already exists, its quantity is updated.
     *
     * @param userIngredient The ingredient to add or update.
     * @return The added or updated [PantryIngredientModel].
     */
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

    /**
     * Adds a list of pantry ingredients in a single batch operation.
     * Intended for bulk inserts, e.g., importing from shopping list.
     *
     * @param userIngredients List of [PantryIngredientModel] to add.
     * @return The list of ingredients added with generated IDs.
     */
    suspend fun addIngredientListToPantry(userIngredients: List<PantryIngredientModel>): List<PantryIngredientModel> =

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

    /**
     * Updates the quantity of a pantry ingredient.
     *
     * @param ingredient The updated ingredient.
     * @return True if successful, false otherwise.
     */
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

    /**
     * Deletes a specific pantry ingredient by document ID.
     *
     * @param id The pantry document ID to delete.
     * @return True if deleted successfully, false otherwise.
     */
    suspend fun deleteIngredientFromPantry(id: String): Boolean = suspendCoroutine { cont ->

        userPantryRef()
            .document(id)
            .delete()
            .addOnSuccessListener { cont.resume(true) }
            .addOnFailureListener { cont.resume(false) }
    }

    /**
     * Deletes pantry items that reference a specific ingredientId for the current user.
     *
     * @param ingredientId The ingredient ID to match.
     */
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

    /**
     * Deletes pantry ingredients that reference a given ingredientId across all users.
     * Typically used when a common ingredient is removed by an admin.
     *
     * @param ingredientId The shared ingredient ID to delete references for.
     */
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