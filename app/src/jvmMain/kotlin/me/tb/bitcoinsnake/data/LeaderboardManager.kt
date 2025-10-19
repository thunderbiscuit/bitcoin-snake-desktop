package me.tb.bitcoinsnake.data

import kotlinx.serialization.json.Json
import me.tb.bitcoinsnake.domain.Leaderboard
import me.tb.bitcoinsnake.domain.LeaderboardEntry
import java.io.File

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
