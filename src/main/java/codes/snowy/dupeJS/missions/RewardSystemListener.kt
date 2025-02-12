package codes.snowy.dupeJS.missions

import codes.snowy.dupeJS.utils.translate
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

class RewardSystemListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title == "&#feda36&lSPINNING WHEEL".translate()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (event.view.title == "&#feda36&lSPINNING WHEEL".translate()) {
            event.isCancelled = true
        }
    }
} 