package codes.snowy.dupeJS.session

import codes.snowy.dupeJS.staff.vanish.VanishManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class SessionListener: Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (VanishManager.isVanished(player)) {
            event.joinMessage = "TRUE IS VANISHED"
            VanishManager.toggleVanish(player, true)
            return
        }
        if (player.hasPermission("dupejs.staff")) {
            event.joinMessage = "&8[&c+&8] &c&o${player.name} &f&ohas connected".translate()
            return
        } else if (player.hasPermission("dupejs.donator")) {
            event.joinMessage = "&8[&b+&8] &b&o${player.name} &f&ohas connected".translate()
            return
        }
        event.joinMessage = "&8[&a+&8] &7&o${player.name}".translate()
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (VanishManager.isVanished(player)) {

            return
        }
        event.quitMessage = ""
    }
}