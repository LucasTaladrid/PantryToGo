package com.lucasdev.apprecetas.users.domain.repository.impl

import com.lucasdev.apprecetas.users.data.datasource.UserFirebaseDataSource
import com.lucasdev.apprecetas.users.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Implementation of the UserRepository interface for handling user authentication.
 * @param dataSource The data source for user authentication operations.
 */
class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserFirebaseDataSource
) : UserRepository {

    /**
     * Logs in a user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    override suspend fun loginUser(email: String, password: String): Result<Unit> {
        return dataSource.loginUser(email, password)
    }

    /**
     * Checks if the current user is an admin.
     * @return A Boolean indicating whether the current user is an admin.
     */
    override suspend fun isAdmin(): Boolean {
        return dataSource.isAdmin()
    }

    /**
     * Logs out the current user.
     */
    override fun logout() {
        dataSource.logout()
    }

    /**
     * Gets the current user's UID.
     */
    override fun currentUserId(): String? {
        return dataSource.currentUserId()
    }
}

