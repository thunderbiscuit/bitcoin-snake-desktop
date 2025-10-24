package me.tb.bitcoinsnake.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.tb.bitcoinsnake.data.BitcoinWallet
import me.tb.bitcoinsnake.presentation.navigation.PaymentRoute
import me.tb.bitcoinsnake.presentation.navigation.WelcomeRoute
import me.tb.bitcoinsnake.presentation.viewmodels.PaymentsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    wallet: BitcoinWallet,
) {
    MaterialTheme {
        val navController = rememberNavController()
        val viewModel = remember { PaymentsViewModel(wallet) }

        NavHost(
            navController = navController,
            startDestination = WelcomeRoute
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    onPaymentAction = viewModel::onAction,
                    onNavigation = { navController.navigate(PaymentRoute) }
                )
            }

            composable<PaymentRoute> {
                val state by viewModel.paymentState.collectAsState()

                PaymentScreen(
                    state = state,
                    onAction = viewModel::onAction,
                )
            }
        }
    }
}
