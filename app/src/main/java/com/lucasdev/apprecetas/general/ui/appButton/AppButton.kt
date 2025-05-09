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
    modifier: Modifier = Modifier,
    fullWidth: Boolean = true,
    containerColor: Color = Color(0xFFD00E0E),
    disabledContainerColor: Color = Color(0xFF5B1421),
    contentColor: Color = Color.White,
    disabledContentColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = disabledContainerColor,
            contentColor = contentColor,
            disabledContentColor = disabledContentColor
        ),
        modifier = if (fullWidth) modifier.fillMaxWidth() else modifier
    ) {
        Text(text = text)
    }
}
