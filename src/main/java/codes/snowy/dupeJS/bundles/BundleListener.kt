package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.bundles.BundleManager
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.entity.Player

class BundleListener : Listener {
    @EventHandler
    fun onBundleOpen(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val bundleName = item.itemMeta?.displayName ?: return
        val bundleNames = ChatColor.stripColor(bundleName).toString()
        val bundleStripped = bundleNames.replace(" Bundle", "")

        val bundle = BundleManager.getBundle(bundleStripped)
        if (bundle != null) {
            event.isCancelled = true
            BundleGUI.openBundleAnimation(player, bundle)
        }
    }
}
