package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object RecipesMainHelp {
    val recipesMainHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("Aquí puedes ver las recetas de la aplicación.\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("A lo largo del tiempo, iremos añadiendo nuevas recetas para que siempre tengas opciones frescas y deliciosas. ")
                append("Además, con el cambio de estación, incluimos recetas que aprovechan los ingredientes de temporada, para que disfrutes de los mejores sabores y productos frescos.\n\n")
                append("No te pierdas las últimas novedades y explora todas las recetas para encontrar tu próxima comida favorita. ¡Buen provecho!")
            }
        }
}