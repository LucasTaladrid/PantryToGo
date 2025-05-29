package com.lucasdev.apprecetas.general.ui.textApp.helpText

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

object MyShoppingHistoryHelp {
    val myShoppingHistoryHelp: AnnotatedString
        get() = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                append("Estas son tus últimas compras. Aquí puedes ver la fecha, los ingredientes y las cantidades que has ido adquiriendo con el tiempo.\n\n")
            }
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                append("Pulsa sobre la fecha para desplegar la lista completa.\n")
                append("Si quieres borrar una lista, solo tienes que pulsar en eliminar.\n")
                append("Recuerda que puedes realizar tus compras directamente desde la pantalla de compras, confirmando la lista que hayas preparado.\n")
                append("Ten en cuenta que solo guardamos las últimas 5 compras para que tengas siempre a mano lo más reciente.\n")
            }
        }

}

