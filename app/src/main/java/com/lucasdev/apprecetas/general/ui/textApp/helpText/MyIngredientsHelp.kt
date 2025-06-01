package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object MyIngredientsHelp {
    val myIngredientsHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("¡Aquí puedes gestionar tus ingredientes registrados y personalizar tu experiencia en la aplicación!\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("Usa el botón ➕ para registrar un nuevo ingrediente. Selecciona la categoría y la unidad de medida correspondiente.\n\n")
                append("Una vez registrado, podrás añadir ese ingrediente a tu despensa, lista de la compra o recetas.\n\n")
                append("Si deseas modificar un ingrediente o ver más detalles, mantén pulsado sobre él. Ten en cuenta que el nombre no se puede modificar.\n\n")
                append("Si el nombre del ingrediente se autocompleta, significa que ya existe en la aplicación. Siempre puedes darle un nombre más específico si lo necesitas. Por ejemplo: arroz largo, basmati, bomba, etc.")
            }
        }
}
