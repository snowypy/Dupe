package codes.snowy.dupeJS.staff.vanish


import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

object VanishManager {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)
    private val vanishedPlayers = mutableSetOf<Player>()
    private val bossBarMap = mutableMapOf<Player, BossBar>()

    init {
        object : BukkitRunnable() {
            override fun run() {
                if (config.getBoolean("options.vanish.action-bar", true)) {
                    for (player in vanishedPlayers) {
                        val actionBarMessage = language.getReplacedMessage("staff.vanish.action-bar")
                        sendActionBar(player, actionBarMessage)
                        updateBossBar(player)
                    }
                }
            }
        }.runTaskTimerAsynchronously(DupeJS.getInstance(), 0L, 20L)
    }

    fun toggleVanish(player: Player): Boolean {
        return if (vanishedPlayers.contains(player)) {
            vanishedPlayers.remove(player)
            removeBossBar(player)
            false
        } else {
            vanishedPlayers.add(player)
            showBossBar(player)
            true
        }
    }

    fun isVanished(player: Player): Boolean {
        return vanishedPlayers.contains(player)
    }

    private fun sendActionBar(player: Player, message: String) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(message.translate()))
    }

    private fun showBossBar(player: Player) {
        val bossBar = Bukkit.createBossBar(
            language.getReplacedMessage("staff.vanish.boss-bar").translate(),
            BarColor.GREEN,
            BarStyle.SOLID
        )
        bossBar.progress = 1.0

        bossBar.addPlayer(player)
        bossBarMap[player] = bossBar
    }

    private fun updateBossBar(player: Player) {
        bossBarMap[player]?.let {
            it.setTitle(language.getReplacedMessage("staff.vanish.boss-bar").translate())
        }
    }

    private fun removeBossBar(player: Player) {
        bossBarMap[player]?.let {
            it.removePlayer(player)
            bossBarMap.remove(player)
        }
    }

    fun getVanishedPlayers(): Set<Player> {
        return vanishedPlayers
    }

}