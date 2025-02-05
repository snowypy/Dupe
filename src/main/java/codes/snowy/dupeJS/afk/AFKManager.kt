package codes.snowy.dupeJS.afk

import codes.snowy.dupeJS.utils.translate
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

class AFKManager {

    private val dbConnection: Connection

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/afk.db")
        createTables()
    }

    private fun createTables() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_shards (uuid TEXT PRIMARY KEY, shards INTEGER)")
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS afk_settings (setting TEXT PRIMARY KEY, value TEXT)")
        statement.close()
    }

    fun addShards(playerUUID: UUID, amount: Int) {
        val currentShards = getShards(playerUUID)
        val newShards = currentShards + amount
        val statement: PreparedStatement = dbConnection.prepareStatement("REPLACE INTO player_shards (uuid, shards) VALUES (?, ?)")
        val player = Bukkit.getPlayer(playerUUID)
        if (player === null) return
        player.sendMessage("&#9436fe&lSHARDS &8| &fYou have been awarded &#9436fe&n$amount&f shards for being AFK.".translate())
        player.sendMessage("&#9436fe&lSHARDS &8| &fYou now have &#9436fe&n$newShards&f shards.".translate())
        statement.setString(1, playerUUID.toString())
        statement.setInt(2, newShards)
        statement.executeUpdate()
        statement.close()
    }

    fun removeShards(playerUUID: UUID, amount: Int) {
        val currentShards = getShards(playerUUID)
        val newShards = (currentShards - amount).coerceAtLeast(0)
        val statement: PreparedStatement = dbConnection.prepareStatement("REPLACE INTO player_shards (uuid, shards) VALUES (?, ?)")
        statement.setString(1, playerUUID.toString())
        statement.setInt(2, newShards)
        statement.executeUpdate()
        statement.close()
    }

    fun getShards(playerUUID: UUID): Int {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT shards FROM player_shards WHERE uuid = ?")
        statement.setString(1, playerUUID.toString())
        val resultSet: ResultSet = statement.executeQuery()
        val shards = if (resultSet.next()) resultSet.getInt("shards") else 0
        resultSet.close()
        statement.close()
        return shards
    }

    fun setAFKTimer(minutes: Int) {
        val statement: PreparedStatement = dbConnection.prepareStatement("REPLACE INTO afk_settings (setting, value) VALUES ('timer', ?)")
        statement.setString(1, minutes.toString())
        statement.executeUpdate()
        statement.close()
    }

    fun getAFKTimer(): Int {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT value FROM afk_settings WHERE setting = 'timer'")
        val resultSet: ResultSet = statement.executeQuery()
        val timer = if (resultSet.next()) resultSet.getInt("value") else 0
        resultSet.close()
        statement.close()
        return timer
    }

    fun setAFKReward(command: String) {
        val statement: PreparedStatement = dbConnection.prepareStatement("REPLACE INTO afk_settings (setting, value) VALUES ('reward', ?)")
        statement.setString(1, command)
        statement.executeUpdate()
        statement.close()
    }

    fun getAFKReward(): String {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT value FROM afk_settings WHERE setting = 'reward'")
        val resultSet: ResultSet = statement.executeQuery()
        val reward = if (resultSet.next()) resultSet.getString("value") else ""
        resultSet.close()
        statement.close()
        return reward
    }
}