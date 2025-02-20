package codes.snowy.dupeJS.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.NumberConverter.convertCompact
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("balance|bal")
class BalanceCommand : BaseCommand() {

    @HelpCommand
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @Description("Check your balance or the balance of another player")
    @Syntax("[player]")
    @CommandCompletion("@players")
    fun onBalanceCommand(sender: CommandSender, @Optional playerName: String?) {
        val target: Player? = if (playerName != null) {
            Bukkit.getPlayer(playerName)
        } else {
            if (sender is Player) sender else null
        }

        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fPlayer not found or you must specify a player.".translate())
            return
        }

        val balance = VaultHook.getBalance(target)
        sender.sendMessage("&#00FF00&lBALANCE &8| &f${target.name}'s balance is &#00FF00$${convertCompact(balance.toLong())} &7[$${balance.toLong().toString().replace(".0", "")}]".translate())
    }
} 