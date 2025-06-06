package com.lucasdev.apprecetas.users.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.users.domain.model.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firebase data source for user registration.
 * @param auth Firebase authentication instance.
 * @param firestore Firebase Firestore instance.
 */
class RegisterFirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
)  {
    /**
     * Registers a new user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
     suspend fun registerAuth(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Error creating user")
    }

    /**
     * Saves user data to Firestore.
     * @param user The user model to be saved.
     */
     suspend fun saveUserData(user: UserModel) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    /**
     * Registers a new user with the provided name, email, and password.
     * @param name The user's name.
     * @param email The user's email.
     * @param password The user's password.
     */
    suspend fun registerUser(name: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Error creating user")

        val user = UserModel(
            uid = uid,
            name = name,
            email = email,
            isAdmin = false
        )

        firestore.collection("users")
            .document(uid)
            .set(user)
            .await()
    }
}