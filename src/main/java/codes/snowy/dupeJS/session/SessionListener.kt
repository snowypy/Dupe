package codes.snowy.dupeJS.session

import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class SessionListener: Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPermission("dupejs.staff")) {
            // [$] Add vanish check.
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
        event.quitMessage = ""
        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission("dupejs.staff") }
            .forEach { it.sendMessage("&8[INSPECT] &7&o${player.name} &c(${Bukkit.getOnlinePlayers().size})".translate()) }
    }

}