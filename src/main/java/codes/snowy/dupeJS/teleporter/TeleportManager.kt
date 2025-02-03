package codes.snowy.dupeJS.teleporter

import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class TeleportManager(private val plugin: JavaPlugin) {

    private val config = Config(plugin)
    private val language = Language(plugin, config)

    fun teleportPlayer(player: Player, location: Location, name: String = "location") {
        val start = player.location
        val countdownSeconds = 5

        player.playSound(player.location, "block.note_block.pling", 1f, 1f)
        player.sendMessage("&#10f08a&lTELEPORTER &8| &#c4f5dfYou will be teleported to \"$name\" in $countdownSeconds seconds...".translate())

        object : BukkitRunnable() {
            var timeLeft = countdownSeconds

            override fun run() {

                if (playerHasMoved(player, start)) {
                    player.playSound(player.location, "entity.enderman.death", 1f, 1f)
                    player.sendMessage("&#10f08a&lTELEPORTER &8| &#f9445dYou moved! Teleportation cancelled.".translate())
                    cancel()
                    return
                }

                if (timeLeft <= 1) {
                    player.sendMessage("&#10f08a&lTELEPORTER &8| &#c4f5dfYou have been teleported to \"$name\"!".translate())
                    player.teleport(location)
                    player.playSound(player.location, "entity.enderman.teleport", 1f, 1f)
                    cancel()
                } else {
                    timeLeft -= 1
                    player.playSound(player.location, "block.note_block.pling", 1f, 1f)
                    player.sendMessage("&#10f08a&lTELEPORTER &8| &#c4f5dfYou will be teleported to \"$name\" in $timeLeft seconds...".translate())
                }
            }
        }.runTaskTimer(plugin, 20, 20)
    }

    fun tpaTeleport(acceptee: Player, location: Location, sender: Player) {
        // player: The person who accepted a /tpa request
        // location: The location to teleport the target to
        // target: The person who ran /tpa <player>

        val start = sender.location
        val accepteeLoc = acceptee.location
        val countdownSeconds = 5

        acceptee.playSound(acceptee.location, "block.note_block.pling", 1f, 1f)
        acceptee.sendMessage(language.getMessages("tpa.teleport-countdown")
            .replace("%target%", sender.name)
            .replace("%cooldown%", countdownSeconds.toString())
            .translate())

        object : BukkitRunnable() {
            var timeLeft = countdownSeconds

            override fun run() {
                if (playerHasMoved(acceptee, accepteeLoc)) {
                    acceptee.playSound(acceptee.location, "entity.enderman.death", 1f, 1f)
                    acceptee.sendMessage(language.getMessages("tpa.teleport-countdown-cancel").translate())
                    sender.sendMessage(language.getMessages("tpa.teleport-countdown-cancel-other")
                        .replace("%target%", acceptee.name)
                        .translate())
                    cancel()
                    return
                }

                if (timeLeft <= 1) {
                    acceptee.sendMessage(language.getMessages("tpa.teleport-complete")
                        .replace("%target%", sender.name)
                        .translate())
                    sender.sendMessage(language.getMessages("tpa.teleport-complete-other")
                        .replace("%target%", acceptee.name)
                        .translate())
                    acceptee.teleport(location)
                    acceptee.playSound(acceptee.location, "entity.enderman.teleport", 1f, 1f)
                    sender.playSound(sender.location, "entity.enderman.teleport", 1f, 1f)
                    cancel()
                } else {
                    timeLeft -= 1
                    acceptee.playSound(acceptee.location, "block.note_block.pling", 1f, 1f)
                    sender.playSound(sender.location, "block.note_block.pling", 1f, 1f)
                    acceptee.sendMessage(language.getMessages("tpa.teleport-countdown")
                        .replace("%target%", sender.name)
                        .replace("%cooldown%", timeLeft.toString())
                        .translate())
                    sender.sendMessage(language.getMessages("tpa.teleport-countdown-other")
                        .replace("%target%", acceptee.name)
                        .replace("%cooldown%", timeLeft.toString())
                        .translate())
                }
            }
        }.runTaskTimer(plugin, 20, 20)
    }

    private fun playerHasMoved(player: Player, start: Location): Boolean {
        return player.location.blockX != start.blockX || player.location.blockZ != start.blockZ
    }
}
