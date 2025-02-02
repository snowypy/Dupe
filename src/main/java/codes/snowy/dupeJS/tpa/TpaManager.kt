package codes.snowy.dupeJS.tpa

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player

data class TeleportRequest(val requester: Player, val target: Player)

class TpaManager {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)
    private val teleportManager = TeleportManager(DupeJS.getInstance())
    private val teleportRequests = mutableListOf<TeleportRequest>()

    fun requestTeleport(sender: Player, acceptee: Player) {
        if (sender == acceptee) {
            sender.sendMessage("&cYou cannot teleport to yourself.".translate())
            return
        }

        if (teleportRequests.any { it.requester == sender && it.target == acceptee }) {
            sender.sendMessage("&cYou have already sent a teleport request to this player.".translate())
            return
        }

        teleportRequests.add(TeleportRequest(sender, acceptee))
        sender.sendMessage(language.getMessages("tpa.send-request").replace("%target%", acceptee.name).translate())
        acceptee.sendMessage(language.getMessages("tpa.receive-request").replace("%target%", sender.name).translate())
    }

    fun acceptTeleport(acceptee: Player, sender: Player) {
        val request = teleportRequests.find { it.requester == sender && it.target == acceptee }
        if (request != null) {
            teleportManager.tpaTeleport(acceptee, sender.location, sender)
            teleportRequests.remove(request)
        } else {
            acceptee.sendMessage(language.getMessages("tpa.notfound").replace("%target%", sender.name).translate())
        }
    }
}