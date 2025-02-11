package codes.snowy.dupeJS.missions.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import codes.snowy.dupeJS.missions.MissionGUI
import codes.snowy.dupeJS.missions.MissionManager
import codes.snowy.dupeJS.missions.RewardSystem
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player

@CommandAlias("mission")
class MissionCommand(
    private val missionManager: MissionManager,
    private val missionGUI: MissionGUI,
    private val rewardSystem: RewardSystem
) : BaseCommand() {

    @Default
    @Subcommand("view")
    @Description("View your current missions")
    fun onView(player: Player) {
        missionManager.ensureMissionsAssigned(player)
        missionGUI.openMissionSelector(player)
    }

    @Subcommand("stats")
    @Description("View your mission stats")
    fun onStats(player: Player) {
        missionManager.ensureMissionsAssigned(player)
        missionGUI.openMissionStats(player)
    }

    @Subcommand("claim")
    @Description("Claim your mission rewards")
    fun onClaim(player: Player) {
        missionManager.ensureMissionsAssigned(player)
        if (missionManager.checkMissionCompletion(player)) {
            rewardSystem.spinRewardWheel(player)
        } else {
            player.sendMessage("&#feda36&lMISSIONS &8| &#FF0000You have no completed missions to claim rewards.".translate())
        }
    }
} 