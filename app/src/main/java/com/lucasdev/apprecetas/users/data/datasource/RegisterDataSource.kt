package com.lucasdev.apprecetas.users.data.datasource

import com.lucasdev.apprecetas.users.domain.model.UserModel

interface RegisterDataSource {
    suspend fun registerAuth(email: String, password: String): String
    suspend fun saveUserData(user: UserModel)
}