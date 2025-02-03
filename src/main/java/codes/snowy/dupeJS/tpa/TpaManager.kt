package codes.snowy.dupeJS.tpa

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.Logger
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID

data class TeleportRequest(val requesterUUID: UUID, val targetUUID: UUID)

class TpaManager {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)
    private val teleportManager = TeleportManager(DupeJS.getInstance())
    private val dbConnection: Connection

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/tpa.db")
        createTable()
    }

    private fun createTable() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS teleport_requests (requesterUUID TEXT, targetUUID TEXT)")
        statement.close()
    }

    private fun saveTeleportRequest(requesterUUID: UUID, targetUUID: UUID) {
        val statement: PreparedStatement = dbConnection.prepareStatement("INSERT INTO teleport_requests (requesterUUID, targetUUID) VALUES (?, ?)")
        statement.setString(1, requesterUUID.toString())
        statement.setString(2, targetUUID.toString())
        statement.executeUpdate()
        statement.close()
    }

    private fun deleteTeleportRequest(requesterUUID: UUID, targetUUID: UUID) {
        val statement: PreparedStatement = dbConnection.prepareStatement("DELETE FROM teleport_requests WHERE requesterUUID = ? AND targetUUID = ?")
        statement.setString(1, requesterUUID.toString())
        statement.setString(2, targetUUID.toString())
        statement.executeUpdate()
        statement.close()
    }

    private fun getTeleportRequest(requesterUUID: UUID, targetUUID: UUID): TeleportRequest? {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT * FROM teleport_requests WHERE requesterUUID = ? AND targetUUID = ?")
        statement.setString(1, requesterUUID.toString())
        statement.setString(2, targetUUID.toString())
        val resultSet: ResultSet = statement.executeQuery()
        val request = if (resultSet.next()) TeleportRequest(UUID.fromString(resultSet.getString("requesterUUID")), UUID.fromString(resultSet.getString("targetUUID"))) else null
        resultSet.close()
        statement.close()
        return request
    }

    private fun getAllTeleportRequests(): List<TeleportRequest> {
        val statement = dbConnection.createStatement()
        val resultSet: ResultSet = statement.executeQuery("SELECT * FROM teleport_requests")
        val requests = mutableListOf<TeleportRequest>()
        while (resultSet.next()) {
            requests.add(TeleportRequest(UUID.fromString(resultSet.getString("requesterUUID")), UUID.fromString(resultSet.getString("targetUUID"))))
        }
        resultSet.close()
        statement.close()
        return requests
    }

    fun requestTeleport(sender: Player, acceptee: Player) {
        if (sender == acceptee) {
            sender.sendMessage("&cYou cannot teleport to yourself.".translate())
            return
        }

        if (getTeleportRequest(sender.uniqueId, acceptee.uniqueId) != null) {
            sender.sendMessage("&cYou have already sent a teleport request to this player.".translate())
            return
        }

        saveTeleportRequest(sender.uniqueId, acceptee.uniqueId)
        Logger.log("Teleport request added: ${sender.name} -> ${acceptee.name}", "debug")
        sender.sendMessage(language.getMessages("tpa.send-request").replace("%target%", acceptee.name).translate())
        acceptee.sendMessage(language.getMessages("tpa.receive-request").replace("%target%", sender.name).translate())
    }

    fun acceptTeleport(acceptee: Player, sender: Player) {
        if (acceptee == sender) {
            acceptee.sendMessage("&cYou cannot accept a teleport request from yourself.".translate())
            return
        }
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log("Attempting to accept teleport request from ${sender.name} to ${acceptee.name}", "debug")
        }
        val request = getTeleportRequest(sender.uniqueId, acceptee.uniqueId)
        if (request != null) {
            if (config.getBoolean("dupe.debug", false)) {
                Logger.log("Teleport request found: ${sender.name} -> ${acceptee.name}", "debug")
            }
            teleportManager.tpaTeleport(acceptee, sender.location, sender)
            deleteTeleportRequest(sender.uniqueId, acceptee.uniqueId)
            Logger.log("Teleport request removed: ${sender.name} -> ${acceptee.name}", "debug")
        } else {
            if (config.getBoolean("dupe.debug", false)) {
                getAllTeleportRequests().forEach {
                    Logger.log("Teleport request: ${it.requesterUUID} -> ${it.targetUUID}", "debug")
                }
                Logger.log("Teleport request not found for ${sender.name} -> ${acceptee.name}", "debug")
            }
            acceptee.sendMessage(language.getMessages("tpa.teleport-notfound").replace("%target%", sender.name).translate())
        }
    }
}