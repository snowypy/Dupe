package codes.snowy.dupeJS.missions

import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

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
                when {
                    lore.any { it.contains("[CLICK TO CLAIM]") } -> {
                        val missionUUID = extractMissionUUIDFromItem(clickedItem)
                        if (missionUUID != null && !missionManager.isMissionClaimed(player, missionUUID)) {
                            rewardSystem.spinRewardWheel(player)
                            missionManager.markMissionAsClaimed(player, missionUUID)
                        } else {
                            player.sendMessage("&#feda36&lMISSIONS &8| &#FF0000This mission has already been claimed.".translate())
                        }
                    }
                    lore.any { it.contains("[CANNOT CLAIM]") } -> {
                        player.sendMessage("&#feda36&lMISSIONS &8| &#FF0000You cannot claim this mission.".translate())
                    }
                    lore.any { it.contains("[ALREADY CLAIMED]") } -> {
                        player.sendMessage("&#feda36&lMISSIONS &8| &#FF0000This mission has already been claimed.".translate())
                    }
                    else -> {
                        player.sendMessage("&#feda36&lMISSIONS &8| &#FF0000This mission is not complete yet.".translate())
                    }
                }
            }
        }
    }

    private fun extractMissionUUIDFromItem(item: ItemStack?): String? {
        if (item == null) return null
        val nbtItem = NBTItem(item)
        return nbtItem.getString("missionUUID")
    }
} 