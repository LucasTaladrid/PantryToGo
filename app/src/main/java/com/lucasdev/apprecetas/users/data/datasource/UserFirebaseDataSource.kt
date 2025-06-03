package com.lucasdev.apprecetas.users.data.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserFirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isAdmin(): Boolean {
        val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val document = firestore.collection("users").document(uid).get().await()
        return document.getBoolean("admin") ?: false
    }


    fun logout() {
        auth.signOut()
        try {
            firestore.clearPersistence()
        } catch (e: Exception) {
            Log.e("UserFirebaseDataSource", "Error al limpiar la persistencia de Firestore", e)
        }

    }

    fun currentUserId(): String? = auth.currentUser?.uid
}
