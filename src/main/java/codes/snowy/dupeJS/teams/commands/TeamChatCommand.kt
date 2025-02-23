package codes.snowy.dupeJS.teams.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import codes.snowy.dupeJS.teams.TeamManager
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player


@Subcommand("teamchat|tc|tchat|teamc")
class TeamChatCommand(private val teamManager: TeamManager) : BaseCommand() {
    
    @Default
    @CommandCompletion("<message>")
    @Syntax("<message>")
    fun onCommand(sender: Player, args: Array<String>) {
        val team = teamManager.getPlayerTeam(sender.name)
        if (team == null) {
            sender.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        val message = args.joinToString(" ")
        val messageFinal = ChatColor.stripColor(message)
        val chatFormat = "&b&lTEAM CHAT &8| &f${sender.name}&f: &7$messageFinal"
        team.members.forEach { member ->
            Bukkit.getPlayer(member)?.sendMessage(chatFormat.translate())
        }
    }
}