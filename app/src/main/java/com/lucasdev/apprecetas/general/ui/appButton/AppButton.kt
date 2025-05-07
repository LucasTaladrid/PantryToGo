package com.lucasdev.apprecetas.general.ui.appButton

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD00E0E),
            disabledContainerColor = Color(0xFF5B1421),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = text)
    }
}
