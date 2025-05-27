package com.lucasdev.apprecetas.users.domain.usecase

import com.lucasdev.apprecetas.users.domain.model.UserModel
import com.lucasdev.apprecetas.users.domain.repository.RegisterRepository
import javax.inject.Inject


class RegisterAuthUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(email: String, password: String): String {
        return repository.registerAuth(email, password)
    }
}

class SaveUserDataUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(user: UserModel) {
        repository.saveUserData(user)
    }
}

class RegisterUserUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String) {
        repository.registerUser(name, email, password)
    }
}