package codes.snowy.dupeJS.dupe

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender

@CommandAlias("rechargealldupe")
class RechargeAllDupeCommand(val dupeManager: DupeManager) : BaseCommand() {

    @HelpCommand
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @CommandPermission("dupe.rechargeall")
    @Description("Recharges all players' dupe charges.")
    fun onRechargeAllDupeCommand(sender: CommandSender) {
        if (!sender.hasPermission("dupe.rechargeall")) {
            sender.sendMessage("&cYou do not have permission to use this command.".translate())
            return
        }

        dupeManager.resetAllDupeCounts()
    }
} 