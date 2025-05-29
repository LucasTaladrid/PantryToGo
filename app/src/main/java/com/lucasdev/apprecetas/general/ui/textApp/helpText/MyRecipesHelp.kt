package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object MyRecipesHelp {
    val myRecipesHelp: AnnotatedString
    get() = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
            append("Aquí puedes ver tus propias recetas y modificarlas.\n\n")
        }
        withStyle(style = SpanStyle(fontSize = 16.sp)) {
            append("Si aún no tienes recetas, pulsa el botón ➕.\n\n")
            append("Podrás darle un nombre a la receta y escribir los diferentes pasos a seguir. Cuando hagas un salto de línea, " +
                    "la aplicación entiende que es otro paso más, ya que luego los numera.\n Por último, puedes añadir los ingredientes y cantidades " +
                    "que hacen falta para prepararla. Recuerda que si no encuentras los ingredientes que te hacen falta, siempre puedes añadirlos en \"Mis ingredientes\".\n\n")
            append("¿Vas a hacer algo rico?")
        }
    }
}
