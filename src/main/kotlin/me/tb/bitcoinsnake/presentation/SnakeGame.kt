package me.tb.bitcoinsnake.presentation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trophy
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.composables.core.Dialog
import com.composables.core.DialogPanel
import com.composables.core.Scrim
import com.composables.core.rememberDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import me.tb.bitcoinsnake.data.LeaderboardManager
import me.tb.bitcoinsnake.domain.BitcoinWallet
import me.tb.bitcoinsnake.domain.CELL_SIZE
import me.tb.bitcoinsnake.domain.Direction
import me.tb.bitcoinsnake.domain.GRID_SIZE
import me.tb.bitcoinsnake.domain.PaymentType
import me.tb.bitcoinsnake.domain.SnakeGameLogic
import me.tb.bitcoinsnake.domain.Speed
import me.tb.bitcoinsnake.domain.bip21Uri
import me.tb.bitcoinsnake.domain.generateQRCodeImage

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SnakeGame(
    wallet: BitcoinWallet,
    onRestartRequest: (() -> Unit)? = null
) {
    val gameLogic = remember { SnakeGameLogic() }
    val leaderboardManager = remember { LeaderboardManager() }
    var gameState by remember { mutableStateOf(gameLogic.createInitialState()) }
    var currentDirection by remember { mutableStateOf(Direction.RIGHT) }
    var isPlaying by remember { mutableStateOf(false) }
    val modeSelectionDialogState = rememberDialogState(initiallyVisible = true)
    var gameMode by remember { mutableStateOf<String?>(null) }
    val intermediaryDialogState = rememberDialogState()
    val nameDialogState = rememberDialogState()
    val leaderboardDialogState = rememberDialogState()
    var playerName by remember { mutableStateOf("") }
    var leaderboard by remember { mutableStateOf(leaderboardManager.loadLeaderboard()) }
    val focusRequester = remember { FocusRequester() }

    // Expose restart function
    LaunchedEffect(onRestartRequest) {
        onRestartRequest?.let {
            // Function is available for external restart
        }
    }

    // Handle respawn when player has lives
    LaunchedEffect(gameState.deathPosition, gameState.lives) {
        if (gameState.deathPosition != null && gameState.lives > 0 && !gameState.isGameOver) {
            isPlaying = false
            delay(1000) // Show death animation for 1 second
            gameState = gameLogic.respawnSnake(gameState)
            currentDirection = Direction.RIGHT
            // Don't auto-resume, wait for player to press a key
        }
    }

    // Game loop with dynamic speed
    LaunchedEffect(isPlaying, gameState.score) {
        if (isPlaying && !gameState.isGameOver) {
            while (isActive && !gameState.isGameOver) {
                // Speed increases every 16 foods eaten
                val currentSpeed = when (gameState.score) {
                    in  0..15 -> Speed.SLOW
                    in 16..31 -> Speed.MEDIUM
                    in 32..47 -> Speed.FAST
                    in 48..63 -> Speed.VERY_FAST
                    else            -> Speed.EXTREME
                }
                delay(currentSpeed.delayMs)
                gameState = gameLogic.updateGame(gameState, currentDirection)
            }
            if (gameState.isGameOver) {
                isPlaying = false
                // Show name dialog if in glory mode and it's a top score
                if (gameMode == "glory" && leaderboard.isTopScore(gameState.score)) {
                    nameDialogState.visible = true
                }
            }
        }
    }

    // Request focus when component mounts
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Mode selection dialog
    Dialog(state = modeSelectionDialogState) {
        Scrim(
            scrimColor = Color.Black.copy(alpha = 0.8f),
            enter = fadeIn(),
            exit = fadeOut()
        )

        DialogPanel(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                .padding(40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Bitcoin Snake",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        gameMode = "practice"
                        gameState = gameLogic.createInitialState(pauses = 0, lives = 1)
                        modeSelectionDialogState.visible = false
                        intermediaryDialogState.visible = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I want practice", fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = {
                        gameMode = "glory"
                        modeSelectionDialogState.visible = false
                        intermediaryDialogState.visible = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I want glory", fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = {
                        modeSelectionDialogState.visible = false
                        leaderboardDialogState.visible = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show me the leaderboard", fontFamily = FontFamily.Monospace)
                }
            }
        }
    }

    // Intermediary dialog (before game starts)
    Dialog(state = intermediaryDialogState) {
        Scrim(
            scrimColor = Color.Black.copy(alpha = 0.8f),
            enter = fadeIn(),
            exit = fadeOut()
        )

        DialogPanel(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                .padding(40.dp)
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
                    onClick = {
                        intermediaryDialogState.visible = false
                        isPlaying = false
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Game", fontFamily = FontFamily.Monospace)
                }
            }
        }
    }

    // Leaderboard modal
    Dialog(state = leaderboardDialogState) {
        Scrim(
            scrimColor = Color.Black.copy(alpha = 0.8f),
            enter = fadeIn(),
            exit = fadeOut()
        )

        DialogPanel(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                .padding(40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text(
                    text = "Top 10 Leaderboard",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF4CAF50),
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (leaderboard.entries.isEmpty()) {
                    Text(
                        text = "No scores yet!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    leaderboard.entries.forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}. ${entry.playerName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${entry.score}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Show back button only if no game mode is selected yet
                if (gameMode == null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            leaderboardDialogState.visible = false
                            modeSelectionDialogState.visible = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back", fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }

    // Name input dialog for leaderboard
    Dialog(state = nameDialogState) {
        Scrim(
            scrimColor = Color.Black.copy(alpha = 0.8f),
            enter = fadeIn(),
            exit = fadeOut()
        )

        DialogPanel(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                .padding(40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Top 10 Score!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF4CAF50),
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = "Score: ${gameState.score}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("Enter your name", fontFamily = FontFamily.Monospace) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (playerName.isNotBlank()) {
                            leaderboard = leaderboardManager.addScore(playerName.trim(), gameState.score)
                            nameDialogState.visible = false
                            playerName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Score", fontFamily = FontFamily.Monospace)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionUp, Key.W -> {
                            if (isPlaying) {
                                currentDirection = Direction.UP
                                true
                            } else if (!gameState.isGameOver && gameMode == "practice") {
                                // In practice mode, any direction key starts the game after respawn
                                currentDirection = Direction.UP
                                isPlaying = true
                                true
                            } else false
                        }
                        Key.DirectionDown, Key.S -> {
                            if (isPlaying) {
                                currentDirection = Direction.DOWN
                                true
                            } else if (!gameState.isGameOver && gameMode == "practice") {
                                currentDirection = Direction.DOWN
                                isPlaying = true
                                true
                            } else false
                        }
                        Key.DirectionLeft, Key.A -> {
                            if (isPlaying) {
                                currentDirection = Direction.LEFT
                                true
                            } else if (!gameState.isGameOver && gameMode == "practice") {
                                currentDirection = Direction.LEFT
                                isPlaying = true
                                true
                            } else false
                        }
                        Key.DirectionRight, Key.D -> {
                            if (isPlaying) {
                                currentDirection = Direction.RIGHT
                                true
                            } else if (!gameState.isGameOver && gameMode == "practice") {
                                currentDirection = Direction.RIGHT
                                isPlaying = true
                                true
                            } else false
                        }
                        Key.Spacebar -> {
                            if (!isPlaying && !gameState.isGameOver && gameMode == "glory") {
                                // Unpause (only in glory mode)
                                isPlaying = true
                            } else if (!isPlaying && !gameState.isGameOver && gameMode == "practice") {
                                // Start game in practice mode
                                isPlaying = true
                            } else if (isPlaying && gameState.pauses > 0 && gameMode == "glory") {
                                // Pause (only if pauses remaining and in glory mode)
                                isPlaying = false
                                gameState = gameState.copy(pauses = gameState.pauses - 1)
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Score, pauses, and lives display
        Row(
            modifier = Modifier.padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Text(
                text = "Score: ${gameState.score}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
            if (gameMode == "practice") {
                Text(
                    text = "Lives: ${gameState.lives + 1}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (!gameState.isGameOver) Color(0xFFFF9800) else Color.Gray,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Text(
                    text = "Pauses: ${gameState.pauses}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (gameState.pauses > 0) Color(0xFF4CAF50) else Color.Gray,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Game board
        Canvas(
            modifier = Modifier
                .size((GRID_SIZE * CELL_SIZE).dp)
                .background(color = Color(0xFF2D2D2D), shape = RoundedCornerShape(8.dp))
        ) {
            val cellSizePx = CELL_SIZE * density

            // Draw snake
            gameState.snake.forEachIndexed { index, position ->
                val color = if (index == 0) Color(0xFF4CAF50) else Color(0xFF8BC34A)
                drawRect(
                    color = color,
                    topLeft = Offset(position.x * cellSizePx, position.y * cellSizePx),
                    size = Size(cellSizePx - 2, cellSizePx - 2)
                )
            }

            // Draw food
            drawRoundRect(
                color = Color(0xFFF44336),
                topLeft = Offset(gameState.food.x * cellSizePx, gameState.food.y * cellSizePx),
                size = Size(cellSizePx - 4, cellSizePx - 4),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Draw X at death position if game is over
            gameState.deathPosition?.let { deathPos ->
                val centerX = deathPos.x * cellSizePx + cellSizePx / 2
                val centerY = deathPos.y * cellSizePx + cellSizePx / 2
                val offset = cellSizePx / 3

                // Draw X with two lines
                drawLine(
                    color = Color.Black,
                    start = Offset(centerX - offset, centerY - offset),
                    end = Offset(centerX + offset, centerY + offset),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(centerX + offset, centerY - offset),
                    end = Offset(centerX - offset, centerY + offset),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Use arrow keys or WASD to control the snake.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontFamily = FontFamily.Monospace
            )
            if (gameMode == "glory") {
                Text(
                    text = "Press SPACE to pause/unpause (1 pause per game).",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontFamily = FontFamily.Monospace
                )
            } else if (gameMode == "practice") {
                Text(
                    text = "Practice mode: 1 extra life, no pauses. Press SPACE to start.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF9800),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        }

        // Leaderboard icon button (top right)
        IconButton(
            onClick = { leaderboardDialogState.visible = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Lucide.Trophy,
                contentDescription = "Show Leaderboard",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
