package codes.snowy.dupeJS.dupe

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("blacklistdupe")
class DupeBlacklistCommand(val dupeManager: DupeManager) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @CommandPermission("staff.blacklist")
    @Description("Manages item blacklists.")
    fun onDupeBlacklistCommand(sender: CommandSender) {
        dupeManager.blacklistDupe(sender as Player)
    }
}