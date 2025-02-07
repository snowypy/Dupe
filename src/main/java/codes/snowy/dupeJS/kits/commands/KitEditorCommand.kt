package codes.snowy.dupeJS.kits.commands;

import Kit
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.kits.KitGUI
import codes.snowy.dupeJS.kits.KitManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import org.bukkit.Material

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
            player.sendMessage("&#feda36&lKITS &8| &#ff0000Kit not found.".translate())
            return
        }
        kitGUI.openKitEditor(player, kitName)
    }

    @Subcommand("create")
    @Syntax("<name> <permission> <displayItem> <cooldownHours>")
    @Description("Create a new kit")
    fun onKitCreate(player: Player, name: String, permission: String, displayItem: String, cooldownHours: Int) {
        if (kitManager.getKit(name) != null) {
            player.sendMessage("&#feda36&lKITS &8| &#ff0000A kit with that name already exists.".translate())
            return
        }

        try {
            Material.valueOf(displayItem.uppercase())
        } catch (e: IllegalArgumentException) {
            player.sendMessage("&#feda36&lKITS &8| &#ff0000Invalid display item material.".translate())
            return
        }

        if (cooldownHours < 0) {
            player.sendMessage("&#feda36&lKITS &8| &#ff0000Cooldown hours must be 0 or greater.".translate())
            return
        }

        val emptyKit = Kit(
            name = name,
            permission = permission,
            displayItem = displayItem.uppercase(),
            items = emptyList(),
            cooldownHours = cooldownHours
        )
        kitManager.saveKit(emptyKit)
        
        kitGUI.openKitEditor(player, name)
        player.sendMessage("&#feda36&lKITS &8| &#10f08aPlace items in the GUI to create your kit.".translate())
        player.sendMessage("&#feda36&lKITS &8| &#10f08aThe kit will be saved when you close the GUI.".translate())
    }

    @Subcommand("delete")
    @CommandCompletion("@kits")
    fun onKitDelete(player: Player, kitName: String) {
        val kit = kitManager.getKit(kitName)
        if (kit == null) {
            player.sendMessage("&#feda36&lKITS &8| &#ff0000Kit not found.".translate())
            return
        }
        kitManager.deleteKit(kitName)
        player.sendMessage("&#feda36&lKITS &8| &#10f08aKit &#feda36&n${kitName}&r &#10f08adeleted successfully.".translate())
    }
} 