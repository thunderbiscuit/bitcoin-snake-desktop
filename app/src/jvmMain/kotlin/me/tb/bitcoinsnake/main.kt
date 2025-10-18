package me.tb.bitcoinsnake

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bitcoin Snake",
        state = rememberWindowState(
            width = 1100.dp,
            height = 900.dp,
            position = androidx.compose.ui.window.WindowPosition(Alignment.Center)
        )
    ) {
        App()
    }
}