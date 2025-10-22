package me.tb.bitcoinsnake.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.tb.bitcoinsnake.domain.BitcoinWallet
import me.tb.bitcoinsnake.presentation.navigation.GameRoute
import me.tb.bitcoinsnake.presentation.navigation.PaymentRoute
import me.tb.bitcoinsnake.presentation.navigation.WelcomeRoute
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    wallet: BitcoinWallet,
) {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = WelcomeRoute
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onPlayClick = {
                        navController.navigate(PaymentRoute)
                    }
                )
            }

            composable<PaymentRoute> {
                PaymentScreen(
                    wallet,
                    onPayment = { navController.navigate(GameRoute) }
                )
            }

            composable<GameRoute> {
                SnakeGame()
            }
        }
    }
}
