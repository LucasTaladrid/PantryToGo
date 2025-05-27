package com.lucasdev.apprecetas.users.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lucasdev.apprecetas.general.ui.appButtons.AppButton
import com.lucasdev.apprecetas.general.ui.appTextFields.AppTextField

//todo cambiar colores y fondo
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    backLoginScreen: () -> Unit
) {

    val name by registerViewModel.name.collectAsState()
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val isRegisterEnabled by registerViewModel.isRegisterEnabled.collectAsState()
    val isLoading by registerViewModel.isLoading.collectAsState()
    val showSuccessDialog by registerViewModel.showSuccessDialog.collectAsState()
    val showErrorDialog by registerViewModel.showErrorDialog.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()

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

            NameRegister(name, { updateFields(newName = it) })
            EmailRegister(email, { updateFields(newEmail = it) })
            PasswordRegister(password, { updateFields(newPassword = it) })
            ConfirmPasswordRegister(confirmPassword, { updateFields(newConfirm = it) })
            AppButton(
                text = "Registrarse",
                onClick = { registerViewModel.registerUser() },
                enabled = isRegisterEnabled
            )
            AppButton(
                text = "Volver",
                onClick = { backLoginScreen() },
                enabled = true
            )
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
fun DialogSuccess(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            AppButton(
                text = "Aceptar",
                onClick = { onDismiss() },
                enabled = true
            )
        },
        title = { Text("Registro exitoso") },
        text = { Text("Tu cuenta ha sido creada correctamente.") }
    )
}

@Composable
fun DialogError(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            AppButton(
                text = "Aceptar",
                onClick = { onDismiss() },
                enabled = true
            )
        },
        title = { Text("Error en el registro") },
        text = { Text(errorMessage) }
    )
}

@Composable
fun EmailRegister(email: String, updateFields: (String) -> Unit) {
    AppTextField(
        value = email,
        onValueChange = { updateFields(it) },
        placeholder = "Email*",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun NameRegister(name: String, updateFields: (String) -> Unit) {
    AppTextField(
        value = name,
        onValueChange = { updateFields(it) },
        placeholder = "Nombre*",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordRegister(password: String, updateFields: (String) -> Unit) {
    AppTextField(
        value = password,
        onValueChange = { updateFields(it) },
        placeholder = "Contraseña*",
        modifier = Modifier.fillMaxWidth(),
        isPassword = true
    )
}

@Composable
fun ConfirmPasswordRegister(confirmPassword: String, updateFields: (String) -> Unit) {
    AppTextField(
        value = confirmPassword,
        onValueChange = { updateFields(it) },
        placeholder = "Repetir contraseña*",
        modifier = Modifier.fillMaxWidth(),
        isPassword = true
    )
}
