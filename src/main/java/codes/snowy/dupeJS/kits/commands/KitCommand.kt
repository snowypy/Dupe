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
            player.sendMessage("&cThat kit doesn't exist!".translate())
            return
        }

        if (!player.hasPermission(kit.permission)) {
            player.sendMessage("&cYou don't have permission to claim this kit!".translate())
            return
        }

        if (!cooldownManager.canUseKit(player.uniqueId, kit.name)) {
            val remaining = cooldownManager.getRemainingCooldown(player.uniqueId, kit.name)
            val hours = remaining / (1000 * 60 * 60)
            val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)
            player.sendMessage("&cYou must wait &e${hours}h ${minutes}m &cbefore using this kit again!".translate())
            return
        }

        kit.items.forEach { item ->
            player.inventory.addItem(item.clone())
        }
        cooldownManager.setKitCooldown(player.uniqueId, kit.name)
        player.sendMessage("&aYou have claimed the ${kit.name} kit!".translate())
    }
}