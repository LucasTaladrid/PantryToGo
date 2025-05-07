package com.lucasdev.apprecetas.users.domain.usecase

import com.lucasdev.apprecetas.users.domain.repository.UserRepository
import javax.inject.Inject

class IsAdminUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isAdmin()
    }
}