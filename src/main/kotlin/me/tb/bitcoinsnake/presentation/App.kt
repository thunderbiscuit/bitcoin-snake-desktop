package me.tb.bitcoinsnake.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import me.tb.bitcoinsnake.domain.BitcoinWallet
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    wallet: BitcoinWallet,
) {
    MaterialTheme {
        SnakeGame(wallet)
    }
}
