package com.lucasdev.apprecetas.users.domain.model


//todo, lista de compra, lista de recetas, lista de recetas favoritas
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
