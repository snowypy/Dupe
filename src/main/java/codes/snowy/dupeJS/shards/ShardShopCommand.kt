package codes.snowy.dupeJS.shards

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("shardshop|sshop")
class ShardShopCommand(private val shopGUI: ShardShopGUI) : BaseCommand() {

    @Default
    fun onCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        shopGUI.open(sender)
    }
} 