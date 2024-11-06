package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.bundles.BundleManager
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

        val bundle = BundleManager.getBundle(bundleName)
        if (bundle != null) {
            event.isCancelled = true
            BundleGUI.openBundleGUI(player, bundle)
        }
    }
}
