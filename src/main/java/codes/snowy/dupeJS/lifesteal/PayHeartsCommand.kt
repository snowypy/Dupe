package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("hearts")
class PayHeartsCommand(private val manager: LifestealManager) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Subcommand("pay")
    @CommandAlias("payhearts")
    @CommandCompletion("@players")
    fun onPayHearts(sender: Player, targetName: String, @Optional amount: Int?) {
        val target = Bukkit.getPlayerExact(targetName)

        if (target == null) {
            sender.sendMessage("&#f6294b&lLIFESTEAL &8| &cPlayer '$targetName' not found.".translate())
            return
        }

        val heartsToPay = amount ?: 1
        if (heartsToPay < 1) {
            sender.sendMessage("&#f6294b&lLIFESTEAL &8| &cYou must pay at least 1 heart.".translate())
            return
        }

        if (manager.payHearts(sender, target, heartsToPay)) {
            sender.sendMessage("&#f6294b&lLIFESTEAL &8| &aYou paid $heartsToPay heart(s) to ${target.name}.".translate())
            target.sendMessage("&#f6294b&lLIFESTEAL &8| &aYou received $heartsToPay heart(s) from ${sender.name}.".translate())
        } else {
            sender.sendMessage("&#f6294b&lLIFESTEAL &8| &cTransaction failed. Either you don’t have enough hearts, or ${target.name} would exceed the heart cap.".translate())
        }
    }
}
