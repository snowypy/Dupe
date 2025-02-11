package codes.snowy.dupeJS.missions

import codes.snowy.dupeJS.utils.translate
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
        val title = event.view.title
        val clickedItem: ItemStack? = event.currentItem

        if (title == "&#feda36&lMISSIONS".translate()) {
            event.isCancelled = true

            clickedItem?.itemMeta?.lore?.let { lore ->
                if (lore.any { it.contains("COMPLETE") }) {
                    rewardSystem.spinRewardWheel(player)
                } else {
                    player.sendMessage("&#feda36&lMISSIONS &8| &#FF0000This mission is not complete yet.".translate())
                }
            }
        }
    }
} 