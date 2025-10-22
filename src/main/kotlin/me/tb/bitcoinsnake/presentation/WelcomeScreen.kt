package me.tb.bitcoinsnake.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import me.tb.bitcoinsnake.generated.resources.Res
import me.tb.bitcoinsnake.generated.resources.snake

@Composable
fun WelcomeScreen(
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bitcoin Snake",
                fontFamily = FontFamily.Monospace,
                fontSize = 48.sp,
                color = Color(0xFF58A6FF),
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(Res.drawable.snake),
                contentDescription = "Retro Snake",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238636),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "PLAY!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
