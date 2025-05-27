package com.lucasdev.apprecetas.users.domain.usecase

import com.lucasdev.apprecetas.users.domain.repository.UserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return userRepository.loginUser(email, password)
    }
}

class IsAdminUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isAdmin()
    }
}

class LogoutUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke() {
        userRepository.logout()
    }
}

class GetCurrentUserIdUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): String? {
        return userRepository.currentUserId()
    }
}