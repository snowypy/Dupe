package codes.snowy.dupeJS.afk

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import isPlayerInAFKRegion
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class AFKRewardTask(private val plugin: DupeJS, private val afkManager: AFKManager) : BukkitRunnable() {

    private var secondsUntilReward: Int = afkManager.getAFKTimer() * 60

    override fun run() {
        if (secondsUntilReward <= 0) {
            val rewardCommand = afkManager.getAFKReward()
            for (player in Bukkit.getOnlinePlayers()) {
                if (isPlayerInAFKRegion(player)) {
                    val shardAmount = when {
                        player.hasPermission("dupey.afk.t1") -> 1
                        player.hasPermission("dupey.afk.t2") -> 2
                        player.hasPermission("dupey.afk.t3") -> 3
                        player.hasPermission("dupey.afk.t4") -> 4
                        player.hasPermission("dupey.afk.t5") -> 5
                        player.hasPermission("dupey.afk.admin") -> 100
                        else -> 1
                    }
                    afkManager.addShards(player.uniqueId, shardAmount)


                }
            }
            secondsUntilReward = afkManager.getAFKTimer() * 60
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                if (isPlayerInAFKRegion(player)) {
                    val message = "&fTime until next reward: &#9436fe&n${secondsUntilReward / 60}m ${secondsUntilReward % 60}s".translate()
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message))
                }
            }
            secondsUntilReward--
        }
    }

    fun startAFKTimer(plugin: DupeJS, afkManager: AFKManager) {
        AFKRewardTask(plugin, afkManager).runTaskTimer(plugin, 0, 20L)
    }
}