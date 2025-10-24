package me.tb.bitcoinsnake.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.composeunstyled.Button
import me.tb.bitcoinsnake.presentation.viewmodels.PaymentAction

@Composable
fun WelcomeScreen(
    onPaymentAction: (PaymentAction) -> Unit,
    onNavigation: () -> Unit
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

            Spacer(modifier = Modifier.height(60.dp))

            val practiceInteractionSource = remember { MutableInteractionSource() }
            val isPracticeHovered by practiceInteractionSource.collectIsHoveredAsState()

            Button(
                onClick = {
                    onPaymentAction(PaymentAction.CreateQrPractice)
                    onNavigation()
                },
                // onClick = {
                //     gameMode = GameMode.PRACTICE
                //     gameState = gameLogic.createInitialState(pauses = 0, lives = 1)
                //     modeSelectionDialogState.visible = false
                //     intermediaryDialogState.visible = true
                // },
                borderColor = Color(0xFF4CAF50),
                borderWidth = 2.dp,
                backgroundColor = if (isPracticeHovered) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Transparent,
                contentColor = Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .width(600.dp)
                    .height(48.dp)
                    .hoverable(practiceInteractionSource)
            ) {
                Text(
                    text = "I want practice",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isPracticeHovered) Color(0xFF66BB6A) else Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val gloryInteractionSource = remember { MutableInteractionSource() }
            val isGloryHovered by gloryInteractionSource.collectIsHoveredAsState()

            Button(
                onClick = {
                    onPaymentAction(PaymentAction.CreateQrGlory)
                    onNavigation()
                },
                // onClick = {
                //     gameMode = GameMode.GLORY
                //     modeSelectionDialogState.visible = false
                //     intermediaryDialogState.visible = true
                // },
                borderColor = Color(0xFF4CAF50),
                borderWidth = 2.dp,
                backgroundColor = if (isGloryHovered) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Transparent,
                contentColor = Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .width(600.dp)
                    .height(48.dp)
                    .hoverable(gloryInteractionSource)
            ) {
                Text(
                    text = "I want glory",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isGloryHovered) Color(0xFF66BB6A) else Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val leaderboardInteractionSource = remember { MutableInteractionSource() }
            val isLeaderboardHovered by leaderboardInteractionSource.collectIsHoveredAsState()

            Button(
                onClick = {},
                // onClick = {
                //     modeSelectionDialogState.visible = false
                //     leaderboardDialogState.visible = true
                // },
                borderColor = Color(0xFF4CAF50),
                borderWidth = 2.dp,
                backgroundColor = if (isLeaderboardHovered) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color.Transparent,
                contentColor = Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .width(600.dp)
                    .height(48.dp)
                    .hoverable(leaderboardInteractionSource)
            ) {
                Text(
                    text = "Show me the leaderboard",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isLeaderboardHovered) Color(0xFF66BB6A) else Color(0xFF4CAF50)
                )
            }
        }
    }
}
