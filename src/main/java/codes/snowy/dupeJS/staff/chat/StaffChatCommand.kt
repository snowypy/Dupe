package codes.snowy.dupeJS.staff.chat

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Optional
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player

@CommandAlias("sc|staffchat")
@CommandPermission("vandal.staff.chat")
class StaffChatCommand : BaseCommand() {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)

    @Default
    @Description("[Ghost] Toggle staff chat or send a message to staff chat")
    fun onStaffChat(player: Player, @Optional message: String?) {

        if (!player.hasPermission(config.getValue("permission.staff.chat").toString())) {
            player.sendMessage(language.getReplacedMessage("staff.chat.no-permission").translate())
            return
        }

        if (message.isNullOrEmpty()) {
            val enabled = StaffChatManager.toggleStaffChat(player)
            if (enabled) {
                player.sendMessage(language.getReplacedMessage("staff.chat.toggle.true").translate())
            } else {
                player.sendMessage(language.getReplacedMessage("staff.chat.toggle.false").translate())
            }
        } else {
            StaffChatManager.sendStaffChatMessage(player, message)
        }
    }
}