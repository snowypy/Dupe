package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.bundles.BundleGUI
import codes.snowy.dupeJS.bundles.BundleInventoryHolder
import codes.snowy.dupeJS.bundles.BundleManager
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent

class BundleListener : Listener {

    @EventHandler
    fun onBundleOpen(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val bundleName = item.itemMeta?.displayName ?: return
        val bundleStripped = ChatColor.stripColor(bundleName)?.replace(" Bundle", "") ?: return

        val bundle = BundleManager.getBundle(bundleStripped)
        if (bundle != null) {

            event.isCancelled = true

            when (event.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> BundleGUI.openBundlePreview(player, bundle)
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> BundleGUI.openBundleAnimation(player, bundle)
                else -> return
            }
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return

        if (inventory.holder is BundleInventoryHolder) {
            val bundleHolder = inventory.holder as BundleInventoryHolder

            event.isCancelled = true

            when (bundleHolder.type) {
                "preview" -> {
                    // [$] Add logging at some point
                }
                "opening" -> {
                    // [$] Add logging at some point
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (inventory.holder is BundleInventoryHolder) {
            val bundleHolder = inventory.holder as BundleInventoryHolder
        }
    }
}
