package codes.snowy.dupeJS.staff.chat

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object StaffChatManager {
    private val staffChatToggledPlayers = mutableSetOf<Player>()
    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)
    private val luckPerms: LuckPerms = LuckPermsProvider.get()

    fun toggleStaffChat(player: Player): Boolean {
        return if (staffChatToggledPlayers.contains(player)) {
            staffChatToggledPlayers.remove(player)
            false
        } else {
            staffChatToggledPlayers.add(player)
            true
        }
    }

    fun isStaffChatEnabled(player: Player): Boolean {
        return staffChatToggledPlayers.contains(player)
    }

    fun sendStaffChatMessage(sender: Player, message: String) {
        val user: User = luckPerms.userManager.getUser(sender.uniqueId) ?: return
        val prefix = user.cachedData.metaData.prefix ?: ""
        val staffMessage = language.getReplacedMessage("staff.chat.format").toString()
            .replace("%prefix%", prefix)
            .replace("%player%", sender.displayName)
            .replace("%message%", message)

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(config.getValue("permission.staff.chat.format").toString())) {
                onlinePlayer.sendMessage(staffMessage.translate())
            }
        }
    }
}