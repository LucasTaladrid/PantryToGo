package com.lucasdev.apprecetas.users.data.datasource

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase data source for user authentication.
 * @param auth Firebase authentication instance.
 * @param firestore Firebase Firestore instance.
 */
class UserFirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Logs in a user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return A Result object indicating the success or failure of the login operation.
     */
    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Checks if the current user is an admin.
     * @return A Boolean indicating whether the current user is an admin.
     */
    suspend fun isAdmin(): Boolean {
        val uid = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val document = firestore.collection("users").document(uid).get().await()
        return document.getBoolean("admin") ?: false
    }

    /**
     * Logs out the current user.
     */
    fun logout() {
        auth.signOut()
        try {
            firestore.clearPersistence()
        } catch (e: Exception) {
            Log.e("UserFirebaseDataSource", "Error al limpiar la persistencia de Firestore", e)
        }

    }

    /**
     * Gets the current user's UID.
     */
    fun currentUserId(): String? = auth.currentUser?.uid
}
