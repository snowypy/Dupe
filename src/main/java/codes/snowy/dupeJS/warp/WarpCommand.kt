package codes.snowy.dupeJS.warp

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Syntax
import codes.snowy.dupeJS.teleporter.TeleportManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Location

@CommandAlias("warp")
class WarpCommand(private val warpGUI: WarpGUI, private val warpManager: WarpManager, private val teleportManager: TeleportManager) : BaseCommand() {


    

    @Default
    @Syntax("<warp>")
    @CommandCompletion("@warps")
    fun onWarpCommand(sender: CommandSender, warpName: String) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        if (warpName.isEmpty()) {
            warpGUI.open(sender)
            return
        }

        val normalizedWarpName = warpName.lowercase()

        val allWarps = warpManager.getAllWarpNames().map { it.lowercase() }
        val index = allWarps.indexOf(normalizedWarpName)

        if (index != -1) {
            val location = warpManager.getWarpLocation(allWarps[index])
            if (location != null) {
                warpManager.incrementVisitCount(allWarps[index])
                teleportManager.teleportPlayer(sender, location, allWarps[index])
            } else {
                sender.sendMessage("&cWarp location not found.".translate())
            }
        } else {
            sender.sendMessage("&cWarp location not found.".translate())
        }
    }
} 