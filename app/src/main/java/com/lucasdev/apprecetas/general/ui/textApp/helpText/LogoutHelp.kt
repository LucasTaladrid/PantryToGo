package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object LogoutHelp {
    val logoutHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("Ajustes de la aplicación.\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("Presiona salir para salir de cuenta.\n\n")
            }
        }
}
