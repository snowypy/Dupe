package codes.snowy.dupeJS.shards

import codes.snowy.dupeJS.afk.AFKManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.ClickType
import codes.snowy.dupeJS.economy.VaultHook

class ShardShopListener(private val afkManager: AFKManager) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.view.title != "&#9436fe&lShard Shop".translate()) return
        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.currentItem ?: return
        
        val items = ShardShop.getItems()
        val shopItem = items.entries.find { (_, item) ->
            clickedItem.type == item.material && 
            clickedItem.itemMeta?.displayName == item.displayName
        } ?: return

        when (event.click) {
            ClickType.LEFT -> handleShardPurchase(player, shopItem.value)
            ClickType.RIGHT -> handleCashPurchase(player, shopItem.value)
            else -> return
        }
    }

    private fun handleShardPurchase(player: Player, item: ShopItem) {
        val playerShards = afkManager.getShards(player.uniqueId)
        if (playerShards < item.price) {
            player.sendMessage("&#9436fe&lSHARDS &8| &cYou don't have enough shards! You need ${item.price} shards.".translate())
            return
        }
        afkManager.removeShards(player.uniqueId, item.price)
        executeCommands(player, item)
        player.sendMessage("&#9436fe&lSHARDS &8| &aSuccessfully purchased ${item.displayName}&a!".translate())
    }

    private fun handleCashPurchase(player: Player, item: ShopItem) {
        val cashPrice = item.cashPrice
        if (cashPrice == null) {
            player.sendMessage("&#9436fe&lSHARDS &8| &cThis item cannot be purchased with cash.".translate())
            return
        }

        if (!VaultHook.hasEconomy()) {
            player.sendMessage("&#9436fe&lSHARDS &8| &cThere was an error with the Economy.".translate())
            return
        }

        if (VaultHook.getBalance(player) < cashPrice) {
            player.sendMessage("&#9436fe&lSHARDS &8| &cYou don't have enough money! You need $${String.format("%.2f", cashPrice)}.".translate())
            return
        }

        VaultHook.withdraw(player, cashPrice)
        executeCommands(player, item)
        player.sendMessage("&#9436fe&lSHARDS &8| &aSuccessfully purchased ${item.displayName}&a for $${String.format("%.2f", cashPrice)}!".translate())
    }

    private fun executeCommands(player: Player, item: ShopItem) {
        item.commands.forEach { command ->
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command.replace("%player%", player.name)
            )
        }
    }
} 