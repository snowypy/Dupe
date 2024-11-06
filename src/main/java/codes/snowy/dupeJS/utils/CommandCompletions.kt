package codes.snowy.dupeJS.utils

import co.aikar.commands.PaperCommandManager
import codes.snowy.dupeJS.bundles.BundleManager
import org.bukkit.ChatColor

object CommandCompletions {

    fun register(manager: PaperCommandManager) {
        manager.commandCompletions.registerCompletion("colors") {
            ChatColor.values().map { it.name.lowercase() }
        }

        manager.commandCompletions.registerCompletion("bundles") {
            BundleManager.getAllBundleNames()
        }
    }
}
