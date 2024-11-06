package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object BundleGUI {

    fun openBundlePreview(player: Player, bundle: Bundle) {
        val inventory = Bukkit.createInventory(null, 27, "${bundle.color}Preview: ${bundle.displayName} ${bundle.color}Bundle!".translate())

        bundle.items.forEachIndexed { index, item ->
            inventory.setItem(index, item)
        }

        player.openInventory(inventory)
    }

    fun openBundleAnimation(player: Player, bundle: Bundle) {
        val inventory = Bukkit.createInventory(null, 27, "${bundle.color}Opening: ${bundle.displayName} ${bundle.color}Bundle!".translate())

        player.openInventory(inventory)

        object : BukkitRunnable() {
            var ticks = 0
            override fun run() {
                if (ticks >= 10) {
                    inventory.clear()
                    bundle.items.forEachIndexed { index, item ->
                        inventory.setItem(index, item)
                    }
                    cancel()
                } else {
                    for (i in 0 until inventory.size) {
                        inventory.setItem(i, bundle.items.random())
                    }
                    ticks++
                }
            }
        }.runTaskTimer(DupeJS.getInstance(), 0L, 5L)
    }
}
