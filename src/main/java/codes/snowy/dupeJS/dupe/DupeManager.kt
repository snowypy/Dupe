package codes.snowy.dupeJS.dupe

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Logger
import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBT
import de.tr7zw.changeme.nbtapi.NBTItem
import formatMaterial
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DupeManager {


    private val config = Config(DupeJS.getInstance())

    fun dupe(player: Player) {
        val player = player
        var item = player.itemInHand
        val itemamount = item.amount

        if (item.type.toString() == "AIR") {
            player.sendMessage("&#7723ea&lDUPE &8| &cYou are not holding an item.".translate())
            return
        }

        if (player.inventory.size <= 36) {
            player.sendMessage("&#7723ea&lDUPE &8| &cYour inventory is full.".translate())
            return
        }

        if (nbtCheck(item)) {
            player.sendMessage("&#7723ea&lDUPE &8| &cThis item is not allowed to be duplicated.".translate())
            return
        }

        player.inventory.addItem(item)
        val itemformat = item.type.toString().formatMaterial()

        if (itemamount > 1) {
            player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have duplicated &#ac9ac6$itemamount&#ac9ac6x $itemformat".translate())
        } else {
            player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have duplicated a &#ac9ac6$itemformat".translate())
        }
    }

    fun blacklistDupe(player: Player) {
        val player = player
        var item = player.itemInHand

        if (item.type.toString() == "AIR") {
            player.sendMessage("&#7723ea&lDUPE &8| &cYou are not holding an item.".translate())
            return
        }

        if (nbtCheck(item)) {
            player.sendMessage("&#7723ea&lDUPE &8| &cThis item is already blacklisted.".translate())
            return
        }

        val nbtItem = NBTItem(item)
        nbtItem.setInteger("custom_model_data", config.getInt("dupe.modeldata", 1111))
        player.setItemInHand(nbtItem.item)
        player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have blacklisted this item.".translate())
    }

    fun nbtCheck(stack: ItemStack): Boolean {

        val nbtItem = NBT.itemStackToNBT(stack)

        val customModelData = nbtItem.toString()
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log(customModelData, "debug")
        }
        if ("custom_model_data:" + config.getInt("dupe.modeldata", 1111) in customModelData) {
            return true
        } else {
            return false
        }
    }
}
