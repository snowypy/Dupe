package codes.snowy.dupeJS.warp

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Syntax
import co.aikar.commands.annotation.Optional
import codes.snowy.dupeJS.teleporter.TeleportManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Location

@CommandAlias("warp")
class WarpCommand(private val warpGUI: WarpGUI, private val warpManager: WarpManager, private val teleportManager: TeleportManager) : BaseCommand() {

    @Default
    @Syntax("[warp]")
    @CommandCompletion("@warps")
    fun onWarpCommand(sender: CommandSender, @Optional warpName: String?) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        if (warpName == null) {
            warpGUI.open(sender)
            return
        }

        val normalizedWarpName = warpName.lowercase()
        val warp = warpManager.getAllWarpNames().find { it.lowercase() == normalizedWarpName }

        if (warp != null) {
            val location = warpManager.getWarpLocation(warp)
            if (location != null) {
                warpManager.incrementVisitCount(warp)
                teleportManager.teleportPlayer(sender, location, warp)
            } else {
                sender.sendMessage("&cWarp location not found.".translate())
            }
        } else {
            sender.sendMessage("&cWarp location not found.".translate())
        }
    }
} 