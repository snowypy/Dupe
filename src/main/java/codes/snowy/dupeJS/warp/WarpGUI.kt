package codes.snowy.dupeJS.warp

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.NamespacedKey
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.ItemFlag

class WarpGUI(private val warpManager: WarpManager, private val teleportManager: TeleportManager) {

    fun open(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, "&#33ffcd&lWARP MENU".translate())
        val warps = warpManager.getAllWarpNames()
        val middleRowStart = 9
        val middleRowEnd = 18
        val middleRowSize = middleRowEnd - middleRowStart
        val startIndex = middleRowStart + (middleRowSize - warps.size) / 2

        warps.forEachIndexed { index, warpName ->
            if (index >= middleRowSize) return@forEachIndexed

            val warpDetails = warpManager.getWarpDetails(warpName)
            val location = warpManager.getWarpLocation(warpName)
            val item = ItemStack(Material.valueOf(warpDetails.previewItem)).apply {
                itemMeta = itemMeta?.apply {
                    setDisplayName(warpDetails.displayName.translate())
                    if (location != null) {
                        lore = listOf(
                            "&#33ffcd&l| &fGlobal Visits: ${warpManager.getVisitCount(warpName)}".translate(),
                            "&#33ffcd&l| &fLocation: x:${location.x.toInt()} y:${location.y.toInt()} z:${location.z.toInt()}".translate(),
                            "",
                            "&7[CLICK TO WARP]".translate()
                        )
                    }
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                }
            }
            val nbtItem = NBTItem(item)
            nbtItem.setString("warpName", warpName)
            inventory.setItem(startIndex + index, nbtItem.item)
        }

        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName(" ")
                    }
                })
            }
        }

        player.openInventory(inventory)
    }

    fun handleWarpSelection(player: Player, clickedItem: ItemStack) {
        val itemMeta = clickedItem.itemMeta
        val key = NamespacedKey(DupeJS.getInstance(), "warpName")
        val warpName = itemMeta?.persistentDataContainer?.get(key, PersistentDataType.STRING)

        if (warpName != null) {
            val location = warpManager.getWarpLocation(warpName)
            if (location != null) {
                teleportManager.teleportPlayer(player, location, warpName)
            } else {
                player.sendMessage("&cWarp location not found.".translate())
            }
        } else {
            player.sendMessage("&cWarp name not found in item.".translate())
        }
    }
} 