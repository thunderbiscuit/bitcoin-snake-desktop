package me.tb.bitcoinsnake.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute

@Serializable
object PaymentRoute

@Serializable
sealed interface Destinations : NavKey {
    @Serializable
    object WelcomeScreen : Destinations
    @Serializable
    object PaymentScreen : Destinations
}
