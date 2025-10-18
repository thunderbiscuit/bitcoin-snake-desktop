package me.tb.bitcoinsnake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

// Data classes for game state
data class Position(val x: Int, val y: Int)

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

enum class Speed(val delayMs: Long) {
    SLOW(170),
    MEDIUM(140),
    FAST(110),
    VERY_FAST(90),
    EXTREME(70)
}

data class GameState(
    val snake: List<Position>,
    val food: Position,
    val direction: Direction,
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val pauses: Int = 1,
    val deathPosition: Position? = null
)

// Game constants
const val GRID_SIZE = 24
const val CELL_SIZE = 25f

class SnakeGameLogic(
    private val gridSize: Int = GRID_SIZE
) {
    fun createInitialState(): GameState {
        val initialSnake = listOf(
            Position(8, 12),
            Position(7, 12),
            Position(6,12)
        )
        return GameState(
            snake = initialSnake,
            food = Position(16, 12),
            direction = Direction.RIGHT,
            score = 0,
            pauses = 1
        )
    }

    fun updateGame(state: GameState, newDirection: Direction): GameState {
        if (state.isGameOver) return state

        // Check if player is trying to reverse direction - if so, game over
        val isReverseDirection = when {
            state.direction == Direction.UP && newDirection == Direction.DOWN -> true
            state.direction == Direction.DOWN && newDirection == Direction.UP -> true
            state.direction == Direction.LEFT && newDirection == Direction.RIGHT -> true
            state.direction == Direction.RIGHT && newDirection == Direction.LEFT -> true
            else -> false
        }

        if (isReverseDirection) {
            // Mark the position where the head would have been
            val head = state.snake.first()
            val deathPos = when (newDirection) {
                Direction.UP -> Position(head.x, (head.y - 1 + gridSize) % gridSize)
                Direction.DOWN -> Position(head.x, (head.y + 1) % gridSize)
                Direction.LEFT -> Position((head.x - 1 + gridSize) % gridSize, head.y)
                Direction.RIGHT -> Position((head.x + 1) % gridSize, head.y)
            }
            return state.copy(isGameOver = true, deathPosition = deathPos)
        }

        val validDirection = newDirection

        // Calculate new head position
        val head = state.snake.first()
        val newHead = when (validDirection) {
            Direction.UP -> Position(head.x, (head.y - 1 + gridSize) % gridSize)
            Direction.DOWN -> Position(head.x, (head.y + 1) % gridSize)
            Direction.LEFT -> Position((head.x - 1 + gridSize) % gridSize, head.y)
            Direction.RIGHT -> Position((head.x + 1) % gridSize, head.y)
        }

        // Check collision with self
        if (state.snake.contains(newHead)) {
            return state.copy(isGameOver = true, deathPosition = newHead)
        }

        // Check if food is eaten
        val (newSnake, newFood, newScore) = if (newHead == state.food) {
            // Snake grows, generate new food, increment score
            Triple(
                listOf(newHead) + state.snake,
                generateFood(listOf(newHead) + state.snake),
                state.score + 1
            )
        } else {
            // Snake moves normally
            Triple(
                listOf(newHead) + state.snake.dropLast(1),
                state.food,
                state.score
            )
        }

        return state.copy(
            snake = newSnake,
            food = newFood,
            direction = validDirection,
            score = newScore
        )
    }

    private fun generateFood(snake: List<Position>): Position {
        var food: Position
        do {
            food = Position(
                Random.nextInt(gridSize),
                Random.nextInt(gridSize)
            )
        } while (snake.contains(food))
        return food
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SnakeGame() {
    val gameLogic = remember { SnakeGameLogic() }
    var gameState by remember { mutableStateOf(gameLogic.createInitialState()) }
    var currentDirection by remember { mutableStateOf(Direction.RIGHT) }
    var isPlaying by remember { mutableStateOf(false) }
    var showModeSelection by remember { mutableStateOf(true) }
    var gameMode by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    // Game loop with dynamic speed
    LaunchedEffect(isPlaying, gameState.score) {
        if (isPlaying && !gameState.isGameOver) {
            while (isActive && !gameState.isGameOver) {
                // Speed increases every 16 foods eaten
                val currentSpeed = when (gameState.score) {
                    in 0..15 -> Speed.SLOW
                    in 16..31 -> Speed.MEDIUM
                    in 32..47 -> Speed.FAST
                    in 48..63 -> Speed.VERY_FAST
                    else -> Speed.EXTREME
                }
                delay(currentSpeed.delayMs)
                gameState = gameLogic.updateGame(gameState, currentDirection)
            }
            if (gameState.isGameOver) {
                isPlaying = false
            }
        }
    }

    // Request focus when component mounts
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
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
                            } else false
                        }
                        Key.DirectionDown, Key.S -> {
                            if (isPlaying) {
                                currentDirection = Direction.DOWN
                                true
                            } else false
                        }
                        Key.DirectionLeft, Key.A -> {
                            if (isPlaying) {
                                currentDirection = Direction.LEFT
                                true
                            } else false
                        }
                        Key.DirectionRight, Key.D -> {
                            if (isPlaying) {
                                currentDirection = Direction.RIGHT
                                true
                            } else false
                        }
                        Key.Spacebar -> {
                            if (!isPlaying && !gameState.isGameOver) {
                                // Unpause
                                isPlaying = true
                            } else if (isPlaying && gameState.pauses > 0) {
                                // Pause (only if pauses remaining)
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
        // Score and pauses display
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
            Text(
                text = "Pauses: ${gameState.pauses}",
                style = MaterialTheme.typography.headlineMedium,
                color = if (gameState.pauses > 0) Color(0xFF4CAF50) else Color.Gray,
                fontFamily = FontFamily.Monospace
            )
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
            Text(
                text = "Press SPACE to pause/unpause (1 pause per game).",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
