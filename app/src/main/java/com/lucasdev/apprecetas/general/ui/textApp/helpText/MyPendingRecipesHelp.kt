package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object MyPendingRecipesHelp {
    val myPendingRecipesHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("¡Aquí puedes ver tus recetas pendientes.!\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("Si aún no tienes recetas marcadas, dirígete a la pantalla de recetas y selecciona las que quieras añadir.\n\n")
                append("Al lado de cada receta encontrarás dos iconos: el ")
                append("❤️ ")
                append("para marcarlas como favoritas, y el ")
                append("🔖 ")
                append("para guardarlas como pendientes.\n\n")
                append("Al guardar una receta como pendiente, le indicas a la aplicación que quieres hacerla pronto, así te ayudamos añadiendo los ingredientes que " +
                        "te faltan a la lista de la compra. Una vez hayas preparado la receta, solo tienes que presionar el botón 'Marcar como cocinada' y" +
                        " la aplicación eliminará automáticamente esos ingredientes de tu despensa. ¿A que es cómodo?")
            }
        }
}