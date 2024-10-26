package codes.snowy.dupeJS.crushplus

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FlightRestrictionListener(private val crushPlusManager: CrushPlusManager) : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player

        if (crushPlusManager.hasStaffPermission(player)) return

        if (!crushPlusManager.hasCrushPlus(player)) return

        val from = event.from
        val to = event.to ?: return

        if (from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) return

        val isCurrentlyInSpawn = crushPlusManager.isInSpawnRegion(player)

        if (isCurrentlyInSpawn && !player.allowFlight) {

            player.allowFlight = true

        } else if (!isCurrentlyInSpawn && player.allowFlight) {

            crushPlusManager.removeFlight(player)

        }
    }
}
