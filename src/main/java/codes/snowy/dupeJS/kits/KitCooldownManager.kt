package codes.snowy.dupeJS.kits

import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID
import org.bukkit.entity.Player

class KitCooldownManager {
    private val dbConnection: Connection

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/kits.db")
        createTable()
        createTimesClaimedTable()
    }

    private fun createTable() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS kit_cooldowns (
                player_uuid TEXT,
                kit_name TEXT,
                last_used BIGINT,
                PRIMARY KEY (player_uuid, kit_name)
            )
        """)
        statement.close()
    }

    private fun createTimesClaimedTable() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS kit_claims (
                player_uuid TEXT,
                kit_name TEXT,
                times_claimed INT DEFAULT 0,
                PRIMARY KEY (player_uuid, kit_name)
            )
        """)
        statement.close()
    }

    fun setKitCooldown(player: UUID, kitName: String) {
        val statement = dbConnection.prepareStatement("""
            INSERT OR REPLACE INTO kit_cooldowns (player_uuid, kit_name, last_used)
            VALUES (?, ?, ?)
        """)
        statement.setString(1, player.toString())
        statement.setString(2, kitName.lowercase())
        statement.setLong(3, System.currentTimeMillis())
        statement.executeUpdate()
        statement.close()
    }

    fun canUseKit(player: UUID, kitName: String): Boolean {
        val statement = dbConnection.prepareStatement("""
            SELECT last_used FROM kit_cooldowns
            WHERE player_uuid = ? AND kit_name = ?
        """)
        statement.setString(1, player.toString())
        statement.setString(2, kitName.lowercase())
        
        val result = statement.executeQuery()
        val canUse = if (result.next()) {
            val lastUsed = result.getLong("last_used")
            System.currentTimeMillis() - lastUsed >= 24 * 60 * 60 * 1000
        } else {
            true
        }
        
        result.close()
        statement.close()
        return canUse
    }

    fun getRemainingCooldown(player: UUID, kitName: String): Long {
        val statement = dbConnection.prepareStatement("""
            SELECT last_used FROM kit_cooldowns
            WHERE player_uuid = ? AND kit_name = ?
        """)
        statement.setString(1, player.toString())
        statement.setString(2, kitName.lowercase())
        
        val result = statement.executeQuery()
        val remaining = if (result.next()) {
            val lastUsed = result.getLong("last_used")
            val timePassed = System.currentTimeMillis() - lastUsed
            val cooldown = 24 * 60 * 60 * 1000
            (cooldown - timePassed).coerceAtLeast(0)
        } else {
            0L
        }
        
        result.close()
        statement.close()
        return remaining
    }

    fun incrementTimesClaimed(player: UUID, kitName: String) {
        val statement = dbConnection.prepareStatement("""
            INSERT INTO kit_claims (player_uuid, kit_name, times_claimed)
            VALUES (?, ?, 1)
            ON CONFLICT(player_uuid, kit_name) DO UPDATE SET times_claimed = times_claimed + 1
        """)
        statement.setString(1, player.toString())
        statement.setString(2, kitName.lowercase())
        statement.executeUpdate()
        statement.close()
    }

    fun getTimesClaimed(player: UUID, kitName: String): Int {
        val statement = dbConnection.prepareStatement("""
            SELECT times_claimed FROM kit_claims
            WHERE player_uuid = ? AND kit_name = ?
        """)
        statement.setString(1, player.toString())
        statement.setString(2, kitName.lowercase())
        
        val result = statement.executeQuery()
        val timesClaimed = if (result.next()) result.getInt("times_claimed") else 0
        result.close()
        statement.close()
        return timesClaimed
    }
} 