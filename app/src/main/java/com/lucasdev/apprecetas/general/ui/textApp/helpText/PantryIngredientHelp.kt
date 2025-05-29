package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object PantryIngredientHelp {
    val pantryHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("¡Esta es tu despensa, aquí puedes ver qué tienes en la nevera!\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("Aquí podrás gestionar y ver los ingredientes que tienes disponibles en casa.\n\n")
                append("Usa el botón ➕ para añadir un nuevo ingrediente a tu despensa, selecciona la categoría y recuerda añadir la cantidad.\n")
                append("Es posible que cuando quieras añadir un ingrediente no esté registrado, puedes registrarlo tú mismo en la pantalla de 'Mis ingredientes'.\n\n")
                append("Si quieres modificar un ingrediente o verlo en más detalle, mantén pulsado sobre él.\n")
            }
        }
}