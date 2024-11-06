package codes.snowy.dupeJS.bundles

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("adminbundle")
@CommandPermission("admin.bundle")
class AdminBundleCommand : BaseCommand() {

    @Subcommand("create")
    @CommandCompletion("@nothing @colors @displayNames")
    fun onCreate(sender: Player, @Single name: String, color: String, @Single displayName: String) {
        val bundle = Bundle(name, color, displayName, sender.inventory.filterNotNull().toList())
        BundleManager.saveBundle(bundle)
        sender.sendMessage("Bundle $displayName created with color $color!")
    }

    @Subcommand("give")
    @CommandCompletion("@players @bundles")
    fun onGive(sender: Player, @Flags("other") target: Player, @Single bundleName: String) {
        val bundle = BundleManager.getBundle(bundleName) ?: run {
            sender.sendMessage("Bundle not found.")
            return
        }

        val bundleItem = ItemStack(Material.BARREL).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("${bundle.color}${bundle.displayName}&r &fBundle".translate())
            }
        }
        target.inventory.addItem(bundleItem)
        sender.sendMessage("Gave ${bundle.displayName} to ${target.name}.")
    }
}

