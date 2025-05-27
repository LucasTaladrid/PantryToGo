package com.lucasdev.apprecetas.users.domain.repository

import com.lucasdev.apprecetas.users.data.datasource.RegisterFirebaseDataSource
import com.lucasdev.apprecetas.users.domain.model.UserModel
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val dataSource: RegisterFirebaseDataSource
) : RegisterRepository {

    override suspend fun registerAuth(email: String, password: String): String {
        return dataSource.registerAuth(email, password)
    }

    override suspend fun saveUserData(user: UserModel) {
        dataSource.saveUserData(user)
    }

    override suspend fun registerUser(name: String, email: String, password: String) {
        dataSource.registerUser(name, email, password)
    }
}