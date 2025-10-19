package me.tb.bitcoinsnake

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class LeaderboardEntry(
    val playerName: String,
    val score: Int,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)

@Serializable
data class Leaderboard(
    val entries: List<LeaderboardEntry> = emptyList()
) {
    fun addEntry(entry: LeaderboardEntry): Leaderboard {
        val newEntries = (entries + entry)
            .sortedByDescending { it.score }
            .take(10) // Keep only top 10
        return copy(entries = newEntries)
    }

    fun isTopScore(score: Int): Boolean {
        return entries.size < 10 || score > (entries.minOfOrNull { it.score } ?: 0)
    }
}

class LeaderboardManager(
    private val storageDir: File = File(System.getProperty("user.home"), ".bitcoin-snake")
) {
    private val leaderboardFile = File(storageDir, "leaderboard.json")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        // Create directory if it doesn't exist
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
    }

    fun loadLeaderboard(): Leaderboard {
        return try {
            if (leaderboardFile.exists()) {
                val jsonString = leaderboardFile.readText()
                json.decodeFromString<Leaderboard>(jsonString)
            } else {
                Leaderboard()
            }
        } catch (e: Exception) {
            println("Error loading leaderboard: ${e.message}")
            Leaderboard()
        }
    }

    fun saveLeaderboard(leaderboard: Leaderboard) {
        try {
            val jsonString = json.encodeToString(leaderboard)
            leaderboardFile.writeText(jsonString)
        } catch (e: Exception) {
            println("Error saving leaderboard: ${e.message}")
        }
    }

    fun addScore(playerName: String, score: Int): Leaderboard {
        val leaderboard = loadLeaderboard()
        val newLeaderboard = leaderboard.addEntry(
            LeaderboardEntry(
                playerName = playerName,
                score = score
            )
        )
        saveLeaderboard(newLeaderboard)
        return newLeaderboard
    }
}
