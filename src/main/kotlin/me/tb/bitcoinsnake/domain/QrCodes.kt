package me.tb.bitcoinsnake.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.awt.image.BufferedImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import org.kotlinbitcointools.bip21.Bip21URI
import org.kotlinbitcointools.bip21.parameters.Amount
import org.kotlinbitcointools.bip21.parameters.Message

fun bip21Uri(address: String, paymentType: PaymentType): String {
    val amount = if (paymentType == PaymentType.PRACTICE) Amount(1000L) else Amount(5000L)
    val message = if (paymentType == PaymentType.PRACTICE) Message("Practicing snake") else Message("Playing snake for glory")

    return Bip21URI(
        address = address,
        amount = amount,
        message = message
    ).toURI()
}

fun generateQRCodeImage(content: String, width: Int, height: Int): BufferedImage {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height)

    val darkSquares: Int = Color.Black.toArgb()
    val background: Int = Color.White.toArgb()
    val config = MatrixToImageConfig(darkSquares, background)

    return MatrixToImageWriter.toBufferedImage(bitMatrix, config)
}
