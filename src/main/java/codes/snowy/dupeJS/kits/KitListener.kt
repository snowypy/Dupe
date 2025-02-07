package codes.snowy.dupeJS.kits

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import codes.snowy.dupeJS.utils.translate
import org.bukkit.ChatColor

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
            title == "&#feda36&lKITS".translate() -> {
                event.isCancelled = true
                val clickedItem = event.currentItem ?: return
                val displayName = clickedItem.itemMeta?.displayName ?: return
                
                val kitName = ChatColor.stripColor(displayName).toString()
                val kit = kitManager.getKit(kitName) ?: return

                if (!player.hasPermission(kit.permission)) {
                    player.sendMessage("&#feda36&lKITS &8| &#ff0000You don't have permission to claim this kit.".translate())
                    return
                }

                if (!cooldownManager.canUseKit(player.uniqueId, kit)) {
                    val remaining = cooldownManager.getRemainingCooldown(player.uniqueId, kit)
                    val hours = remaining / (1000 * 60 * 60)
                    val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)
                    player.sendMessage("&#feda36&lKITS &8| &#ff0000You must wait &#feda36&n${hours}h ${minutes}m&r &#ff0000before using this kit again.".translate())
                    return
                }

                kit.items.forEach { item ->
                    player.inventory.addItem(item.clone())
                }
                cooldownManager.setKitCooldown(player.uniqueId, kit.name)
                cooldownManager.incrementTimesClaimed(player.uniqueId, kit.name)
                player.sendMessage("&#feda36&lKITS &8| &#10f08aYou have claimed the &#feda36&n${kit.name}&r &#10f08akit!".translate())
                player.closeInventory()
            }
            
            title.startsWith("&#feda36&lKIT EDITOR:".translate()) -> {
                val kitName = title.substringAfter("&#feda36&lKIT EDITOR: ".translate())
                editingSessions[player] = kitName
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val title = event.view.title
        
        if (title.startsWith("&#feda36&lKIT EDITOR:".translate())) {
            val kitName = editingSessions[player] ?: return
            val kit = kitManager.getKit(kitName)
            
            val items = event.inventory.contents.filterNotNull().map { it.clone() }
            
            if (kit != null) {
                val updatedKit = kit.copy(items = items)
                kitManager.saveKit(updatedKit)
                player.sendMessage("&#feda36&lKITS &8| &#10f08aKit '&#feda36&n${kitName}&r&#10f08a' has been updated with &#feda36&n${items.size}&r &#10f08aitems!".translate())
            }
            
            editingSessions.remove(player)
        }
    }
} 