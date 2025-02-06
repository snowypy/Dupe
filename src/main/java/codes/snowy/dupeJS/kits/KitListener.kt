package codes.snowy.dupeJS.kits

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import codes.snowy.dupeJS.utils.translate

class KitListener(
    private val kitManager: KitManager,
    private val cooldownManager: KitCooldownManager
) : Listener {
    
    private val editingSessions = mutableMapOf<Player, String>()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val title = event.view.title
        val player = event.whoClicked as? Player ?: return

        when {
            title == "&b&lKits".translate() -> {
                event.isCancelled = true
                val clickedItem = event.currentItem ?: return
                val kitName = clickedItem.itemMeta?.displayName?.replace("§b", "") ?: return
                
                val kit = kitManager.getKit(kitName) ?: return
                if (!player.hasPermission(kit.permission)) return

                if (!cooldownManager.canUseKit(player.uniqueId, kit.name)) {
                    val remaining = cooldownManager.getRemainingCooldown(player.uniqueId, kit.name)
                    val hours = remaining / (1000 * 60 * 60)
                    val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)
                    player.sendMessage("&cYou must wait &e${hours}h ${minutes}m &cbefore using this kit again!".translate())
                    return
                }

                kit.items.forEach { item ->
                    player.inventory.addItem(item.clone())
                }
                cooldownManager.setKitCooldown(player.uniqueId, kit.name)
                player.sendMessage("&aYou have claimed the ${kit.name} kit!".translate())
                player.closeInventory()
            }
            
            title.startsWith("&b&lKit Editor:".translate()) -> {
                val kitName = title.substringAfter("&b&lKit Editor: ".translate())
                editingSessions[player] = kitName
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val title = event.view.title
        
        if (title.startsWith("&b&lKit Editor:".translate())) {
            val kitName = editingSessions[player] ?: return
            val kit = kitManager.getKit(kitName)
            
            val items = event.inventory.contents.filterNotNull().map { it.clone() }
            
            if (kit != null) {
                val updatedKit = kit.copy(items = items)
                kitManager.saveKit(updatedKit)
                player.sendMessage("&aKit '${kitName}' has been updated with ${items.size} items!".translate())
            }
            
            editingSessions.remove(player)
        }
    }
} 