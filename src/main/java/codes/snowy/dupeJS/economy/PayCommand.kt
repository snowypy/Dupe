package codes.snowy.dupeJS.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.NumberConverter.convertCompact
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("pay")
class PayCommand : BaseCommand() {

    @HelpCommand
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @Syntax("<player> <amount>")
    @CommandCompletion("@players 1|10|100|1000|10000|100000|1000000|10000000|100000000|1000000000")
    @Description("Pay another player an amount of money")
    fun onPayCommand(sender: Player, targetName: String, amount: Double) {
        val target = Bukkit.getPlayerExact(targetName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't find a player by the name &#ff0000&n$targetName".translate())
            return
        }

        if (amount <= 0) {
            sender.sendMessage("&#FF0000&lERROR &8| &fYou must pay a positive amount.".translate())
            return
        }

        if (!VaultHook.hasEconomy()) {
            sender.sendMessage("&#FF0000&lERROR &8| &fThere was an error with the Economy.".translate())
            return
        }

        val result = VaultHook.withdraw(sender, amount)
        if (result.isEmpty()) {
            VaultHook.deposit(target, amount)
            sender.sendMessage("&#00FF00&lSUCCESS &8| &fYou sent &#00FF00$${convertCompact(amount)} &7[$${amount.toInt()}]&f to ${target.name}".translate())
            target.sendMessage("&#00FF00&lSUCCESS &8| &fYou received &#00FF00$${convertCompact(amount)} &7[$${amount.toInt()}]&f from ${sender.name}".translate())
        } else {
            sender.sendMessage("&#FF0000&lERROR &8| &f$result".translate())
        }
    }
}
