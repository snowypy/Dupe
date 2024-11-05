package rip.hardcore.basic.menus

import codes.snowy.dupeJS.homes.HomeManager
import codes.snowy.dupeJS.utils.formatUppercase
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class HomeGUI(private val homeManager: HomeManager, private val player: Player) {

    fun open() {
        val inventory = Bukkit.createInventory(null, 27, "&7Your Homes".translate())
        val homes = homeManager.getHomes(player.uniqueId)
        val maxHomes = getMaxHomes(player)

        for (i in 0 until maxHomes) {
            val home = homes.getOrNull(i)
            val name = home?.name?.formatUppercase()
            val item = if (home != null) {
                ItemStack(Material.LIME_BED).apply {
                    itemMeta = createItemMeta(
                        "&#fa143e&l$name".translate(),
                        "&#f9628f".translate(),
                        "&#f9628f[LEFT CLICK TO TELEPORT]".translate(),
                        "&#f9628f[RIGHT CLICK TO DELETE]".translate()
                    )
                }
            } else {
                ItemStack(Material.GRAY_BED).apply {
                    itemMeta = createItemMeta(
                        "&7&lEmpty Slot".translate(),
                        "&cYou have no home set here".translate(),
                        "&#f9628f".translate(),
                        "&#f9628f[CLICK TO CREATE]".translate()
                    )
                }
            }
            inventory.setItem(11 + i, item)
        }

        for (i in inventory.contents.indices) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
                    itemMeta = createItemMeta("&7".translate(), "")
                })
            }
        }

        player.openInventory(inventory)
    }

    fun getMaxHomes(player: Player): Int {
        return when {
            player.hasPermission("home.toprank") -> 5
            player.hasPermission("home.midrank") -> 4
            player.hasPermission("home.shitrank") -> 2
            else -> 1
        }
    }

    private fun createItemMeta(name: String, vararg loreLines: String): ItemMeta? {
        val meta = ItemStack(Material.PAPER).itemMeta
        if (meta != null) {
            meta.setDisplayName(name)
        }

        val loreList = loreLines.toList().filter { it.isNotEmpty() }
        if (meta != null) {
            meta.lore = loreList
        }

        return meta
    }
}