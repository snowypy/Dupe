package codes.snowy.dupeJS.player

import codes.snowy.dupeJS.utils.DatabaseHelper
import codes.snowy.dupeJS.utils.Logger
import codes.snowy.dupeJS.utils.translate
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerListener(private val playerManager: PlayerManager, private val dbHelper: DatabaseHelper) : Listener {
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
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        val spawnLocation = dbHelper.getSpawn() ?: player.world.spawnLocation
        if (dbHelper.getSpawn() == null) {
            Logger.log("&cSpawn location not set. Using world spawn.".translate(), "warning")
        }
        event.respawnLocation = spawnLocation
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