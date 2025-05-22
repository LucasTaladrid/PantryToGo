package com.lucasdev.apprecetas.users.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserFirebaseDataSource  @Inject constructor() : UserDataSource {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override suspend fun isAdmin(): Boolean = suspendCoroutine { cont ->
        val uid = auth.currentUser?.uid
        if (uid == null) {
            cont.resume(false)
            return@suspendCoroutine
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val isAdmin = document.getBoolean("admin") ?: false
                cont.resume(isAdmin)
            }
            .addOnFailureListener {
                cont.resume(false)
            }
    }
}