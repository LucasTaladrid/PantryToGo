package com.lucasdev.apprecetas.users.domain.repository

import com.lucasdev.apprecetas.users.data.datasource.RegisterDataSource
import com.lucasdev.apprecetas.users.domain.model.UserModel
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val dataSource: RegisterDataSource
) : RegisterRepository {

    override suspend fun registerUser(name: String, email: String, password: String) {
        val uid = dataSource.registerAuth(email, password)
        val user = UserModel(
            uid = uid,
            name = name,
            email = email,
            isAdmin = false
        )
        dataSource.saveUserData(user)
    }
}