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

data class TeleportRequest(val requesterUUID: UUID, val targetUUID: UUID, val type: String)

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
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS teleport_requests (requesterUUID TEXT, targetUUID TEXT, type TEXT)")
        statement.close()
    }

    fun saveTeleport(requesterUUID: UUID, targetUUID: UUID, type: String) {
        val statement: PreparedStatement = dbConnection.prepareStatement("INSERT INTO teleport_requests (requesterUUID, targetUUID, type) VALUES (?, ?, ?)")
        statement.setString(1, requesterUUID.toString())
        statement.setString(2, targetUUID.toString())
        statement.setString(3, type)
        statement.executeUpdate()
        statement.close()
    }

    fun deleteTeleportRequest(requesterUUID: UUID, targetUUID: UUID, type: String) {
        val statement: PreparedStatement = dbConnection.prepareStatement("DELETE FROM teleport_requests WHERE requesterUUID = ? AND targetUUID = ? AND type = ?")
        statement.setString(1, requesterUUID.toString())
        statement.setString(2, targetUUID.toString())
        statement.setString(3, type)
        statement.executeUpdate()
        statement.close()
    }

    fun getTeleportRequest(requesterUUID: UUID, targetUUID: UUID, type: String): TeleportRequest? {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT * FROM teleport_requests WHERE requesterUUID = ? AND targetUUID = ? AND type = ?")
        statement.setString(1, requesterUUID.toString())
        statement.setString(2, targetUUID.toString())
        statement.setString(3, type)
        val resultSet: ResultSet = statement.executeQuery()
        val request = if (resultSet.next()) TeleportRequest(UUID.fromString(resultSet.getString("requesterUUID")), UUID.fromString(resultSet.getString("targetUUID")), resultSet.getString("type")) else null
        resultSet.close()
        statement.close()
        return request
    }

    fun getAllTeleportRequests(uuid: UUID): List<TeleportRequest> {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT * FROM teleport_requests WHERE requesterUUID = ? OR targetUUID = ?")
        statement.setString(1, uuid.toString())
        statement.setString(2, uuid.toString())
        val resultSet: ResultSet = statement.executeQuery()
        val requests = mutableListOf<TeleportRequest>()
        while (resultSet.next()) {
            requests.add(TeleportRequest(UUID.fromString(resultSet.getString("requesterUUID")), UUID.fromString(resultSet.getString("targetUUID")), resultSet.getString("type")))
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

        if (getTeleportRequest(sender.uniqueId, acceptee.uniqueId, "tpa") != null) {
            sender.sendMessage("&cYou have already sent a teleport request to this player.".translate())
            return
        }

        saveTeleport(sender.uniqueId, acceptee.uniqueId, "tpa")
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

        val tpaRequest = getTeleportRequest(sender.uniqueId, acceptee.uniqueId, "tpa")
        val tpaHereRequest = getTeleportRequest(sender.uniqueId, acceptee.uniqueId, "tpahere")

        when {
            tpaRequest != null -> {
                if (config.getBoolean("dupe.debug", false)) {
                    Logger.log("TPA request found: ${sender.name} -> ${acceptee.name}", "debug")
                }
                teleportManager.tpaTeleport(sender, acceptee)
                deleteTeleportRequest(sender.uniqueId, acceptee.uniqueId, "tpa")
                Logger.log("TPA request removed: ${sender.name} -> ${acceptee.name}", "debug")
            }
            tpaHereRequest != null -> {
                if (config.getBoolean("dupe.debug", false)) {
                    Logger.log("TPAHere request found: ${sender.name} -> ${acceptee.name}", "debug")
                }
                teleportManager.tpaTeleport(acceptee, sender)
                deleteTeleportRequest(sender.uniqueId, acceptee.uniqueId, "tpahere")
                Logger.log("TPAHere request removed: ${sender.name} -> ${acceptee.name}", "debug")
            }
            else -> {
                if (config.getBoolean("dupe.debug", false)) {
                    getAllTeleportRequests(acceptee.uniqueId).forEach {
                        Logger.log("Teleport request: ${it.requesterUUID} -> ${it.targetUUID} (${it.type})", "debug")
                    }
                    Logger.log("No teleport requests found for ${sender.name} -> ${acceptee.name}", "debug")
                }
                acceptee.sendMessage(language.getMessages("tpa.teleport-notfound").replace("%target%", sender.name).translate())
            }
        }
    }

    fun requestTeleportHere(sender: Player, target: Player) {
        if (sender == target) {
            sender.sendMessage("&cYou cannot teleport to yourself.".translate())
            return
        }

        if (getTeleportRequest(sender.uniqueId, target.uniqueId, "tpahere") != null) {
            sender.sendMessage("&cYou have already sent a teleport request to this player.".translate())
            return
        }

        if (config.getBoolean("dupe.debug", false)) {
            Logger.log("Attempting to send teleport request from ${sender.name} to ${target.name}", "debug")
        }

        saveTeleport(sender.uniqueId, target.uniqueId, "tpahere")
        Logger.log("Teleport request added: ${sender.name} -> ${target.name}", "debug")
        sender.sendMessage(language.getMessages("tpa.send-request-here").replace("%target%", target.name).translate())
        target.sendMessage(language.getMessages("tpa.receive-request-here").replace("%target%", sender.name).translate())
    }

    fun cancelTeleport(sender: Player, target: Player) {
        val tpaRequest = getTeleportRequest(sender.uniqueId, target.uniqueId, "tpa")
        val tpaHereRequest = getTeleportRequest(sender.uniqueId, target.uniqueId, "tpahere")

        if (tpaRequest != null) {
            deleteTeleportRequest(sender.uniqueId, target.uniqueId, "tpa")
            sender.sendMessage(language.getMessages("tpa.cancel-request").replace("%target%", target.name).translate())
            target.sendMessage(language.getMessages("tpa.cancel-request-other").replace("%target%", sender.name).translate())
        } else if (tpaHereRequest != null) {
            deleteTeleportRequest(sender.uniqueId, target.uniqueId, "tpahere")
            sender.sendMessage(language.getMessages("tpa.cancel-request-here").replace("%target%", target.name).translate())
            target.sendMessage(language.getMessages("tpa.cancel-request-here-other").replace("%target%", sender.name).translate())
        } else {
            sender.sendMessage(language.getMessages("tpa.cancel-request-notfound").replace("%target%", target.name).translate())
        }
    }

    fun denyTeleport(sender: Player, target: Player) {
        val tpaRequest = getTeleportRequest(target.uniqueId, sender.uniqueId, "tpa")
        val tpaHereRequest = getTeleportRequest(target.uniqueId, sender.uniqueId, "tpahere")

        if (tpaRequest != null) {
            deleteTeleportRequest(target.uniqueId, sender.uniqueId, "tpa")
            sender.sendMessage(language.getMessages("tpa.deny-request").replace("%target%", target.name).translate())
            target.sendMessage(language.getMessages("tpa.deny-request-other").replace("%target%", sender.name).translate())
        } else if (tpaHereRequest != null) {
            deleteTeleportRequest(target.uniqueId, sender.uniqueId, "tpahere")
            sender.sendMessage(language.getMessages("tpa.deny-request-here").replace("%target%", target.name).translate())
            target.sendMessage(language.getMessages("tpa.deny-request-here-other").replace("%target%", sender.name).translate())
        } else {
            sender.sendMessage(language.getMessages("tpa.deny-request-notfound").replace("%target%", target.name).translate())
        }
    }

}