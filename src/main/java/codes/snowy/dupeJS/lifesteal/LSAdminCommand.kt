package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("lsadmin")
@CommandPermission("dupe.lsadmin")
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
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fAdded $amount heart(s) to &#f987ca&n${target.name}&f.")
    }

    @Subcommand("remove")
    @Syntax("<target> <amount>")
    fun onRemove(sender: CommandSender, target: Player, amount: Int) {
        manager.removeHearts(target, amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fRemoved $amount heart(s) from &#f987ca&n${target.name}&f.")
    }

    @Subcommand("giveall")
    @Syntax("<amount>")
    fun onGiveAll(sender: CommandSender, amount: Int) {
        manager.giveAllHearts(amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fGave $amount heart(s) to all players.")
    }

    @Subcommand("giveitem")
    @Syntax("<target> <amount>")
    fun onGiveItem(sender: CommandSender, target: Player, amount: Int) {
        manager.giveHeartItem(target, amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fGave $amount heart(s) to &#f987ca&n${target.name}&f.")
    }

    @Subcommand("giveallitem")
    @Syntax("<amount>")
    fun onGiveAllItem(sender: CommandSender, amount: Int) {
        manager.giveAllHeartItems(amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fGave $amount heart(s) to all players.")
    }
}
