package codes.snowy.dupeJS.missions

import codes.snowy.dupeJS.DupeJS
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class MissionListener(private val missionManager: MissionManager) : Listener {

    private val playerPlaytime = ConcurrentHashMap<UUID, Long>()

    init {
        object : BukkitRunnable() {
            override fun run() {
                playerPlaytime.forEach { (uuid, time) ->
                    missionManager.updateMissionProgress(Bukkit.getPlayer(uuid) ?: return@forEach, "Playtime", 1)
                }
            }
        }.runTaskTimer(DupeJS.getInstance(), 0L, 1200L)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        missionManager.updateMissionProgress(player, "Break Blocks", 1)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        playerPlaytime[event.player.uniqueId] = System.currentTimeMillis()
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val playerUUID = event.player.uniqueId
        val joinTime = playerPlaytime.remove(playerUUID) ?: return
        val playtimeMinutes = (System.currentTimeMillis() - joinTime) / 60000
        missionManager.updateMissionProgress(event.player, "Playtime", playtimeMinutes.toInt())
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        if (killer is Player) {
            if (event.entity.type == EntityType.PLAYER) {
                missionManager.updateMissionProgress(killer, "Kill Players", 1)
            } else {
                missionManager.updateMissionProgress(killer, "Kill Mobs", 1)
            }
        }
    }
} 