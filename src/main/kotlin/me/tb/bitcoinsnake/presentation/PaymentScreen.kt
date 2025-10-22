package me.tb.bitcoinsnake.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import me.tb.bitcoinsnake.domain.BitcoinWallet
import me.tb.bitcoinsnake.domain.PaymentType
import me.tb.bitcoinsnake.domain.bip21Uri
import me.tb.bitcoinsnake.domain.generateQRCodeImage

@Composable
fun PaymentScreen(
    wallet: BitcoinWallet,
    onPayment: () -> Unit
) {
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
            Text(
                text = "Get Ready!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(20.dp))

            val address = wallet.getNewAddress()
            val bip21Uri = bip21Uri(address, PaymentType.GLORY)
            val qrCode = generateQRCodeImage(bip21Uri, 1000, 1000)

            val imageBitmap = qrCode.toComposeImageBitmap()

            Image(
                bitmap = imageBitmap,
                contentDescription = "QR Code",
                modifier = Modifier
                    .height(400.dp)
                    .width(400.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Button(
                onClick = { onPayment() },
            ) {
                Text("Start Game", fontFamily = FontFamily.Monospace)
            }
        }
    }
}
