package com.lucasdev.apprecetas.users.domain.repository

interface RegisterRepository {
    suspend fun registerUser(name: String, email: String, password: String)
}