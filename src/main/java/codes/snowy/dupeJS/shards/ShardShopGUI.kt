package codes.snowy.dupeJS.shards

import codes.snowy.dupeJS.afk.AFKManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ShardShopGUI(private val afkManager: AFKManager) {

    fun open(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, "&b&lShard Shop".translate())
        val items = ShardShop.getItems()

        items.entries.forEachIndexed { index, (id, item) ->
            val displayItem = ItemStack(item.material).apply {
                itemMeta = itemMeta?.apply {
                    setDisplayName(item.displayName)
                    lore = item.lore
                }
            }
            inventory.setItem(10 + index, displayItem)
        }

        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName(" ")
                    }
                })
            }
        }

        player.openInventory(inventory)
    }
} 