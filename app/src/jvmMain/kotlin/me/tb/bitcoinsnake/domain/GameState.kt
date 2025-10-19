package me.tb.bitcoinsnake.domain

data class GameState(
    val snake: List<Position>,
    val food: Position,
    val direction: Direction,
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val pauses: Int = 1,
    val lives: Int = 0,
    val deathPosition: Position? = null
)
