package codes.snowy.dupeJS.items.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.items.ItemManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player

@CommandAlias("adminsave")
@CommandPermission("dupe.admin.items")
class AdminSaveCommand(private val itemManager: ItemManager) : BaseCommand() {

    @Default
    @Syntax("<item-name>")
    fun onSave(player: Player, itemName: String) {
        val item = player.inventory.itemInMainHand
        
        if (item.type.isAir) {
            player.sendMessage("&#FF0000&lITEMS &8| &fYou must be holding an item to save.".translate())
            return
        }

        if (itemManager.itemExists(itemName)) {
            player.sendMessage("&#FF0000&lITEMS &8| &fAn item with that name already exists.".translate())
            return
        }

        itemManager.saveItem(itemName, item)
        player.sendMessage("&#00FF00&lITEMS &8| &fSaved item as '&a$itemName&f'".translate())
    }
} 