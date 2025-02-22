package codes.snowy.dupeJS.items.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.items.ItemManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("givecustom")
@CommandPermission("dupe.admin.items")
class GiveCustomCommand(private val itemManager: ItemManager) : BaseCommand() {

    @Default
    @Syntax("<player> <item-name> <amount>")
    @CommandCompletion("@players")
    fun onGive(sender: CommandSender, targetName: String, itemName: String, amount: Int) {
        val target = Bukkit.getPlayer(targetName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lITEMS &8| &fPlayer not found.".translate())
            return
        }

        if (amount <= 0) {
            sender.sendMessage("&#FF0000&lITEMS &8| &fAmount must be greater than 0.".translate())
            return
        }

        val item = itemManager.getItem(itemName)
        if (item == null) {
            sender.sendMessage("&#FF0000&lITEMS &8| &fItem '&c$itemName&f' not found.".translate())
            return
        }

        item.amount = amount
        target.inventory.addItem(item)
        
        sender.sendMessage("&#00FF00&lITEMS &8| &fGave &a$amount &fof '&a$itemName&f' to &a${target.name}&f.".translate())
    }
} 