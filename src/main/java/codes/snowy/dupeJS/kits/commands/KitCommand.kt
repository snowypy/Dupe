package codes.snowy.dupeJS.kits.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.kits.KitGUI
import codes.snowy.dupeJS.kits.KitManager
import org.bukkit.entity.Player
import codes.snowy.dupeJS.utils.translate
import codes.snowy.dupeJS.kits.KitCooldownManager

@CommandAlias("kit")
class KitCommand(
    private val kitManager: KitManager, 
    private val kitGUI: KitGUI,
    private val cooldownManager: KitCooldownManager
) : BaseCommand() {

    @Default
    @CommandCompletion("@kits")
    fun onKit(player: Player, @Optional kitName: String?) {
        if (kitName == null) {
            kitGUI.openKitSelector(player)
            return
        }

        val kit = kitManager.getKit(kitName)
        if (kit == null) {
            player.sendMessage("&#feda36&lKITS &8| &#ff0000Kit not found.".translate())
            return
        }

        if (!player.hasPermission(kit.permission)) {
            player.sendMessage("&#feda36&lKITS &8| &#ff0000You don't have permission to claim this kit.".translate())
            return
        }

        if (!cooldownManager.canUseKit(player.uniqueId, kit)) {
            val remaining = cooldownManager.getRemainingCooldown(player.uniqueId, kit)
            val hours = remaining / (1000 * 60 * 60)
            val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)
            player.sendMessage("&#feda36&lKITS &8| &#ff0000You must wait &#feda36&n${hours}h ${minutes}m&r &#ff0000before using this kit again.".translate())
            return
        }

        kit.items.forEach { item ->
            player.inventory.addItem(item.clone())
        }
        cooldownManager.setKitCooldown(player.uniqueId, kit.name)
        player.sendMessage("&#10f08aYou have claimed the &#feda36${kit.name} &#10f08akit!".translate())
    }
}