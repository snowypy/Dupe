package codes.snowy.dupeJS.afk

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Optional
import co.aikar.commands.annotation.Subcommand
import codes.snowy.dupeJS.afk.AFKManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("adminafk")
class AFKAdminCommand(private val afkManager: AFKManager) : BaseCommand() {

    @Subcommand("settimer")
    @Description("Set the AFK timer in minutes")
    fun onSetTimer(sender: CommandSender, minutes: Int) {
        if (sender is Player && sender.hasPermission("adminafk.manage")) {
            afkManager.setAFKTimer(minutes)
            sender.sendMessage("AFK timer set to $minutes minutes.")
        } else {
            sender.sendMessage("You do not have permission to use this command.")
        }
    }

    @Subcommand("setreward")
    @Description("Set the AFK reward command")
    fun onSetReward(sender: CommandSender, @Optional @Default("command") type: String, vararg command: String) {
        if (sender is Player && sender.hasPermission("adminafk.manage")) {
            if (type.equals("command", ignoreCase = true)) {
                val rewardCommand = command.joinToString(" ")
                afkManager.setAFKReward(rewardCommand)
                sender.sendMessage("AFK reward command set to: $rewardCommand")
            } else {
                sender.sendMessage("Usage: /adminafk setreward command <command>")
            }
        } else {
            sender.sendMessage("You do not have permission to use this command.")
        }
    }
}