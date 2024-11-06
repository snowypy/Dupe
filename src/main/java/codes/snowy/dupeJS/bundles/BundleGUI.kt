package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.DupeJS
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object BundleGUI {
    fun openBundleGUI(player: Player, bundle: Bundle) {
        val inventory = Bukkit.createInventory(null, 27, "Opening ${bundle.name}")

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

        player.openInventory(inventory)
    }

    fun openBundlePreview(player: Player, bundle: Bundle) {
        val inventory = Bukkit.createInventory(null, 27, "Preview: ${bundle.name}")

        bundle.items.forEachIndexed { index, item ->
            inventory.setItem(index, item)
        }

        player.openInventory(inventory)
    }

}
