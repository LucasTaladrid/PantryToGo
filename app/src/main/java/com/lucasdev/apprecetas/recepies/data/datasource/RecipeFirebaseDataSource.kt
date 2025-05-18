package com.lucasdev.apprecetas.recepies.data.datasource

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.recepies.domain.model.RecipeModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecipeFirebaseDataSource @Inject constructor() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val uid = auth.currentUser?.uid ?: "anon"

    private fun userRecipesRef() = db.collection("users").document(uid).collection("recipes")

    private fun favoritesRef()=db.collection("users").document(uid).collection("favorites")

    private fun pendingRef()=db.collection("users").document(uid).collection("pending")

    private fun commonRecipesRef() = db.collection("recipes")

    suspend fun isAdmin(): Boolean {
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.getBoolean("admin") ?: false

    }

    suspend fun addRecipe(recipe: RecipeModel): RecipeModel? {
        val admin = isAdmin()
        val ref = if (admin) commonRecipesRef() else userRecipesRef()

        val docRef=ref.document()
        val withId=recipe.copy(id=docRef.id, dateCreated = Timestamp.now())

        return try{
            docRef.set(withId).await()
            withId
        }catch (e:Exception){
            Log.e("RecipeFirebaseDataSource", "Error adding recipe", e)
            null
        }

    }

    suspend fun getCommonRecipes(): List<RecipeModel> = suspendCoroutine { cont ->
        commonRecipesRef().get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.mapNotNull { it.toObject(RecipeModel::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener {
                cont.resume(emptyList()) // o puedes lanzar excepción si prefieres
            }
    }

    suspend fun addToFavorites(recipe: RecipeModel) {
        try {
            favoritesRef()
                .document(recipe.id)
                .set(mapOf(
                    "id" to recipe.id,
                    "name" to recipe.name,
                    "ingredients" to recipe.ingredients,
                    "steps" to recipe.steps))
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error adding to favorites", e)
        }
    }

    suspend fun removeFromFavorites(recipe: RecipeModel) {
        try {
            favoritesRef()
                .document(recipe.id)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error removing from favorites", e)
        }
    }

    suspend fun addToPending(recipe: RecipeModel) {
        try {
            pendingRef()
                .document(recipe.id)
                .set(mapOf(
                    "id" to recipe.id,
                    "name" to recipe.name,
                    "ingredients" to recipe.ingredients,
                    "steps" to recipe.steps))
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error adding to pending", e)
        }
    }

    suspend fun removeFromPending(recipe: RecipeModel) {
        try {
            pendingRef()
                .document(recipe.id)
                .delete()
                .await()
        } catch (e: Exception) {
            Log.e("RecipeDataSource", "Error removing from pending", e)
        }
    }

    suspend fun getFavoriteRecipes(): List<RecipeModel> = try {
        db.collection("users")
            .document(uid)
            .collection("favorites")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(RecipeModel::class.java) }
    } catch (e: Exception) {
        Log.e("RecipeDataSource", "Error getting favorite recipes", e)
        emptyList()
    }

    suspend fun getPendingRecipes(): List<RecipeModel> = try {
        db.collection("users")
            .document(uid)
            .collection("pending")
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(RecipeModel::class.java) }
    } catch (e: Exception) {
        Log.e("RecipeDataSource", "Error getting pending recipes", e)
        emptyList()
    }



}