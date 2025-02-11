package codes.snowy.dupeJS.missions

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MissionGUIListener(private val missionManager: MissionManager, private val rewardSystem: RewardSystem) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val inventory = event.inventory
        val clickedItem: ItemStack? = event.currentItem

        if (inventory.type.defaultTitle.contains("MISSIONS")) {
            event.isCancelled = true

            clickedItem?.itemMeta?.lore?.let { lore ->
                if (lore.any { it.contains("COMPLETE") }) {
                    rewardSystem.spinRewardWheel(player)
                }
            }
        }
    }
} 