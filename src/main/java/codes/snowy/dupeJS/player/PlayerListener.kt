package codes.snowy.dupeJS.player

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerListener(private val playerManager: PlayerManager) : Listener {
    private val sessionStartTimes = ConcurrentHashMap<UUID, Long>()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        playerManager.initializePlayer(player)
        playerManager.updateLastJoin(player.uniqueId)
        sessionStartTimes[player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val startTime = sessionStartTimes.remove(player.uniqueId) ?: return
        val sessionDuration = System.currentTimeMillis() - startTime
        playerManager.updatePlaytime(player.uniqueId, sessionDuration)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val victim = event.entity
        val killer = victim.killer

        playerManager.addDeath(victim.uniqueId)
        if (killer != null) {
            playerManager.addKill(killer.uniqueId)
        }
    }
} 