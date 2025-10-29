package me.tb.bitcoinsnake.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import me.tb.bitcoinsnake.data.BitcoinWallet
import me.tb.bitcoinsnake.presentation.navigation.Navigation

@Composable
fun App(
    wallet: BitcoinWallet,
) {
    MaterialTheme {
        Navigation(wallet)
    }
}
