package com.lucasdev.apprecetas.general.ui.appButtons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.lucasdev.apprecetas.R

/**
 * Custom button with configurable colors, width, and enabled state.
 *
 * @param text The text displayed inside the button.
 * @param onClick The action to perform when the button is clicked.
 * @param enabled Whether the button is enabled or disabled. Defaults to true.
 * @param modifier Jetpack Compose modifier for layout or behavior customization.
 * @param fullWidth If true, the button will occupy the full available width. Defaults to true.
 * @param containerColor Background color of the button when enabled.
 * @param disabledContainerColor Background color of the button when disabled.
 * @param contentColor Color of the button’s content (text) when enabled.
 * @param disabledContentColor Color of the button’s content (text) when disabled.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = true,
    containerColor: Color = colorResource(id = R.color.orange),
    disabledContainerColor: Color = colorResource(id=R.color.personal_gray),
    contentColor: Color = Color.Black,
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
