package codes.snowy.dupeJS.dupe

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("rechargedupe")
class DupeRechargeCommand(val dupeManager: DupeManager) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @CommandCompletion("@players")
    @Description("Recharges the dupe.")
    fun onDupeRechargeCommand(sender: CommandSender, @Optional player: Player?) {

        if (!sender.hasPermission("dupe.recharge")) {
            sender.sendMessage("&cYou do not have permission to use this command.".translate())
            return
        }

        if (player == null) {
            dupeManager.rechargeDupe(sender as Player)
        } else {
            dupeManager.rechargeDupe(player)
            sender.sendMessage("&aYou recharged ${player.name}'s dupe charges.".translate())
        }
    }
}