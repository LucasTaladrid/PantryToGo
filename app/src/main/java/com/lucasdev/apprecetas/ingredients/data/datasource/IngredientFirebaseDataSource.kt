package com.lucasdev.apprecetas.ingredients.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.ingredients.domain.model.IngredientModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IngredientFirebaseDataSource @Inject constructor() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val uid = auth.currentUser?.uid ?: "anon"

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


    private fun userIngredientsRef() = db.collection("users")
        .document(uid)
        .collection("ingredients")


    private fun commonIngredientsRef() = db.collection("ingredients")

    private suspend fun isAdmin(): Boolean = suspendCoroutine { cont ->
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snapshot ->
                val admin = snapshot.getBoolean("admin") ?: false
                cont.resume(admin)
            }
            .addOnFailureListener { cont.resume(false) }
    }

    private suspend fun getCommonIngredients(): List<IngredientModel> = suspendCoroutine { cont ->
        commonIngredientsRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(IngredientModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

    suspend fun getUserIngredients(): List<IngredientModel> = suspendCoroutine { cont ->
        userIngredientsRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(IngredientModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { cont.resume(emptyList()) }
    }

    suspend fun getIngredients(): List<IngredientModel> {
        val common = getCommonIngredients()
        val user = getUserIngredients()
        return common + user
    }

    suspend fun getIngredientsForDisplay(): List<IngredientModel> {
        return if (isAdmin()) {
            getCommonIngredients()
        } else {
            getUserIngredients()
        }
    }

    //todo revisar y comprobar que funciona
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

    suspend fun updateIngredient(ingredient: IngredientModel): Boolean {
        val admin = isAdmin()
        val ref = if (admin) commonIngredientsRef().document(ingredient.id) else userIngredientsRef().document(ingredient.id)

        return try {
            ref.set(ingredient).await()
            Log.d("IngredientFirebaseDataSource", "Ingredient updated: $ingredient")
            true
        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error updating ingredient", e)
            false
        }
    }

    suspend fun deleteIngredient(id: String): Boolean {
        val admin = isAdmin()
        val ref = if (admin) commonIngredientsRef() else userIngredientsRef()

        return try {
            // Eliminar el ingrediente
            ref.document(id).delete().await()
            Log.d("IngredientFirebaseDataSource", "Ingredient deleted: $id")
            true
        } catch (e: Exception) {
            Log.e("IngredientFirebaseDataSource", "Error deleting ingredient", e)
            false
        }
    }
}
