package codes.snowy.dupeJS.utils

import co.aikar.commands.PaperCommandManager
import codes.snowy.dupeJS.bundles.BundleManager
import codes.snowy.dupeJS.kits.KitManager
import org.bukkit.ChatColor

class CommandCompletions(private val kitManager: KitManager) {

    fun register(manager: PaperCommandManager) {
        manager.commandCompletions.registerCompletion("colors") {
            ChatColor.values().map { it.name.lowercase() }
        }

        manager.commandCompletions.registerCompletion("bundles") {
            BundleManager.getAllBundleNames()
        }

        manager.commandCompletions.registerCompletion("kits") {
            kitManager.getAllKits().map { it.name }
        }
    }
}
