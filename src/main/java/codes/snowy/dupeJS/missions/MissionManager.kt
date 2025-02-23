package codes.snowy.dupeJS.missions

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID
import kotlin.random.Random
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.nio.file.Files
import java.util.concurrent.TimeUnit

class MissionManager(private val db: MissionDatabase) {

    private val dbConnection: Connection = DriverManager.getConnection("jdbc:sqlite:Databases/missions.db")
    private val cooldownFile = File("plugins/DupeJS/missions/cooldown.json")
    private val gson = Gson()

    init {
        loadCooldowns()
        startResetScheduler()
    }

    var lastDailyReset: Long = 0
    var lastWeeklyReset: Long = 0

    private fun loadCooldowns() {
        if (!cooldownFile.exists()) {
            cooldownFile.createNewFile()
            val json = JsonObject()
            json.addProperty("lastDailyReset", System.currentTimeMillis())
            json.addProperty("lastWeeklyReset", System.currentTimeMillis())
            Files.write(cooldownFile.toPath(), gson.toJson(json).toByteArray())
        } else {
            val json = gson.fromJson(cooldownFile.readText(), JsonObject::class.java)
            lastDailyReset = json.get("lastDailyReset").asLong
            lastWeeklyReset = json.get("lastWeeklyReset").asLong
        }
    }

    private fun saveCooldowns() {
        val json = JsonObject()
        json.addProperty("lastDailyReset", lastDailyReset)
        json.addProperty("lastWeeklyReset", lastWeeklyReset)
        Files.write(cooldownFile.toPath(), gson.toJson(json).toByteArray())
    }

    private fun startResetScheduler() {
        object : BukkitRunnable() {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
                val sevenDaysInMillis = TimeUnit.DAYS.toMillis(7)

                if (currentTime - lastDailyReset >= oneDayInMillis) {

                    val daysToSkip = ((currentTime - lastDailyReset) / oneDayInMillis).toInt()
                    lastDailyReset += (oneDayInMillis * daysToSkip)
                    resetDailyMissions()
                    saveCooldowns()
                }

                if (currentTime - lastWeeklyReset >= sevenDaysInMillis) {

                    val weeksToSkip = ((currentTime - lastWeeklyReset) / sevenDaysInMillis).toInt()
                    lastWeeklyReset += (sevenDaysInMillis * weeksToSkip)
                    resetWeeklyMissions()
                    saveCooldowns()
                }
            }
        }.runTaskTimer(DupeJS.getInstance(), 20L, 1200L)
    }

    fun resetDailyMissions() {
        val playerUUIDs = getAllPlayerUUIDs()
        val onlinePlayers = Bukkit.getOnlinePlayers().map { it.uniqueId }.toSet()
        
        playerUUIDs.forEach { playerUUID ->
            val player = Bukkit.getPlayer(playerUUID)
            if (player != null) {
                clearPlayerMissions(player, "daily")
                assignMissions(player, 2, "daily")
            }
        }
        Bukkit.broadcastMessage("&#feda36&lMISSIONS &8| &fYour daily missions have been reset!".translate())
    }

    fun resetWeeklyMissions() {
        val playerUUIDs = getAllPlayerUUIDs()
        val onlinePlayers = Bukkit.getOnlinePlayers().map { it.uniqueId }.toSet()
        
        playerUUIDs.forEach { playerUUID ->
            val player = Bukkit.getPlayer(playerUUID)
            if (player != null) {
                clearPlayerMissions(player, "weekly")
                assignMissions(player, 3, "weekly")
            }
        }
        Bukkit.broadcastMessage("&#feda36&lMISSIONS &8| &fYour weekly missions have been reset!".translate())
    }

    fun getAllPlayerUUIDs(): List<UUID> {
        val playerUUIDs = mutableListOf<UUID>()
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT player_uuid FROM player_missions")
        val resultSet: ResultSet = statement.executeQuery()
        while (resultSet.next()) {
            playerUUIDs.add(UUID.fromString(resultSet.getString("player_uuid")))
        }
        resultSet.close()
        statement.close()
        return playerUUIDs
    }

    fun assignMissions(player: Player) {
        val playerUUID = player.uniqueId
        val missions = listOf(
            "Kill ${Random.nextInt(1, 11)} players",
            "Kill ${Random.nextInt(2, 6)} ${getRandomMob()}",
            "Break ${Random.nextInt(100, 251)} blocks",
            "Play for ${Random.nextInt(2, 11)} hours"
        )

        missions.forEach { mission ->
            val missionUUID = UUID.randomUUID().toString()
            val statement: PreparedStatement = dbConnection.prepareStatement("""
                INSERT OR REPLACE INTO player_missions (player_uuid, mission_uuid, mission_type, progress, target, last_updated, claimed, frequency)
                VALUES (?, ?, ?, 0, ?, ?, 0, ?)
            """)
            statement.setString(1, playerUUID.toString())
            statement.setString(2, missionUUID)
            statement.setString(3, mission)
            statement.setInt(4, extractTarget(mission))
            statement.setLong(5, System.currentTimeMillis())
            statement.setString(6, "daily")
            statement.executeUpdate()
            statement.close()
        }
    }

    fun assignMissions(player: Player, count: Int, frequency: String) {
        val playerUUID = player.uniqueId
        val missions = when (frequency) {
            "daily" -> (1..count).map {
                val dailyMissions = listOf(
                    Pair("Kill ${Random.nextInt(1, 11)} players", "Kill Players"),
                    Pair("Kill ${Random.nextInt(2, 6)} ${getRandomMob()}", "Kill Mobs"),
                    Pair("Break ${Random.nextInt(100, 251)} blocks", "Break Blocks"),
                    Pair("Play for ${Random.nextInt(2, 11)} hours", "Play for Hours")
                )
                dailyMissions.random()
            }
            "weekly" -> (1..count).map {
                val weeklyMissions = listOf(
                    Pair("Break ${Random.nextInt(251, 900)} blocks", "Break Blocks"),
                    Pair("Kill ${Random.nextInt(6, 23)} ${getRandomMob()}", "Kill Mobs"),
                    Pair("Kill ${Random.nextInt(10, 25)} players", "Kill Players")
                )
                weeklyMissions.random()
            }
            else -> throw IllegalArgumentException("Unknown frequency: $frequency")
        }

        missions.forEach { (missionDescription, baseMissionType) ->
            val missionUUID = UUID.randomUUID().toString()
            val target = if (baseMissionType == "Play for Hours") {
                extractTarget(missionDescription) * 60
            } else {
                extractTarget(missionDescription)
            }
            val statement: PreparedStatement = dbConnection.prepareStatement("""
                INSERT OR REPLACE INTO player_missions (player_uuid, mission_uuid, mission_type, progress, target, last_updated, claimed, frequency)
                VALUES (?, ?, ?, 0, ?, ?, 0, ?)
            """)
            statement.setString(1, playerUUID.toString())
            statement.setString(2, missionUUID)
            statement.setString(3, baseMissionType)
            statement.setInt(4, target)
            statement.setLong(5, System.currentTimeMillis())
            statement.setString(6, frequency)
            statement.executeUpdate()
            statement.close()
        }
    }

    fun updateMissionProgress(player: Player, missionType: String, progress: Int) {
        val playerUUID = player.uniqueId

        val selectStatement: PreparedStatement = dbConnection.prepareStatement("""
            SELECT mission_uuid, progress, target FROM player_missions
            WHERE player_uuid = ? AND mission_type = ?
        """)
        selectStatement.setString(1, playerUUID.toString())
        selectStatement.setString(2, missionType)
        val resultSet: ResultSet = selectStatement.executeQuery()

        while (resultSet.next()) {
            val missionUUID = resultSet.getString("mission_uuid")
            val currentProgress = resultSet.getInt("progress")
            val target = resultSet.getInt("target")
            val newProgress = (currentProgress + progress).coerceAtMost(target)

            val updateStatement: PreparedStatement = dbConnection.prepareStatement("""
                UPDATE player_missions
                SET progress = ?
                WHERE player_uuid = ? AND mission_uuid = ?
            """)
            updateStatement.setInt(1, newProgress)
            updateStatement.setString(2, playerUUID.toString())
            updateStatement.setString(3, missionUUID)
            updateStatement.executeUpdate()
            updateStatement.close()

            if (newProgress >= target && currentProgress < target) {
                notifyMissionCompletion(player, missionType)
            }
        }

        resultSet.close()
        selectStatement.close()
    }

    private fun notifyMissionCompletion(player: Player, missionType: String) {
        player.sendMessage("&#feda36&lMISSIONS &8| &fYou finished your &#feda36&n${missionType}&f mission!".translate())
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        player.sendTitle("&#feda36&lMISSION COMPLETE".translate(), "&fYou finished your &#feda36&n${missionType}&f mission!".translate(), 10, 70, 20)
    }

    fun checkMissionCompletion(player: Player): Boolean {
        val playerUUID = player.uniqueId
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            SELECT progress, target, claimed FROM player_missions
            WHERE player_uuid = ? AND claimed = 0
        """)
        statement.setString(1, playerUUID.toString())
        val resultSet: ResultSet = statement.executeQuery()
        var completed = false
        while (resultSet.next()) {
            if (resultSet.getInt("progress") >= resultSet.getInt("target")) {
                completed = true
                break
            }
        }
        resultSet.close()
        statement.close()
        return completed
    }

    fun cleanupInactivePlayers() {
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            DELETE FROM player_missions
            WHERE player_uuid IN (
                SELECT player_uuid FROM player_activity
                WHERE join_count < 5 AND last_join < ?
            )
        """)
        statement.setLong(1, System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
        statement.executeUpdate()
        statement.close()
    }

    private fun getRandomMob(): String {
        val mobs = listOf("pig", "sheep", "cow", "chicken", "ender dragon", "wither")
        return mobs.random()
    }

    private fun extractTarget(mission: String): Int {
        return try {
            val parts = mission.split(" ")
            if (parts.size >= 3 && parts[1] == "for") {
                parts[2].toInt()
            } else {
                mission.split(" ")[1].toInt()
            }
        } catch (e: Exception) {
            Bukkit.getLogger().warning("Failed to extract target from mission: $mission. Defaulting to 0.")
            0
        }
    }

    fun getPlayerMissions(player: Player): List<Mission> {
        val playerUUID = player.uniqueId
        val missions = mutableListOf<Mission>()
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            SELECT mission_uuid, mission_type, progress, target, last_updated, claimed, frequency FROM player_missions
            WHERE player_uuid = ?
        """)
        statement.setString(1, playerUUID.toString())
        val resultSet: ResultSet = statement.executeQuery()
        while (resultSet.next()) {
            val mission = Mission(
                missionUUID = UUID.fromString(resultSet.getString("mission_uuid")),
                type = resultSet.getString("mission_type"),
                missionType = resultSet.getString("mission_type"),
                progress = resultSet.getInt("progress"),
                target = resultSet.getInt("target"),
                lastUpdated = resultSet.getLong("last_updated"),
                frequency = resultSet.getString("frequency"),
                claimed = resultSet.getBoolean("claimed")
            )
            missions.add(mission)
        }
        resultSet.close()
        statement.close()
        return missions
    }

    fun getCompletedMissions(player: Player): Int {
        val playerUUID = player.uniqueId
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            SELECT COUNT(*) AS completed_count FROM player_missions
            WHERE player_uuid = ? AND progress >= target
        """)
        statement.setString(1, playerUUID.toString())
        val resultSet: ResultSet = statement.executeQuery()
        val completedCount = if (resultSet.next()) resultSet.getInt("completed_count") else 0
        resultSet.close()
        statement.close()
        return completedCount
    }

    fun ensureMissionsAssigned(player: Player) {
        val missions = getPlayerMissions(player)
        if (missions.isEmpty()) {
            assignMissions(player, 3, "daily")
            assignMissions(player, 2, "weekly")
        }
    }

    fun clearPlayerMissions(player: Player, frequency: String? = null) {
        val playerUUID = player.uniqueId
        val sql = if (frequency != null) {
            "DELETE FROM player_missions WHERE player_uuid = ? AND frequency = ?"
        } else {
            "DELETE FROM player_missions WHERE player_uuid = ?"
        }
        
        val statement: PreparedStatement = dbConnection.prepareStatement(sql)
        statement.setString(1, playerUUID.toString())
        if (frequency != null) {
            statement.setString(2, frequency)
        }
        statement.executeUpdate()
        statement.close()
    }

    fun markMissionsAsClaimed(player: Player) {
        val playerUUID = player.uniqueId
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            UPDATE player_missions
            SET claimed = 1
            WHERE player_uuid = ? AND progress >= target
        """)
        statement.setString(1, playerUUID.toString())
        statement.executeUpdate()
        statement.close()
    }

    fun isMissionClaimed(player: Player, missionUUID: String): Boolean {
        val playerUUID = player.uniqueId
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            SELECT claimed FROM player_missions
            WHERE player_uuid = ? AND mission_uuid = ?
        """)
        statement.setString(1, playerUUID.toString())
        statement.setString(2, missionUUID)
        val resultSet: ResultSet = statement.executeQuery()
        val claimed = resultSet.next() && resultSet.getBoolean("claimed")
        resultSet.close()
        statement.close()
        return claimed
    }

    fun markMissionAsClaimed(player: Player, missionUUID: String) {
        val playerUUID = player.uniqueId
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            UPDATE player_missions
            SET claimed = 1
            WHERE player_uuid = ? AND mission_uuid = ?
        """)
        statement.setString(1, playerUUID.toString())
        statement.setString(2, missionUUID)
        statement.executeUpdate()
        statement.close()
    }
}