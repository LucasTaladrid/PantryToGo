package com.lucasdev.apprecetas.users.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

//todo cambiar colores y fondo
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    backLoginScreen: () -> Unit
) {

    //Variables to save register information
    val name by registerViewModel.name.collectAsState()
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val isRegisterEnabled by registerViewModel.isRegisterEnabled.collectAsState()
    val isLoading by registerViewModel.isLoading.collectAsState()
    val showSuccessDialog by registerViewModel.showSuccessDialog.collectAsState()
    val showErrorDialog by registerViewModel.showErrorDialog.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()

    //Manage password visibility
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    fun updateFields(
        newName: String = name,
        newEmail: String = email,
        newPassword: String = password,
        newConfirm: String = confirmPassword
    ) {
        registerViewModel.onRegisterChanged(newName, newEmail, newPassword, newConfirm)
    }

    Box(
        modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            TextField(
                value = name,
                onValueChange = { updateFields(newName = it) },
                label = { Text("Nombre*") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = email,
                onValueChange = { updateFields(newEmail = it) },
                label = { Text("Correo electrónico*") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = password,
                onValueChange = { updateFields(newPassword = it) },
                label = { Text("Contraseña*") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle contraseña")
                    }
                }
            )
            TextField(
                value = confirmPassword,
                onValueChange = { updateFields(newConfirm = it) },
                label = { Text("Repetir contraseña*") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = "Toggle confirmar contraseña"
                        )
                    }
                }
            )

            Button(
                onClick = {
                    registerViewModel.registerUser()
                },
                enabled = isRegisterEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
            Button(
                onClick = { backLoginScreen() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }

        }

        if (showSuccessDialog) {
            DialogSuccess(
                onDismiss = {
                    registerViewModel.dismissSuccessDialog()
                    onRegisterSuccess()
                }
            )
        }
        if (showErrorDialog) {
            DialogError(
                errorMessage = errorMessage,
                onDismiss = { registerViewModel.dismissErrorDialog() }
            )
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(color = Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
    }
    }
}


@Composable
fun DialogSuccess(onDismiss:() -> Unit) {
    AlertDialog(
        onDismissRequest = {onDismiss() },
        confirmButton = {
            Button(onClick = {
               onDismiss()
            }) {
                Text("Aceptar")
            }
        },
        title = { Text("Registro exitoso") },
        text = { Text("Tu cuenta ha sido creada correctamente.") }
    )
}

@Composable
fun DialogError(errorMessage: String,onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Entendido")
            }
        },
        title = { Text("Error en el registro") },
        text = { Text(errorMessage) }
    )
}