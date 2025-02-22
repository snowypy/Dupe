package codes.snowy.dupeJS.shards

import codes.snowy.dupeJS.afk.AFKManager
import codes.snowy.dupeJS.utils.Logger
import codes.snowy.dupeJS.utils.NumberConverter.convertCompact
import codes.snowy.dupeJS.utils.NumberFormatter.convertShort
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemFlag

class ShardShopGUI(private val afkManager: AFKManager) {

    fun open(player: Player) {
        val inventory = Bukkit.createInventory(null, 36, "&#9436fe&lShard Shop".translate())
        val items = ShardShop.getItems()

        val statItem = ItemStack(Material.AMETHYST_SHARD).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("&#9436fe&lShard Shop".translate())
                lore = listOf(
                    "".translate(),
                    "&fYou can earn shards by visiting &7[/AFK]".translate(),
                    "".translate(),
                    "&fYour Shards: &#9436fe${afkManager.getShards(player.uniqueId)}".translate(),
                    "".translate()
                )
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }
        }

        inventory.setItem(4, statItem)

        val middleSlots = listOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25)
        
        items.entries.forEachIndexed { index, (id, item) ->
            if (index < middleSlots.size) {
                val displayItem = ItemStack(item.material).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName(item.displayName)
                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                        setCustomModelData(1111)
                        val updatedLore = mutableListOf<String>()
                        updatedLore.addAll(item.lore)
                        updatedLore.add("&7Cost: &#9436fe${item.price} Shards".translate())
                        if (item.cashPrice != null) {
                            updatedLore.add("&7Cash Price: &a$${convertCompact(item.cashPrice.toLong())}".translate())
                            updatedLore.add("")
                            updatedLore.add("&7Left-Click to purchase with &#9436feShards".translate())
                            updatedLore.add("&7Right-Click to purchase with &a$${convertCompact(item.cashPrice.toLong())}".translate())
                        }
                        lore = updatedLore

                    }
                }


                inventory.setItem(middleSlots[index], displayItem)
            }
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