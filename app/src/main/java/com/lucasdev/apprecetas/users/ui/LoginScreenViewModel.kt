package com.lucasdev.apprecetas.users.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(private val auth:FirebaseAuth): ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoginEnable = MutableStateFlow(false)
    val loginEnable: StateFlow<Boolean> = _isLoginEnable

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun onLoginChanged(email: String, password: String){
        _email.value=email
        _password.value=password
        _isLoginEnable.value=enableLogin(email, password)
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            // Verificamos si el usuario es administrador
                            val userRef = Firebase.firestore.collection("users").document(userId)
                            userRef.get().addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // Obtener el valor de isAdmin
                                    val isAdmin = documentSnapshot.getBoolean("isAdmin") ?: false
                                    // Guardamos el estado en el ViewModel
                                    _isAdmin.value = isAdmin
                                    _isLoading.value = false
                                    onSuccess()


                                } else {
                                    onError("Usuario no encontrado")
                                }
                            }.addOnFailureListener {
                                onError("Error al verificar el usuario")
                            }
                        }
                    } else {
                        _isLoading.value = false
                        onError(task.exception?.message ?: "Error al iniciar sesión")
                    }
                }
        }
    }


    private fun enableLogin(email: String, password: String) =
        Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 6



}
