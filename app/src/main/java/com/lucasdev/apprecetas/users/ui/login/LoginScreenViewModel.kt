package com.lucasdev.apprecetas.users.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.firestore
import com.lucasdev.apprecetas.users.domain.usecase.IsAdminUseCase
import com.lucasdev.apprecetas.users.domain.usecase.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val isAdminUseCase: IsAdminUseCase,
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _isLoginEnable = MutableStateFlow(false)
    val loginEnable: StateFlow<Boolean> = _isLoginEnable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _isLoginEnable.value = enableLogin(email, password)
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = loginUserUseCase(email, password)
            if (result.isSuccess) {
                val isAdminResult = runCatching { isAdminUseCase() }
                if (isAdminResult.isSuccess) {
                    _isAdmin.value = isAdminResult.getOrDefault(false)
                    _isLoading.value = false
                    onSuccess()
                } else {
                    _isLoading.value = false
                    onError("Error al verificar si es admin")
                }
            } else {
                _isLoading.value = false
                val exception = result.exceptionOrNull()
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> "El correo electrónico o la contraseña son incorrectos"
                    else -> exception?.localizedMessage ?: "Error al iniciar sesión"
                }
                onError(errorMessage)
            }

        }
    }

    private fun enableLogin(email: String, password: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 6
}
