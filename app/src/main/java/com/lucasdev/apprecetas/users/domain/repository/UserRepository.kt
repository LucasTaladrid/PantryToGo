package com.lucasdev.apprecetas.users.domain.repository

interface UserRepository {
    suspend fun isAdmin() : Boolean
}