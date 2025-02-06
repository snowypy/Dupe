package codes.snowy.dupeJS.shards

import codes.snowy.dupeJS.afk.AFKManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ShardShopListener(private val afkManager: AFKManager) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title != "&b&lShard Shop".translate()) return
        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        
        val items = ShardShop.getItems()
        val shopItem = items.entries.find { (_, item) ->
            clickedItem.type == item.material && 
            clickedItem.itemMeta?.displayName == item.displayName
        } ?: return

        val playerShards = afkManager.getShards(player.uniqueId)
        if (playerShards < shopItem.value.price) {
            player.sendMessage("&#9436fe&lSHARDS &8| &cYou don't have enough shards! You need ${shopItem.value.price} shards.".translate())
            return
        }

        afkManager.removeShards(player.uniqueId, shopItem.value.price)
        
        shopItem.value.commands.forEach { command ->
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command.replace("%player%", player.name)
            )
        }

        player.sendMessage("&#9436fe&lSHARDS &8| &aSuccessfully purchased ${shopItem.value.displayName}&a!".translate())
    }
} 