package me.tb.bitcoinsnake.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Button
import kotlinx.coroutines.flow.SharedFlow
import me.tb.bitcoinsnake.domain.GameMode
import me.tb.bitcoinsnake.presentation.viewmodels.PaymentAction
import me.tb.bitcoinsnake.presentation.viewmodels.PaymentEvent
import me.tb.bitcoinsnake.presentation.viewmodels.PaymentState

@Composable
fun PaymentScreen(
    state: PaymentState,
    events: SharedFlow<PaymentEvent>,
    onAction: (PaymentAction) -> Unit,
    onNavigation: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                PaymentEvent.SUCCESS -> onNavigation()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val modeText = if (state.gameMode == GameMode.PRACTICE) "You are playing in PRACTICE MODE" else "You are playing in GLORY MODE"
            val infoText = if (state.gameMode == GameMode.PRACTICE) "1000 satoshis. 2 lives. None of the glory." else "5000 satoshis. 1 life. All the glory."

            Text(
                text = modeText,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = infoText,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                bitmap = state.qrCode!!,
                contentDescription = "QR Code - Click to copy address",
                modifier = Modifier
                    .height(400.dp)
                    .width(400.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(state.address))
                    }
                    .pointerHoverIcon(PointerIcon.Hand)
            )

            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()

            Button(
                onClick = { onAction(PaymentAction.CheckPayment) },
                enabled = !state.isScanning,
                borderColor = Color(0xFF4CAF50),
                borderWidth = 2.dp,
                backgroundColor = if (isHovered) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Transparent,
                contentColor = Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(48.dp)
                    .hoverable(interactionSource)
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF4CAF50),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "Scan for Payment",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = if (isHovered) Color(0xFF66BB6A) else Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}
