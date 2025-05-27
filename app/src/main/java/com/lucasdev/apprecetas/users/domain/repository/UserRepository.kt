package com.lucasdev.apprecetas.users.domain.repository

interface UserRepository {
    suspend fun loginUser(email: String, password: String): Result<Unit>
    suspend fun isAdmin(): Boolean
    fun logout()
    fun currentUserId(): String?
}
