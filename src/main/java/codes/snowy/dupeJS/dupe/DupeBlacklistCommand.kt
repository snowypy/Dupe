package codes.snowy.dupeJS.dupe

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("blacklistdupe")
class DupeBlacklistCommand(val dupeManager: DupeManager) : BaseCommand() {

    @Default
    @CommandPermission("staff.blacklist")
    @Description("Manages item blacklists.")
    fun onDupeBlacklistCommand(sender: CommandSender) {
        dupeManager.blacklistDupe(sender as Player)
    }
}