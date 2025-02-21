package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
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
    @Syntax("<targetName> <amount>")
    @CommandCompletion("@players 1|2|3|4|5|6|7|8|9|10")
    fun onAdd(sender: CommandSender, targetName: String, amount: Int) {
        val target = Bukkit.getPlayer(targetName) ?: run {
            sender.sendMessage("&#FF0000&nCouldn't find player with name $targetName.".translate())
            return
        }
        manager.addHearts(target, amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fAdded $amount heart(s) to &#f987ca&n${target.name}&f.".translate())
    }

    @Subcommand("remove")
    @Syntax("<target> <amount>")
    @CommandCompletion("@players 1|2|3|4|5|6|7|8|9|10")
    fun onRemove(sender: CommandSender, targetName: String, amount: Int) {
        val target = Bukkit.getPlayer(targetName) ?: run {
            sender.sendMessage("&#FF0000&nCouldn't find player with name $targetName.".translate())
            return
        }
        manager.removeHearts(target, amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fRemoved $amount heart(s) from &#f987ca&n${target.name}&f.".translate())
    }

    @Subcommand("giveall")
    @Syntax("<amount>")
    @CommandCompletion("1|2|3|4|5|6|7|8|9|10")
    fun onGiveAll(sender: CommandSender, amount: Int) {
        manager.giveAllHearts(amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fGave $amount heart(s) to all players.".translate())
    }

    @Subcommand("giveitem")
    @Syntax("<targetName> <amount>")
    @CommandCompletion("@players 1|2|3|4|5|6|7|8|9|10")
    fun onGiveItem(sender: CommandSender, targetName: String, amount: Int) {
        val target = Bukkit.getPlayer(targetName) ?: run {
            sender.sendMessage("&#FF0000&nCouldn't find player with name $targetName.".translate())
            return
        }
        manager.giveHeartItem(target, amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fGave $amount heart(s) to &#f987ca&n${target.name}&f.".translate())
    }

    @Subcommand("giveallitem")
    @Syntax("<amount>")
    @CommandCompletion("1|2|3|4|5|6|7|8|9|10")
    fun onGiveAllItem(sender: CommandSender, amount: Int) {
        manager.giveAllHeartItems(amount)
        sender.sendMessage("&#f987ca&lLIFESTEAL &8| &fGave $amount heart(s) to all players.".translate())
    }
}