package com.lucasdev.apprecetas.users.ui.login

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lucasdev.apprecetas.R
import com.lucasdev.apprecetas.general.ui.appButtons.AppButton
import com.lucasdev.apprecetas.general.ui.appTextFields.AppTextField


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginScreenViewModel: LoginScreenViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val isLoading by loginScreenViewModel.isLoading.collectAsState(initial = false)
    Box(
        modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .background(Color.White)
    ) {
        Header(Modifier.align(Alignment.TopEnd))
        Body(Modifier.align(Alignment.Center), loginScreenViewModel, onLoginSuccess)
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Blue)
            }
        }
        Footer(Modifier.align(Alignment.BottomCenter), onNavigateToRegister)
    }

}

@Composable
fun Header(modifier: Modifier) {
    val activity = LocalActivity.current as Activity
    Icon(
        imageVector = Icons.Default.Clear,
        contentDescription = "Close app",
        modifier = modifier.clickable { activity.finish() })
}

@Composable
fun Footer(modifier: Modifier, onNavigateToRegister: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Divider(
            Modifier
                .background(Color(0xFFF9F9F9))
                .height(1.dp)
                .fillMaxWidth(),
            color = colorResource(id = R.color.orange_test)
        )
        Spacer(modifier = Modifier.size(24.dp))
        SingUp(onNavigateToRegister)
        Spacer(modifier = Modifier.size(24.dp))

    }

}

@Composable
fun SingUp(onNavigateToRegister: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onNavigateToRegister() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿No tienes una cuenta?",
            color = Color.Black,
            fontSize = 12.sp
        )
        Text(
            text = "Inscribete.",
            Modifier.padding(horizontal = 8.dp),
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Body(
    modifier: Modifier,
    loginScreenViewModel: LoginScreenViewModel,
    onLoginSuccess: () -> Unit
) {

    val email: String by loginScreenViewModel.email.collectAsState(initial = "")
    val password: String by loginScreenViewModel.password.collectAsState(initial = "")
    val isLoginEnable: Boolean by loginScreenViewModel.loginEnable.collectAsState(initial = false)

    Column(modifier = modifier) {
        ImageLogo(Modifier.align(Alignment.CenterHorizontally), 240)
        NameApp()
        Spacer(modifier = Modifier.size(16.dp))
        Email(email, { loginScreenViewModel.onLoginChanged(email = it, password = password) })
        Spacer(modifier = Modifier.size(4.dp))
        Password(password, { loginScreenViewModel.onLoginChanged(email = email, password = it) })
        Spacer(modifier = Modifier.size(8.dp))
        //ForgotPassword(Modifier.align(Alignment.End))
        Spacer(modifier = Modifier.size(16.dp))
        LoginButton(isLoginEnable, loginScreenViewModel, onLoginSuccess)
        Spacer(modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.size(32.dp))
        //SocialLogin()
    }
}
//todo add this feature in the future,allow login with google account
/*
@Composable
fun SocialLogin() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            //painter = painterResource(id = Icons.Outlined.Goo),
            contentDescription = "Social login google",
            modifier = Modifier.size(16.dp)
        )
        /*
        Text(
            text = "Continue as Lucas",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color(0xFF4EA8E9)
        )

         */
    }
}
*/




@Composable
fun LoginButton(
    loginEnable: Boolean,
    loginScreenViewModel: LoginScreenViewModel,
    onLoginSuccess: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val email: String by loginScreenViewModel.email.collectAsState(initial = "")
    val password: String by loginScreenViewModel.password.collectAsState(initial = "")

    Column {
        AppButton(
            text = "Conectarse",
            onClick = {
                loginScreenViewModel.loginUser(
                    onSuccess = {
                        errorMessage = null
                        onLoginSuccess()
                    },
                    onError = {
                        errorMessage = it
                    },
                    email = email,
                    password = password
                )
            },
            enabled = loginEnable
        )
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/*
todo add this feature in the future
@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        "Forgot password?",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4EA8E9),
        modifier = modifier.clickable {})
}

 */

@Composable
fun Password(password: String, onTextChange: (String) -> Unit) {

    AppTextField(
        value = password,
        onValueChange = { onTextChange(it) },
        placeholder = "Contraseña",
        keyboardType = KeyboardType.Password,
        isPassword = true,
    )
}

@Composable
fun Email(email: String, onTextChange: (String) -> Unit) {
    AppTextField(
        value = email,
        onValueChange = { onTextChange(it) },
        placeholder = "Email",
        keyboardType = KeyboardType.Email
    )
}

@Composable
fun ImageLogo(modifier: Modifier, size: Int) {
    Image(
        painter = painterResource(R.drawable.logo_app),
        contentDescription = "Logo",
        modifier = modifier.size(size.dp)
    )
}

@Composable
fun NameApp() {
    Text(
        text = "Pantry to go",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(top = 8.dp, bottom = 16.dp)
    )
}