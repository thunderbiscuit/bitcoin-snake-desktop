package me.tb.bitcoinsnake.presentation.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tb.bitcoinsnake.data.BitcoinWallet
import me.tb.bitcoinsnake.data.ElectrumClient
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
    val address: String = "",
    val isScanning: Boolean = false,
)

enum class PaymentEvent {
    SUCCESS,
}

class PaymentsViewModel(
    private val wallet: BitcoinWallet,
    // private val electrumUrl: String,
) {
    private val _paymentState: MutableStateFlow<PaymentState> = MutableStateFlow(PaymentState())
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _events = MutableSharedFlow<PaymentEvent>()
    val events: SharedFlow<PaymentEvent> = _events.asSharedFlow()

    // private val electrumClient: ElectrumClient = ElectrumClient(electrumUrl)
    private val electrumClient: ElectrumClient = ElectrumClient("tcp://127.0.0.1:60401")

    // Consider cleaning up the coroutines when the viewmodel gets destroyed
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun onAction(action: PaymentAction) {
        when (action) {
            is PaymentAction.CreateQrPractice -> preparePaymentScreen(PaymentType.PRACTICE)
            is PaymentAction.CreateQrGlory    -> preparePaymentScreen(PaymentType.GLORY)
            is PaymentAction.CheckPayment     -> coroutineScope.launch { scanForPayment() }
        }
    }

    private suspend fun scanForPayment() {
        _paymentState.value = _paymentState.value.copy(isScanning = true)
        val startTime = System.currentTimeMillis()

        withContext(Dispatchers.IO) {
            val syncRequest = wallet.getSyncRequest()
            val update = electrumClient.sync(syncRequest)
            wallet.updateWallet(update)
        }

        val address = paymentState.value.address
        val transactionPaid = wallet.checkTransaction(address)

        // Ensure at least 1000ms has passed to show the loading indicator
        val elapsedTime = System.currentTimeMillis() - startTime
        if (elapsedTime < 1000) {
            delay(1000 - elapsedTime)
        }

        if (transactionPaid) {
            _paymentState.value = _paymentState.value.copy(isScanning = false)
            _events.emit(PaymentEvent.SUCCESS)
        } else {
            _paymentState.value = _paymentState.value.copy(isScanning = false)
        }
    }

    private fun preparePaymentScreen(paymentType: PaymentType) {
        val address = wallet.getNewAddress()
        val bip21Uri = bip21Uri(address, paymentType)
        val qrCode = generateQRCodeImage(bip21Uri, 1000, 1000)

        val imageBitmap = qrCode.toComposeImageBitmap()

        val gameMode = if (paymentType == PaymentType.PRACTICE) GameMode.PRACTICE else GameMode.GLORY
        _paymentState.value = _paymentState.value.copy(gameMode = gameMode, qrCode = imageBitmap, address = address)
    }
}
