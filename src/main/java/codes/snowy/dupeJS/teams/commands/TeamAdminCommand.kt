package codes.snowy.dupeJS.teams.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.teams.TeamManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("teamadmin")
@CommandPermission("dupe.teamadmin")
class TeamAdminCommand(private val teamManager: TeamManager) : BaseCommand() {

    @Subcommand("delete")
    @CommandCompletion("@teams")
    fun onDelete(sender: CommandSender, teamName: String) {
        if (teamManager.forceDeleteTeam(teamName)) {
            sender.sendMessage("&#00FF00&lTEAMS &8| &fForce deleted team '&a$teamName&f'".translate())
        } else {
            sender.sendMessage("&#FF0000&lTEAMS &8| &fTeam not found.".translate())
        }
    }

    @Subcommand("transfer")
    @CommandCompletion("@teams @players")
    fun onTransfer(sender: CommandSender, teamName: String, newOwner: String) {
        if (teamManager.transferOwnership(teamName, newOwner)) {
            sender.sendMessage("&#00FF00&lTEAMS &8| &fTransferred ownership of '&a$teamName&f' to &a$newOwner".translate())
        } else {
            sender.sendMessage("&#FF0000&lTEAMS &8| &fCould not transfer ownership. Check if team exists and player is a member.".translate())
        }
    }
} 