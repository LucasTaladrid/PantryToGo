package com.lucasdev.apprecetas.users.domain.model


data class UserModel(
    val uid: String="",
    val name: String="",
    val email: String="",
    val status: UserStatus = UserStatus.FREE,
    val isAdmin: Boolean = false

)

enum class UserStatus {
    FREE, PREMIUM
}
