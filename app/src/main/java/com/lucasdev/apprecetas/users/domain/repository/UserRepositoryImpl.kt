package com.lucasdev.apprecetas.users.domain.repository

import com.lucasdev.apprecetas.users.data.datasource.UserFirebaseDataSource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserFirebaseDataSource
) : UserRepository {

    override suspend fun isAdmin(): Boolean {
        return userDataSource.isAdmin()
    }
}