package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.concurrent.ConcurrentHashMap

@CommandAlias("shout")
@CommandPermission("dupe.shout")
class ShoutCommand: BaseCommand() {

    private val luckPerms: LuckPerms = LuckPermsProvider.get()
    private val cooldowns = ConcurrentHashMap<String, Long>()
    val cooldownTime = 15 * 60 * 1000

    @Default
    @CommandPermission("<message>")
    fun onCommand(sender: CommandSender, message: String) {
        val currentTime = System.currentTimeMillis() // Update currentTime here

        if (message.isEmpty()) {
            sender.sendMessage("&cPlease provide a message to shout.".translate())
            return
        }

        val player = Bukkit.getPlayer(sender.name)
        val user: User = player?.uniqueId?.let { luckPerms.userManager.getUser(it) } ?: return
        val prefix = user.cachedData.metaData.prefix ?: ""

        if (cooldowns.containsKey(player?.name)) {
            val lastUsed = cooldowns[player?.name]!!
            if (currentTime - lastUsed < cooldownTime) {
                val timeLeft = (cooldownTime - (currentTime - lastUsed)) / 1000
                sender.sendMessage("&cYou must wait $timeLeft seconds before using this command again.".translate())
                return
            }
        }

        cooldowns[player.name] = currentTime

        val messageFormatted = ChatColor.stripColor(message).toString()

        org.bukkit.Bukkit.broadcastMessage("".translate())
        org.bukkit.Bukkit.broadcastMessage("&#f81e2b&lSHOUT &8| ${prefix}${player.name}&f: &f${messageFormatted}".translate())
        org.bukkit.Bukkit.broadcastMessage("".translate())
        org.bukkit.Bukkit.getOnlinePlayers().forEach {
            it.playSound(it.location, "block.note_block.pling", 0.1f, 0.1f)
        }
    }
}