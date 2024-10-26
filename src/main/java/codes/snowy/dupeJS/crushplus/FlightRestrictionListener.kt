package codes.snowy.dupeJS.crushplus

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FlightRestrictionListener(private val crushPlusManager: CrushPlusManager) : Listener {

    private val staffPermission = "dupejs.staff"

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (player.hasPermission(staffPermission)) return

        if (!crushPlusManager.hasCrushPlus(player)) return

        val from = event.from
        val to = event.to ?: return

        if (from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) return

        val isCurrentlyInSpawn = crushPlusManager.isInSpawnRegion(player)

        if (isCurrentlyInSpawn && !player.allowFlight) {

            player.allowFlight = true
            player.sendMessage("§aFlight has been enabled as you entered the spawn region.")

        } else if (!isCurrentlyInSpawn && player.allowFlight) {

            crushPlusManager.removeFlight(player)

        }
    }
}
