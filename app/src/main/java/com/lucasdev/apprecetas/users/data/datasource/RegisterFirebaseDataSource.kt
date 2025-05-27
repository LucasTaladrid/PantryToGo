package com.lucasdev.apprecetas.users.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.users.domain.model.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterFirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
)  {

     suspend fun registerAuth(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Error creating user")
    }

     suspend fun saveUserData(user: UserModel) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

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