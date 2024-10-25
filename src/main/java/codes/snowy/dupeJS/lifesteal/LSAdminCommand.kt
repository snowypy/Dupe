package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("lsadmin")
class LSAdminCommand(private val manager: LifestealManager) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Subcommand("add")
    @Syntax("<target> <amount>")
    fun onAdd(sender: CommandSender, target: Player, amount: Int) {
        manager.addHearts(target, amount)
        sender.sendMessage("Added $amount heart(s) to ${target.name}.")
    }

    @Subcommand("remove")
    @Syntax("<target> <amount>")
    fun onRemove(sender: CommandSender, target: Player, amount: Int) {
        manager.removeHearts(target, amount)
        sender.sendMessage("Removed $amount heart(s) from ${target.name}.")
    }

    @Subcommand("giveall")
    @Syntax("<amount>")
    fun onGiveAll(sender: CommandSender, amount: Int) {
        manager.giveAllHearts(amount)
        sender.sendMessage("Gave $amount heart(s) to all players.")
    }
}
