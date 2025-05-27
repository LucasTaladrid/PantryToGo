package com.lucasdev.apprecetas.users.domain.repository

import com.lucasdev.apprecetas.users.data.datasource.UserFirebaseDataSource
import javax.inject.Inject
import javax.inject.Singleton

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserFirebaseDataSource
) : UserRepository {

    override suspend fun loginUser(email: String, password: String): Result<Unit> {
        return dataSource.loginUser(email, password)
    }

    override suspend fun isAdmin(): Boolean {
        return dataSource.isAdmin()
    }

    override fun logout() {
        dataSource.logout()
    }

    override fun currentUserId(): String? {
        return dataSource.currentUserId()
    }
}

