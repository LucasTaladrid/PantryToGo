package com.lucasdev.apprecetas.users.ui.logout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasdev.apprecetas.users.domain.usecase.LogoutUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    fun logout(
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                logoutUseCase()
                onComplete()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
