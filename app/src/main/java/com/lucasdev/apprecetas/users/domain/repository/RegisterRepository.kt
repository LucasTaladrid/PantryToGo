package com.lucasdev.apprecetas.users.domain.repository

import com.lucasdev.apprecetas.users.domain.model.UserModel

interface RegisterRepository {
    suspend fun registerAuth(email: String, password: String): String
    suspend fun saveUserData(user: UserModel)
    suspend fun registerUser(name: String, email: String, password: String)
}