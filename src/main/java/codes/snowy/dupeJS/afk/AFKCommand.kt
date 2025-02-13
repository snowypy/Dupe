package codes.snowy.dupeJS.afk

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.sendError
import codes.snowy.dupeJS.warp.WarpManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("afk")
class AFKCommand(private val warpManager: WarpManager, private val teleportManager: TeleportManager): BaseCommand() {

    @Default
    fun onAfkCommand(player: CommandSender) {
        warpManager.getWarpLocation("afk")?.let {
            warpManager.incrementVisitCount("afk")
            teleportManager.teleportPlayer(player as Player, it, "afk")
            return
        }

        (player as Player).sendError("AFK-HOTCMD-404-WM")

    }

}