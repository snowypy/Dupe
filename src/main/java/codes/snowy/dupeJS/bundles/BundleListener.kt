package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.bundles.BundleGUI
import codes.snowy.dupeJS.bundles.BundleInventoryHolder
import codes.snowy.dupeJS.bundles.BundleManager
import codes.snowy.dupeJS.dupe.DupeManager
import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable

class BundleListener(private val dupeManager: DupeManager) : Listener {

    private val ongoingAnimations = mutableMapOf<Player, BukkitRunnable>()

    @EventHandler
    fun onBundleOpen(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val bundleName = item.itemMeta?.displayName ?: return
        val bundleStripped = ChatColor.stripColor(bundleName)?.replace(" Bundle", "") ?: return

        val bundle = BundleManager.getBundle(bundleStripped)
        if (bundle != null) {

            if (!dupeManager.nbtCheck(item)) {
                player.sendMessage("&#FF0000&lBLOCKED &8| &cThis item cannot be confirmed to be a bundle.".translate())
                player.sendMessage("&#FF0000&lBLOCKED &8| &cPlease contact an admin if you believe this is a mistake.".translate())
                return
            }

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
        val player = event.player as? Player ?: return
        val inventoryHolder = event.inventory.holder as? BundleInventoryHolder ?: return

        if (inventoryHolder.type == "opening") {
            ongoingAnimations[player]?.let { task ->
                task.cancel()
                ongoingAnimations.remove(player)

                val bundle = inventoryHolder.bundle
                if (BundleGUI.removeBundleItem(player, bundle)) {
                    val selectedItem = bundle.items.random()
                    player.inventory.addItem(selectedItem)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerBundleClickDuringOpening(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.clickedInventory ?: return
        val inventoryHolder = inventory.holder as? BundleInventoryHolder ?: return

        if (ongoingAnimations.contains(player)) {
            event.isCancelled = true
        }
    }

}
