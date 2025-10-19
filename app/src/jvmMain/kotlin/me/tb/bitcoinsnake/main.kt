package me.tb.bitcoinsnake

import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    var restartTrigger by remember { mutableStateOf(0) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Bitcoin Snake",
        state = rememberWindowState(
            width = 1100.dp,
            height = 900.dp,
            position = WindowPosition(Alignment.Center)
        )
    ) {
        MenuBar {
            Menu("Game") {
                Item(
                    "Restart",
                    onClick = {
                        restartTrigger++
                    },
                    shortcut = KeyShortcut(Key.R, meta = true)
                )
            }
        }

        key(restartTrigger) {
            App()
        }
    }
}