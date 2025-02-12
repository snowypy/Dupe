package codes.snowy.dupeJS.warp

import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.NamespacedKey
import codes.snowy.dupeJS.DupeJS
import de.tr7zw.changeme.nbtapi.NBTItem

class WarpGUIListener(private val warpManager: WarpManager, private val teleportManager: TeleportManager) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return

        if (event.view.title == "&#33ffcd&lWARP MENU".translate()) {
            event.isCancelled = true

            val clickedItem = event.currentItem ?: return
            val nbtItem = NBTItem(clickedItem)
            val warpName = nbtItem.getString("warpName")

            if (warpName != null) {
                val location = warpManager.getWarpLocation(warpName)
                if (location != null) {
                    warpManager.incrementVisitCount(warpName)
                    teleportManager.teleportPlayer(player, location, warpName)
                    player.closeInventory()
                } else {
                    player.sendMessage("&cWarp location not found.".translate())
                    player.closeInventory()
                }
            } else {
                player.sendMessage("&cWarp name not found in item.".translate())
                player.closeInventory()
            }
        }
    }
} 