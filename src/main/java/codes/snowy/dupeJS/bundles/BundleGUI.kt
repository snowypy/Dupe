package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

object BundleGUI {

    private val ongoingAnimations = mutableMapOf<Player, BukkitTask>()

    fun openBundlePreview(player: Player, bundle: Bundle) {
        val inventory = Bukkit.createInventory(BundleInventoryHolder(bundle, "preview"), 27, "${bundle.color}Preview: ${bundle.displayName} ${bundle.color}Bundle!".translate())

        bundle.items.forEachIndexed { index, item ->
            inventory.setItem(index, item)
        }

        player.openInventory(inventory)
    }

    fun openBundleAnimation(player: Player, bundle: Bundle) {
        val inventory = Bukkit.createInventory(BundleInventoryHolder(bundle, "opening"), 27, "${bundle.color}Opening: ${bundle.displayName} ${bundle.color}Bundle!".translate())
        player.openInventory(inventory)

        val animationTask = object : BukkitRunnable() {
            var ticks = 0
            var selectedItem: ItemStack? = null

            override fun run() {
                if (ticks >= 10) {
                    if (removeBundleItem(player, bundle)) {
                        inventory.clear()
                        selectedItem = bundle.items.random()
                        inventory.setItem(13, selectedItem)
                        selectedItem?.let { player.inventory.addItem(it) }
                    }

                    ongoingAnimations.remove(player)
                    cancel()
                } else {
                    for (i in 0 until inventory.size) {
                        inventory.setItem(i, bundle.items.random())
                    }
                    ticks++
                }
            }
        }.runTaskTimer(DupeJS.getInstance(), 0L, 5L)

        ongoingAnimations[player] = animationTask
    }

     fun removeBundleItem(player: Player, bundle: Bundle): Boolean {
        val inventory = player.inventory

        for (item in inventory.contents) {
            if (item != null && item.hasItemMeta()) {
                val displayName = item.itemMeta?.displayName
                if (displayName == "${bundle.color}&l${bundle.displayName}&r &fBundle".translate()) {
                    item.amount = item.amount - 1
                    if (item.amount <= 0) {
                        inventory.remove(item)
                    }
                    return true
                }
            }
        }
        return false
    }

}
