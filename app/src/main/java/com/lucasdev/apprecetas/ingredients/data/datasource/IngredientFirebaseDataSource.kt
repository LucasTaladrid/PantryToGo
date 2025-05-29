package com.lucasdev.apprecetas.ingredients.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import com.lucasdev.apprecetas.shopping.data.datasource.ShoppingListFirebaseDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Firebase data source responsible for managing ingredient-related operations.
 * It supports CRUD operations for both common (admin-level) and user-specific ingredients.
 *
 * @property dataSourcePantry Used for syncing ingredient changes with user pantries.
 * @property dataSourceShoppingList Used for syncing ingredient changes with shopping lists.
 */
class IngredientFirebaseDataSource @Inject constructor( private val dataSourcePantry: PantryIngredientFirebaseDataSource,private val dataSourceShoppingList: ShoppingListFirebaseDataSource) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val uid = auth.currentUser?.uid ?: "anon"

    /**
     * Checks if an ingredient with the given name already exists
     * in either the common or user-specific ingredient collections.
     *
     * @param name The name of the ingredient to check.
     * @return True if it exists, false otherwise.
     */
    private suspend fun ingredientExists(name: String): Boolean {

        val nameLower = name.trim().lowercase()
        val commonExists = commonIngredientsRef()
            .whereEqualTo("name", nameLower)
            .get()
            .await()
            .any { it.getString("name")?.trim()?.lowercase() == nameLower }

        val userExists = userIngredientsRef()
            .whereEqualTo("name", nameLower)
            .get()
            .await()
            .any { it.getString("name")?.trim()?.lowercase() == nameLower }

        return commonExists || userExists
    }

    /**
     * Returns a reference to the user's ingredient collection.
     */
    private fun userIngredientsRef() = db.collection("users")
        .document(uid)
        .collection("ingredients")

    /**
     * Returns a reference to the shared (common) ingredients collection.
     */
    private fun commonIngredientsRef() = db.collection("ingredients")

    /**
     * Determines whether the current user is an admin.
     *
     * @return True if admin, false otherwise.
     */
    suspend fun isAdmin(): Boolean = suspendCoroutine { cont ->
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val admin = snapshot.getBoolean("admin") ?: false
                cont.resume(admin)
            }
            .addOnFailureListener { cont.resume(false) }
    }

    /**
     * Retrieves all common (admin-level) ingredients.
     *
     * @return A list of common [IngredientModel].
     */
     suspend fun getCommonIngredients(): List<IngredientModel> = suspendCoroutine { cont ->
        commonIngredientsRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(IngredientModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

    /**
     * Retrieves all user-specific ingredients.
     *
     * @return A list of user [IngredientModel].
     */
    suspend fun getUserIngredients(): List<IngredientModel> = suspendCoroutine { cont ->
        userIngredientsRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(IngredientModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

    /**
     * Retrieves all ingredients, including both user-specific and common ones.
     *
     * @return A combined list of all [IngredientModel].
     */
    suspend fun getIngredients(): List<IngredientModel> {
        val common = getCommonIngredients()
        val user = getUserIngredients()
        return common + user
    }

    /**
     * Adds a new ingredient if it doesn't already exist.
     * The ingredient will be added to either the common or user-specific collection depending on admin status.
     *
     * @param ingredient The ingredient to add.
     * @return True if added successfully, false otherwise.
     */
    suspend fun addIngredient(ingredient: IngredientModel): Boolean {
        val exists = ingredientExists(ingredient.name)
        if (exists) return false

        val admin = isAdmin()
        val ref = if (admin) commonIngredientsRef() else userIngredientsRef()

        return try {
            val documentRef = ref.add(ingredient).await()

            val updatedIngredient = ingredient.copy(id = documentRef.id)

            ref.document(documentRef.id).set(updatedIngredient).await()
            Log.d("IngredientFirebaseDataSource", "Ingredient added: $ingredient")
            true
        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error adding ingredient", e)
            false
        }
    }

    /**
     * Updates an existing ingredient in the correct collection.
     * Also updates all pantry and shopping list references to ensure consistency.
     *
     * @param ingredient The updated ingredient.
     * @return True if updated successfully, false otherwise.
     */
    suspend fun updateIngredient(ingredient: IngredientModel): Boolean {
        val admin = isAdmin()
        val ref = if (admin) commonIngredientsRef().document(ingredient.id) else userIngredientsRef().document(ingredient.id)

        return try {
            ref.set(ingredient).await()
            Log.d("IngredientFirebaseDataSource", "Ingredient updated: $ingredient")
            dataSourcePantry.updateIngredientReferencesInPantries(ingredient)
            dataSourceShoppingList.updateIngredientReferencesInShoppingLists(ingredient)
            true
        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error updating ingredient", e)
            false
        }
    }

    /**
     * Deletes an ingredient by ID. Deletes from common or user-specific collections based on admin status.
     * Also removes it from related pantry data.
     *
     * @param id The ID of the ingredient to delete.
     * @return True if deleted successfully, false otherwise.
     */
    suspend fun deleteIngredient(id: String): Boolean {
        val admin = isAdmin()
        val ref = if (admin) commonIngredientsRef() else userIngredientsRef()

        return try {
            ref.document(id).delete().await()
            if(admin){
                dataSourcePantry.deleteIngredientFromAllUserPantries(id)
            }else{
                dataSourcePantry.deleteIngredientFromPantries(id)
            }
            Log.d("IngredientFirebaseDataSource", "Ingredient deleted: $id")
            true

        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error deleting ingredient", e)
            false
        }
    }
}
