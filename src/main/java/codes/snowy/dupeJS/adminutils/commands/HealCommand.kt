package codes.snowy.dupeJS.adminutils.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

@CommandAlias("heal")
class HealCommand: BaseCommand() {

    @HelpCommand
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @Syntax("<player>")
    @CommandCompletion("@players")
    fun onHealCommand(sender: CommandSender , @Optional player: Player?) {
        if (!sender.hasPermission("dupe.heal")) {
            sender.sendMessage("&cYou do not have permission to use this command.".translate())
            return
        }

        if (player === null) {
            if (sender is Player) {
                sender.health = sender.maxHealth
                sender.sendMessage("&aYou have been healed.".translate())
                return
            }
        }

        if (player != null) {
            if (!player.isOnline) {
                sender.sendMessage("&c${player.name} is not online.".translate())
                return
            }

            player.health = player.maxHealth
            player.foodLevel = 20
            player.sendMessage("&aYou have been healed.".translate())
            sender.sendMessage("&aYou healed ${player.name}.".translate())
        }

    }

}