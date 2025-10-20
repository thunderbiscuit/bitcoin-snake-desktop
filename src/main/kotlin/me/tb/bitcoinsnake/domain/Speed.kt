package me.tb.bitcoinsnake.domain

enum class Speed(val delayMs: Long) {
    SLOW(170),
    MEDIUM(140),
    FAST(110),
    VERY_FAST(90),
    EXTREME(70)
}
