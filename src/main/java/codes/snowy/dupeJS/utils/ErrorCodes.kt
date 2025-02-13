package codes.snowy.dupeJS.utils

import org.bukkit.entity.Player
import codes.snowy.dupeJS.utils.translate

fun Player.sendError(error: String) : String {
    if (error.isEmpty()) return "done"
    player?.sendMessage("")
    player?.sendMessage("&#FF0000&lDUPEJS ERROR".translate())
    player?.sendMessage("&#FF0000&n$error".translate())
    player?.sendMessage("")
    player?.sendMessage("&7&oPlease make a ticket in our Discord to report this.".translate())
    player?.sendMessage("")
    return "done"
}

