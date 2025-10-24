package me.tb.bitcoinsnake.presentation.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tb.bitcoinsnake.data.BitcoinWallet
import me.tb.bitcoinsnake.domain.GameMode
import me.tb.bitcoinsnake.domain.PaymentType
import me.tb.bitcoinsnake.domain.bip21Uri
import me.tb.bitcoinsnake.domain.generateQRCodeImage
import org.bitcoindevkit.Amount

sealed interface PaymentAction {
    data object CreateQrPractice : PaymentAction
    data object CreateQrGlory : PaymentAction
    data object CheckPayment : PaymentAction
}

data class PaymentState(
    val gameMode: GameMode? = null,
    val paymentValue: Amount? = null,
    val qrCode: ImageBitmap? = null,
)

class PaymentsViewModel(
    private val wallet: BitcoinWallet,
) {
    private val _paymentState: MutableStateFlow<PaymentState> = MutableStateFlow(PaymentState())
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    fun onAction(action: PaymentAction) {
        when (action) {
            PaymentAction.CreateQrPractice -> preparePaymentScreen(PaymentType.PRACTICE)
            PaymentAction.CreateQrGlory    -> preparePaymentScreen(PaymentType.GLORY)
            PaymentAction.CheckPayment     -> { }
        }
    }

    private fun preparePaymentScreen(paymentType: PaymentType) {
        val address = wallet.getNewAddress()
        val bip21Uri = bip21Uri(address, paymentType)
        val qrCode = generateQRCodeImage(bip21Uri, 1000, 1000)

        val imageBitmap = qrCode.toComposeImageBitmap()

        val gameMode = if (paymentType == PaymentType.PRACTICE) GameMode.PRACTICE else GameMode.GLORY
        _paymentState.value = _paymentState.value.copy(gameMode = gameMode, qrCode = imageBitmap)
    }
}
