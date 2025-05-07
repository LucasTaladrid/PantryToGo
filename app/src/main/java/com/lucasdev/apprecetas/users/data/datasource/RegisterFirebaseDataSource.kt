package com.lucasdev.apprecetas.users.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lucasdev.apprecetas.users.domain.model.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterFirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterDataSource {

    override suspend fun registerAuth(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("Error creating user")
    }

    override suspend fun saveUserData(user: UserModel) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }
}