package codes.snowy.dupeJS.teams

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class TeamPvPListener(private val teamManager: TeamManager) : Listener {

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val damaged = event.entity as? Player ?: return
        val damager = event.damager as? Player ?: return

        val damagedTeam = teamManager.getPlayerTeam(damaged.name)
        val damagerTeam = teamManager.getPlayerTeam(damager.name)

        if (damagedTeam != null && damagerTeam != null && damagedTeam.name == damagerTeam.name && !damagedTeam.allowTeamPvp) {
            event.isCancelled = true
        }
    }
} 