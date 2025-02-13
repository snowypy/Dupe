package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.sendError
import codes.snowy.dupeJS.warp.WarpManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("koth")
class KoTHCommand(private val warpManager: WarpManager, private val teleportManager: TeleportManager): BaseCommand() {

    @Default
    fun onKoTHCommand(player: CommandSender) {
        warpManager.getWarpLocation("koth")?.let {
            warpManager.incrementVisitCount("koth")
            teleportManager.teleportPlayer(player as Player, it, "koth")
            return
        }

        (player as Player).sendError("KOTH-HOTCMD-404-WM")

    }

}