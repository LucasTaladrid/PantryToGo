package com.lucasdev.apprecetas.users.domain.repository.impl

import com.lucasdev.apprecetas.users.data.datasource.RegisterFirebaseDataSource
import com.lucasdev.apprecetas.users.domain.model.UserModel
import com.lucasdev.apprecetas.users.domain.repository.RegisterRepository
import javax.inject.Inject

/**
 * Implementation of the RegisterRepository interface for handling user registration.
 * @param dataSource The data source for user registration operations.
 */
class RegisterRepositoryImpl @Inject constructor(
    private val dataSource: RegisterFirebaseDataSource
) : RegisterRepository {

    /**
     * Registers a new user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     */
    override suspend fun registerAuth(email: String, password: String): String {
        return dataSource.registerAuth(email, password)
    }

    /**
     * Saves user data to Firestore.
     * @param user The user model to be saved.
     */
    override suspend fun saveUserData(user: UserModel) {
        dataSource.saveUserData(user)
    }

    /**
     * Registers a new user with the provided name, email, and password.
     * @param name The user's name.
     * @param email The user's email.
     * @param password The user's password.
     */
    override suspend fun registerUser(name: String, email: String, password: String) {
        dataSource.registerUser(name, email, password)
    }
}