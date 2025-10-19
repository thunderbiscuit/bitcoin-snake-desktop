package me.tb.bitcoinsnake.domain

import kotlin.random.Random

class SnakeGameLogic(
    private val gridSize: Int = GRID_SIZE
) {
    fun createInitialState(pauses: Int = 1, lives: Int = 0): GameState {
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
            pauses = pauses,
            lives = lives
        )
    }

    fun respawnSnake(state: GameState): GameState {
        val initialSnake = listOf(
            Position(8, 12),
            Position(7, 12),
            Position(6, 12)
        )
        return state.copy(
            snake = initialSnake,
            direction = Direction.RIGHT,
            food = generateFood(initialSnake),
            deathPosition = null,
            lives = state.lives - 1
        )
    }

    fun updateGame(state: GameState, newDirection: Direction): GameState {
        if (state.isGameOver) return state

        // Check if player is trying to reverse direction - if so, game over or lose life
        val isReverseDirection = when (state.direction) {
            Direction.UP if newDirection == Direction.DOWN    -> true
            Direction.DOWN if newDirection == Direction.UP    -> true
            Direction.LEFT if newDirection == Direction.RIGHT -> true
            Direction.RIGHT if newDirection == Direction.LEFT -> true
            else -> false
        }

        if (isReverseDirection) {
            // Mark the position where the head would have been
            val head = state.snake.first()
            val deathPos = when (newDirection) {
                Direction.UP -> Position(head.x, head.y - 1)
                Direction.DOWN -> Position(head.x, head.y + 1)
                Direction.LEFT -> Position(head.x - 1, head.y)
                Direction.RIGHT -> Position(head.x + 1, head.y)
            }
            return if (state.lives > 0) {
                state.copy(deathPosition = deathPos)
            } else {
                state.copy(isGameOver = true, deathPosition = deathPos)
            }
        }

        val validDirection = newDirection

        // Calculate new head position
        val head = state.snake.first()
        val newHead = when (validDirection) {
            Direction.UP -> Position(head.x, head.y - 1)
            Direction.DOWN -> Position(head.x, head.y + 1)
            Direction.LEFT -> Position(head.x - 1, head.y)
            Direction.RIGHT -> Position(head.x + 1, head.y)
        }

        // Check collision with wall
        if (newHead.x < 0 || newHead.x >= gridSize || newHead.y < 0 || newHead.y >= gridSize) {
            return if (state.lives > 0) {
                state.copy(deathPosition = newHead)
            } else {
                state.copy(isGameOver = true, deathPosition = newHead)
            }
        }

        // Check collision with self
        if (state.snake.contains(newHead)) {
            return if (state.lives > 0) {
                state.copy(deathPosition = newHead)
            } else {
                state.copy(isGameOver = true, deathPosition = newHead)
            }
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
