package codes.snowy.dupeJS.kits.commands;

import Kit
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.kits.KitGUI
import codes.snowy.dupeJS.kits.KitManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

@CommandAlias("kiteditor")
@CommandPermission("dupe.kitadmin")
class KitEditorCommand(private val kitManager: KitManager, private val kitGUI: KitGUI) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Subcommand("edit")
    @CommandCompletion("@kits")
    fun onKitEdit(player: Player, kitName: String) {
        val kit = kitManager.getKit(kitName)
        if (kit == null) {
            player.sendMessage("&cKit not found!".translate())
            return
        }
        kitGUI.openKitEditor(player, kitName)
    }

    @Subcommand("create")
    fun onKitCreate(player: Player, name: String, permission: String, displayItem: String) {
        if (kitManager.getKit(name) != null) {
            player.sendMessage("&cA kit with that name already exists!".translate())
            return
        }
        kitGUI.openKitEditor(player, name)
        player.sendMessage("&aPlace items in the GUI to create your kit.".translate())
        player.sendMessage("&aThe kit will be saved when you close the GUI.".translate())
    }

    @Subcommand("delete")
    @CommandCompletion("@kits")
    fun onKitDelete(player: Player, kitName: String) {
        val kit = kitManager.getKit(kitName)
        if (kit == null) {
            player.sendMessage("&cKit not found!".translate())
            return
        }
        kitManager.deleteKit(kitName)
        player.sendMessage("&aKit deleted successfully!".translate())
    }
} 