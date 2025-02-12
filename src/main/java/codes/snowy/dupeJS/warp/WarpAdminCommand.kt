package codes.snowy.dupeJS.warp

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.NamespacedKey
import de.tr7zw.changeme.nbtapi.NBTItem

@CommandAlias("warpadmin")
@CommandPermission("dupe.warpadmin")
class WarpAdminCommand(private val warpDatabase: WarpDatabase) : BaseCommand() {

    @Subcommand("set")
    @Syntax("<preview item>|<real name or text id>|<preview item display name>")
    fun onSetWarp(sender: CommandSender, args: String) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        val parts = args.split("|")
        if (parts.size != 3) {
            sender.sendMessage("&cInvalid syntax. Use: /warpadmin set <preview item>|<real name or text id>|<preview item display name>".translate())
            return
        }

        val previewItem = parts[0].uppercase()
        val warpName = parts[1]
        val displayName = parts[2]

        try {
            Material.valueOf(previewItem)
        } catch (e: IllegalArgumentException) {
            sender.sendMessage("&cInvalid preview item material.".translate())
            return
        }

        val location = sender.location

        val itemStack = ItemStack(Material.valueOf(previewItem))
        val nbtItem = NBTItem(itemStack)
        nbtItem.setString("warpName", warpName)

        warpDatabase.addWarp(warpName, location, previewItem, displayName)

        sender.sendMessage("&aWarp '$warpName' set with preview item '$previewItem' and display name '$displayName'.".translate())
    }
} 