package com.lucasdev.apprecetas.users.data.datasource

interface UserDataSource {
    suspend fun isAdmin(): Boolean
}