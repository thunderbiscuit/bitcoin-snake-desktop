package me.tb.bitcoinsnake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

// Data classes for game state
data class Position(val x: Int, val y: Int)

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class GameState(
    val snake: List<Position>,
    val food: Position,
    val direction: Direction,
    val isGameOver: Boolean = false,
    val score: Int = 0
)

// Game constants
const val GRID_SIZE = 24
const val CELL_SIZE = 25f

class SnakeGameLogic(
    private val gridSize: Int = GRID_SIZE
) {
    fun createInitialState(): GameState {
        val initialSnake = listOf(
            Position(gridSize / 2, gridSize / 2),
            Position(gridSize / 2 - 1, gridSize / 2),
            Position(gridSize / 2 - 2, gridSize / 2)
        )
        return GameState(
            snake = initialSnake,
            food = generateFood(initialSnake),
            direction = Direction.RIGHT,
            score = 0
        )
    }

    fun updateGame(state: GameState, newDirection: Direction): GameState {
        if (state.isGameOver) return state

        // Prevent reversing direction
        val validDirection = when {
            state.direction == Direction.UP && newDirection == Direction.DOWN -> state.direction
            state.direction == Direction.DOWN && newDirection == Direction.UP -> state.direction
            state.direction == Direction.LEFT && newDirection == Direction.RIGHT -> state.direction
            state.direction == Direction.RIGHT && newDirection == Direction.LEFT -> state.direction
            else -> newDirection
        }

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
            return state.copy(isGameOver = true)
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
    var isPlaying by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    // Game loop
    LaunchedEffect(isPlaying) {
        if (isPlaying && !gameState.isGameOver) {
            while (isActive && !gameState.isGameOver) {
                delay(150) // Game speed
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
                if (event.type == KeyEventType.KeyDown && isPlaying) {
                    when (event.key) {
                        Key.DirectionUp, Key.W -> {
                            currentDirection = Direction.UP
                            true
                        }
                        Key.DirectionDown, Key.S -> {
                            currentDirection = Direction.DOWN
                            true
                        }
                        Key.DirectionLeft, Key.A -> {
                            currentDirection = Direction.LEFT
                            true
                        }
                        Key.DirectionRight, Key.D -> {
                            currentDirection = Direction.RIGHT
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
        // Score display
        Text(
            text = "Score: ${gameState.score}",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Game board
        Canvas(
            modifier = Modifier
                .size((GRID_SIZE * CELL_SIZE).dp)
                .background(Color(0xFF2D2D2D))
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
            drawRect(
                color = Color(0xFFF44336),
                topLeft = Offset(gameState.food.x * cellSizePx, gameState.food.y * cellSizePx),
                size = Size(cellSizePx - 2, cellSizePx - 2)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isPlaying) {
                Button(
                    onClick = {
                        if (gameState.isGameOver) {
                            gameState = gameLogic.createInitialState()
                            currentDirection = Direction.RIGHT
                        }
                        isPlaying = true
                        focusRequester.requestFocus()
                    }
                ) {
                    Text(if (gameState.isGameOver) "Restart" else "Start")
                }
            } else {
                Button(
                    onClick = {
                        isPlaying = false
                    }
                ) {
                    Text("Pause")
                }
            }
        }

        if (gameState.isGameOver) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Game Over!",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Use arrow keys or WASD to control the snake",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}
