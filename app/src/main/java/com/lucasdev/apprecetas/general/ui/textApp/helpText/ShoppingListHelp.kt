package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object ShoppingListHelp {
    val shoppingListHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("¡Hola, esta es tu lista de la compra!\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("Aquí podrás gestionar fácilmente los ingredientes que necesitas comprar, ¡muy conveniente para organizarte mejor!\n\n")
                append("Usa el botón ➕ para añadir un nuevo ingrediente a tu lista.\n")
                append("Marca los ingredientes que ya hayas comprado.\n")
                append("Cuando termines, pulsa 'Finalizar Compra' y los ingredientes marcados se moverán automáticamente a tu despensa.\n\n")
                append("Además, las recetas que marques como pendientes añaden automáticamente los ingredientes necesarios a esta lista, para que no se te escape nada.\n\n")
                append("Puedes consultar tus últimas compras en la pantalla 'Últimas compras'.")
            }

        }
}