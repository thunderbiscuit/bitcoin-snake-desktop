package me.tb.bitcoinsnake.domain

import kotlinx.serialization.Serializable

@Serializable
data class Leaderboard(
    val entries: List<LeaderboardEntry> = emptyList()
) {
    fun addEntry(entry: LeaderboardEntry): Leaderboard {
        val newEntries = (entries + entry)
            .sortedByDescending { it.score }
            .take(10)
        return copy(entries = newEntries)
    }

    fun isTopScore(score: Int): Boolean {
        return entries.size < 10 || score > (entries.minOfOrNull { it.score } ?: 0)
    }
}
