package codes.snowy.dupeJS.missions.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import codes.snowy.dupeJS.missions.MissionManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

@CommandAlias("resetmissions")
@CommandPermission("dupe.admin")
class ForceResetMissionsCommand(private val missionManager: MissionManager) : BaseCommand() {

    private val notifiedPlayers = mutableSetOf<UUID>()

    @Subcommand("all")
    @Description("Force reset all players' missions")
    fun onResetAll(sender: CommandSender) {
        if (sender.hasPermission("dupe.admin")) {
            missionManager.resetDailyMissions()
            missionManager.resetWeeklyMissions()
            sender.sendMessage("&#feda36&lMISSIONS &8| &fAll players' missions have been reset.".translate())
            notifiedPlayers.clear()
        } else {
            sender.sendMessage("&#feda36&lMISSIONS &8| &#FF0000You do not have permission to use this command.".translate())
        }
    }
} 