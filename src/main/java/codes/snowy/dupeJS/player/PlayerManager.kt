package codes.snowy.dupeJS.player

import codes.snowy.dupeJS.DupeJS
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID
import java.util.concurrent.TimeUnit

class PlayerManager {
    private val dbConnection: Connection

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/players.db")
        createTables()
    }

    private fun createTables() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS player_stats (
                uuid TEXT PRIMARY KEY,
                kills INT DEFAULT 0,
                deaths INT DEFAULT 0,
                first_join BIGINT,
                last_join BIGINT,
                playtime BIGINT DEFAULT 0
            )
        """)
        statement.close()
    }

    fun initializePlayer(player: Player) {
        val statement = dbConnection.prepareStatement("""
            INSERT OR IGNORE INTO player_stats (uuid, first_join, last_join)
            VALUES (?, ?, ?)
        """)
        val currentTime = System.currentTimeMillis()
        statement.setString(1, player.uniqueId.toString())
        statement.setLong(2, currentTime)
        statement.setLong(3, currentTime)
        statement.executeUpdate()
        statement.close()
    }

    fun addKill(uuid: UUID) {
        val statement = dbConnection.prepareStatement("""
            UPDATE player_stats 
            SET kills = kills + 1 
            WHERE uuid = ?
        """)
        statement.setString(1, uuid.toString())
        statement.executeUpdate()
        statement.close()
    }

    fun addDeath(uuid: UUID) {
        val statement = dbConnection.prepareStatement("""
            UPDATE player_stats 
            SET deaths = deaths + 1 
            WHERE uuid = ?
        """)
        statement.setString(1, uuid.toString())
        statement.executeUpdate()
        statement.close()
    }

    fun getKills(uuid: UUID): Int {
        val statement = dbConnection.prepareStatement("SELECT kills FROM player_stats WHERE uuid = ?")
        statement.setString(1, uuid.toString())
        val resultSet = statement.executeQuery()
        val kills = if (resultSet.next()) resultSet.getInt("kills") else 0
        resultSet.close()
        statement.close()
        return kills
    }

    fun getDeaths(uuid: UUID): Int {
        val statement = dbConnection.prepareStatement("SELECT deaths FROM player_stats WHERE uuid = ?")
        statement.setString(1, uuid.toString())
        val resultSet = statement.executeQuery()
        val deaths = if (resultSet.next()) resultSet.getInt("deaths") else 0
        resultSet.close()
        statement.close()
        return deaths
    }

    fun getKDR(uuid: UUID): Double {
        val kills = getKills(uuid)
        val deaths = getDeaths(uuid)
        return if (deaths == 0) kills.toDouble() else kills.toDouble() / deaths
    }

    fun updateLastJoin(uuid: UUID) {
        val statement = dbConnection.prepareStatement("""
            UPDATE player_stats 
            SET last_join = ? 
            WHERE uuid = ?
        """)
        statement.setLong(1, System.currentTimeMillis())
        statement.setString(2, uuid.toString())
        statement.executeUpdate()
        statement.close()
    }

    fun updatePlaytime(uuid: UUID, sessionTime: Long) {
        val statement = dbConnection.prepareStatement("""
            UPDATE player_stats 
            SET playtime = playtime + ? 
            WHERE uuid = ?
        """)
        statement.setLong(1, sessionTime)
        statement.setString(2, uuid.toString())
        statement.executeUpdate()
        statement.close()
    }

    fun getPlaytime(uuid: UUID): Long {
        val statement = dbConnection.prepareStatement("SELECT playtime FROM player_stats WHERE uuid = ?")
        statement.setString(1, uuid.toString())
        val resultSet = statement.executeQuery()
        val playtime = if (resultSet.next()) resultSet.getLong("playtime") else 0
        resultSet.close()
        statement.close()
        return playtime
    }

    fun formatPlaytime(millis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        val hours = TimeUnit.MILLISECONDS.toHours(millis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return "${days}d ${hours}h ${minutes}m"
    }

    fun getFirstJoinDate(uuid: UUID): Long {
        val statement = dbConnection.prepareStatement("SELECT first_join FROM player_stats WHERE uuid = ?")
        statement.setString(1, uuid.toString())
        val resultSet = statement.executeQuery()
        val firstJoin = if (resultSet.next()) resultSet.getLong("first_join") else 0
        resultSet.close()
        statement.close()
        return firstJoin
    }
} 