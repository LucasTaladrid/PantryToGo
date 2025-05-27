package com.lucasdev.apprecetas.general.ui.appTextFields

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
/**
 * A customizable outlined text field with label support and keyboard type options.
 *
 * @param value The current text to be shown in the text field.
 * @param onValueChange Callback invoked when the input text changes.
 * @param label The label text displayed inside the outlined text field.
 * @param modifier Modifier to be applied to the text field (default is [Modifier]).
 * @param keyboardType The type of keyboard to be shown (default is [KeyboardType.Text]).
 * @param enabled Controls whether the text field is enabled or disabled (default is true).
 */
@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        maxLines = 1,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Gray,
            focusedBorderColor = Color(0xFFB2B2B2),
            unfocusedBorderColor = Color(0xFFB2B2B2),
            disabledBorderColor = Color.LightGray,
            disabledTextColor = Color.LightGray
        ),
        modifier = modifier.fillMaxWidth()
    )
}


