package me.tb.bitcoinsnake.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {
    @Serializable
    object WelcomeScreen : Destinations
    @Serializable
    object PaymentScreen : Destinations
    @Serializable
    data class GameScreen(val gameMode: String) : Destinations
}
