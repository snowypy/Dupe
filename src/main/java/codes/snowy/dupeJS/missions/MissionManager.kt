package codes.snowy.dupeJS.missions

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

class MissionManager(private val db: MissionDatabase) {

    private val dbConnection: Connection = DriverManager.getConnection("jdbc:sqlite:Databases/missions.db")

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
                INSERT OR REPLACE INTO player_missions (player_uuid, mission_uuid, mission_type, progress, target, last_updated, claimed)
                VALUES (?, ?, ?, 0, ?, ?, 0)
            """)
            statement.setString(1, playerUUID.toString())
            statement.setString(2, missionUUID)
            statement.setString(3, mission)
            statement.setInt(4, extractTarget(mission))
            statement.setLong(5, System.currentTimeMillis())
            statement.executeUpdate()
            statement.close()
        }
    }

    fun updateMissionProgress(player: Player, missionType: String, progress: Int) {
        val playerUUID = player.uniqueId
        Bukkit.getLogger().info("Updating mission progress for player: ${player.name}, missionType: $missionType, progress: $progress")

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
        return mission.split(" ")[1].toInt()
    }

    fun getPlayerMissions(player: Player): List<Mission> {
        val playerUUID = player.uniqueId
        val missions = mutableListOf<Mission>()
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            SELECT mission_uuid, mission_type, progress, target, last_updated, claimed FROM player_missions
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
                frequency = determineFrequency(resultSet.getString("mission_type")),
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
            assignMissions(player, 2, "daily")
            assignMissions(player, 3, "weekly")
        }
    }

    fun assignMissions(player: Player, count: Int, frequency: String) {
        val playerUUID = player.uniqueId
        val missions = (1..count).map {
            when (frequency) {
                "daily" -> Pair("Kill ${Random.nextInt(1, 11)} players", "Kill Players")
                "weekly" -> Pair("Break ${Random.nextInt(100, 251)} blocks", "Break Blocks")
                else -> throw IllegalArgumentException("Unknown frequency: $frequency")
            }
        }

        missions.forEach { (mission, missionType) ->
            val missionUUID = UUID.randomUUID().toString()
            val statement: PreparedStatement = dbConnection.prepareStatement("""
                INSERT OR REPLACE INTO player_missions (player_uuid, mission_uuid, mission_type, progress, target, last_updated, claimed)
                VALUES (?, ?, ?, 0, ?, ?, 0)
            """)
            statement.setString(1, playerUUID.toString())
            statement.setString(2, missionUUID)
            statement.setString(3, missionType)
            statement.setInt(4, extractTarget(mission))
            statement.setLong(5, System.currentTimeMillis())
            statement.executeUpdate()
            statement.close()
        }
    }

    private fun determineFrequency(missionType: String): String {
        return when {
            missionType.contains("Kill") -> "daily"
            missionType.contains("Break") -> "weekly"
            else -> "daily"
        }
    }

    fun clearPlayerMissions(player: Player) {
        val playerUUID = player.uniqueId
        val statement: PreparedStatement = dbConnection.prepareStatement("""
            DELETE FROM player_missions WHERE player_uuid = ?
        """)
        statement.setString(1, playerUUID.toString())
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