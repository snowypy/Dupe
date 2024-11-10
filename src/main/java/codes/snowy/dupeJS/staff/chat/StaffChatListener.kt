package codes.snowy.dupeJS.staff.chat

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class StaffChatListener : Listener {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)
    private val luckPerms: LuckPerms = LuckPermsProvider.get()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        if (player.hasPermission("${config.getValue("permission.staff")}")) {
            val user: User = luckPerms.userManager.getUser(player.uniqueId) ?: return
            val prefix = user.cachedData.metaData.prefix ?: ""

            val joinMessage = language.getReplacedMessage("staff.join").toString()
                .replace("%prefix%", prefix)
                .replace("%player%", "${player.name}")
                .replace("%uuid%", player.uniqueId.toString())
                .replace("%gamemode%", config.getValue("server-name").toString())

            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("${config.getValue("permission.staff")}")) {
                    onlinePlayer.sendMessage(joinMessage.translate())
                }
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player

        if (player.hasPermission("${config.getValue("permission.staff.notify")}")) {
            val user: User = luckPerms.userManager.getUser(player.uniqueId) ?: return
            val prefix = user.cachedData.metaData.prefix ?: ""

            val quitMessage = language.getReplacedMessage("staff.quit").toString()
                .replace("%prefix%", prefix)
                .replace("%player%", "${player.name}")
                .replace("%uuid%", player.uniqueId.toString())

            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("${config.getValue("permission.staff.notify")}")) {
                    onlinePlayer.sendMessage(quitMessage.translate())
                }
            }
        }
    }

}