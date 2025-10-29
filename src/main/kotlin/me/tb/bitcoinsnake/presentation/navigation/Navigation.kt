package me.tb.bitcoinsnake.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import me.tb.bitcoinsnake.data.BitcoinWallet
import me.tb.bitcoinsnake.domain.SnakeGameLogic
import me.tb.bitcoinsnake.presentation.PaymentScreen
import me.tb.bitcoinsnake.presentation.SnakeGame
import me.tb.bitcoinsnake.presentation.WelcomeScreen
import me.tb.bitcoinsnake.presentation.viewmodels.PaymentsViewModel

@Composable
fun Navigation(wallet: BitcoinWallet) {
    val startingScreen = Destinations.WelcomeScreen
    val backStack = remember { mutableStateListOf<Destinations>(startingScreen) }
    val viewModel = remember { PaymentsViewModel(wallet) }

    NavDisplay(
        backStack = backStack,
        onBack = { },
        entryProvider = entryProvider {
            entry<Destinations.WelcomeScreen> {
                WelcomeScreen(
                    onPaymentAction = viewModel::onAction,
                    onNavigation = { backStack.add(Destinations.PaymentScreen) }
                )
            }

            entry<Destinations.PaymentScreen> {
                val state by viewModel.paymentState.collectAsState()

                PaymentScreen(
                    state = state,
                    events = viewModel.events,
                    onAction = viewModel::onAction,
                    onNavigation = {
                        val mode = if (state.gameMode == me.tb.bitcoinsnake.domain.GameMode.PRACTICE) "practice" else "glory"
                        backStack.add(Destinations.GameScreen(mode))
                    }
                )
            }

            entry<Destinations.GameScreen> { (gameMode) ->
                SnakeGame(gameMode = gameMode)
            }
        }
    )
}
