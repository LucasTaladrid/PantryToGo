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

}