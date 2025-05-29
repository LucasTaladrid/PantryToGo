package com.lucasdev.apprecetas.users.domain.repository.impl

import com.lucasdev.apprecetas.users.data.datasource.UserFirebaseDataSource
import com.lucasdev.apprecetas.users.domain.repository.UserRepository
import javax.inject.Inject

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

