package codes.snowy.dupeJS.bundles

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.applyRainbowText
import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBTItem
import formatMaterial
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("adminbundle")
@CommandPermission("admin.bundle")
class AdminBundleCommand(private val config: Config) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Subcommand("create")
    @Syntax("<name> <color> <displayName>")
    @CommandCompletion("<name> @colors <displayName>")
    fun onCreate(sender: Player, @Single name: String, color: String, @Single displayName: String) {
        val bundle = Bundle(name, color, displayName, sender.inventory.filterNotNull().toList())
        BundleManager.saveBundle(bundle)
        sender.sendMessage("&#FAAAAA&lBUNDLES &8| &aCreated the $color&n$displayName&f Bundle".translate())
    }

    @Subcommand("give")
    @Syntax("<target> <bundle>")
    @CommandCompletion("@players @bundles")
    fun onGive(sender: Player, @Flags("other") target: Player, @Single bundleName: String) {
        val bundle = BundleManager.getBundle(bundleName) ?: run {
            sender.sendMessage("&#FAAAAA&lBUNDLES &8| &#FF0000Bundle not found.")
            return
        }

        val bundleItem = ItemStack(Material.BARREL).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("${bundle.color}&l${bundle.displayName}&r &fBundle".translate())

                val lore = mutableListOf<String>()
                lore.add("")
                lore.add("&fThis Bundle will give you ${bundle.color}&n1 Reward&f".translate())
                lore.add("&fYou can only open this bundle once.".translate())
                lore.add("")

                bundle.items.forEachIndexed { index, item ->
                    lore.add(" ${bundle.color}&l| &f${applyRainbowText(item.type.name.replace("_", " ").formatMaterial())}".translate())
                }

                lore.add("")
                lore.add("&7Given to: ${bundle.color}${target.name}".translate())
                lore.add("&7Get more @ ${bundle.color}/store".translate())
                lore.add("")
                lore.add("&a[Click to Open]".translate())

                setLore(lore)
            }
        }
        val nbtItem = NBTItem(bundleItem)
        nbtItem.setInteger("custom_model_data", config.getInt("dupe.modeldata", 1111))

        target.inventory.addItem(nbtItem.item)
        sender.sendMessage("&#FAAAAA&lBUNDLES &8| &aGave ${bundle.color}$&n{bundle.displayName}&a to ${target.name}.".translate())
    }

}

