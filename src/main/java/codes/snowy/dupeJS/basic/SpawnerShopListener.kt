package codes.snowy.dupeJS.basic

import codes.snowy.dupeJS.shards.ShardShopGUI
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryEvent

class SpawnerShopListener(private val shopGUI: ShardShopGUI) : Listener {

    @EventHandler
    fun onMenuClick(event: InventoryClickEvent) {
        if (!(event.view.title == "Shop".translate())) {
            return
        }

        if (event.currentItem?.type == Material.SPAWNER) {
            event.isCancelled = true
            shopGUI.open(event.whoClicked as Player)
        }
    }

}