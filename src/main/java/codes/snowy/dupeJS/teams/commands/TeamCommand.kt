package codes.snowy.dupeJS.teams.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Subcommand
import co.aikar.commands.annotation.Syntax
import codes.snowy.dupeJS.teams.TeamManager
import codes.snowy.dupeJS.teams.TeamListener
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("team|teams")
class TeamCommand(
    private val teamManager: TeamManager,
    private val teamListener: TeamListener
) : BaseCommand() {

    @Subcommand("create")
    @Syntax("<name> <tag>")
    fun onCreate(player: Player, name: String, tag: String) {
        if (teamManager.getPlayerTeam(player.name) != null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are already in a team.".translate())
            return
        }

        if (name.length > 16) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fTeam name cannot be longer than 16 characters.".translate())
            return
        }

        if (tag.length > 4) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fTeam tag cannot be longer than 4 characters.".translate())
            return
        }

        if (teamManager.createTeam(name, tag, player.name)) {
            player.sendMessage("&#00FF00&lTEAMS &8| &fTeam '&a$name&f' created with tag '&a$tag&f'".translate())
        } else {
            player.sendMessage("&#FF0000&lTEAMS &8| &fA team with that name already exists.".translate())
        }
    }

    @Subcommand("delete")
    fun onDelete(player: Player) {
        val team = teamManager.getPlayerTeam(player.name)
        if (team == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        if (team.owner != player.name) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou must be the team owner to delete it.".translate())
            return
        }

        teamListener.openDeleteConfirmation(player, team.name)
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    fun onInvite(player: Player, target: String) {
        val team = teamManager.getPlayerTeam(player.name)
        if (team == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        val targetPlayer = Bukkit.getPlayer(target)
        if (targetPlayer == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fPlayer not found.".translate())
            return
        }

        if (teamManager.invitePlayer(team.name, player.name, targetPlayer.name)) {
            player.sendMessage("&#00FF00&lTEAMS &8| &fInvited &a${targetPlayer.name}&f to your team.".translate())
            targetPlayer.sendMessage("&#00FF00&lTEAMS &8| &fYou have been invited to join &a${team.name}&f.".translate())
        } else {
            player.sendMessage("&#FF0000&lTEAMS &8| &fCould not invite player to team.".translate())
        }
    }

    @Subcommand("chat")
    @CommandAlias("tc")
    fun onTeamChat(player: Player, message: String) {
        val team = teamManager.getPlayerTeam(player.name)
        if (team == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        val chatFormat = "&8[&b${team.tag}&8] &7${player.name}&f: $message"
        team.members.forEach { member ->
            Bukkit.getPlayer(member)?.sendMessage(chatFormat.translate())
        }
    }

    @Subcommand("settings pvp")
    @CommandCompletion("allow|deny")
    fun onPvpToggle(player: Player, setting: String) {
        val team = teamManager.getPlayerTeam(player.name)
        if (team == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        if (team.owner != player.name) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fOnly the team owner can change settings.".translate())
            return
        }

        val allow = when (setting.lowercase()) {
            "allow" -> true
            "deny" -> false
            else -> {
                player.sendMessage("&#FF0000&lTEAMS &8| &fInvalid setting. Use 'allow' or 'deny'.".translate())
                return
            }
        }

        if (teamManager.toggleTeamPvp(team.name, player.name, allow)) {
            val status = if (allow) "enabled" else "disabled"
            player.sendMessage("&#00FF00&lTEAMS &8| &fTeam PvP has been $status.".translate())
        }
    }

    @Subcommand("lookup")
    @CommandCompletion("@teams")
    fun onLookup(sender: CommandSender, teamName: String) {
        val team = teamManager.getTeamByName(teamName)
        if (team == null) {
            sender.sendMessage("&#FF0000&lTEAMS &8| &fTeam not found.".translate())
            return
        }

        sender.sendMessage("""
            &#00FF00&lTEAM INFO
            &8&l» &fName: &a${team.name}
            &8&l» &fTag: &b${team.tag}
            &8&l» &fOwner: &a${team.owner}
            &8&l» &fMembers &7(${team.members.size})&f: &a${team.members.joinToString("&f, &a")}
            &8&l» &fPvP: ${if (team.allowTeamPvp) "&aEnabled" else "&cDisabled"}
        """.trimIndent().translate())
    }

    @Subcommand("who")
    @CommandCompletion("@players")
    fun onWho(sender: CommandSender, playerName: String) {
        val team = teamManager.getPlayerTeam(playerName)
        if (team == null) {
            sender.sendMessage("&#FF0000&lTEAMS &8| &fPlayer is not in a team.".translate())
            return
        }

        sender.sendMessage("""
            &#00FF00&lPLAYER TEAM INFO
            &8&l» &fPlayer: &a$playerName
            &8&l» &fTeam: &a${team.name}
            &8&l» &fTag: &b${team.tag}
            &8&l» &fRole: ${if (team.owner == playerName) "&6Owner" else "&7Member"}
        """.trimIndent().translate())
    }

    @Subcommand("join")
    @CommandCompletion("@teams_invited")
    fun onJoin(player: Player, teamName: String) {
        if (teamManager.joinTeam(teamName, player.name)) {
            player.sendMessage("&#00FF00&lTEAMS &8| &fYou have joined team '&a$teamName&f'".translate())
        } else {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou cannot join this team. Make sure you have an invite and aren't already in a team.".translate())
        }
    }

    @Subcommand("leave")
    fun onLeave(player: Player) {
        val team = teamManager.getPlayerTeam(player.name)
        if (team == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        if (team.owner == player.name) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou must transfer ownership or delete the team to leave.".translate())
            return
        }

        team.members.remove(player.name)
        teamManager.updateTeam(team)
        teamManager.notifyTeam(team.name, "&c${player.name}&f has left the team")
        player.sendMessage("&#00FF00&lTEAMS &8| &fYou have left the team.".translate())
    }

    @Subcommand("kick")
    @CommandCompletion("@team_members")
    fun onKick(player: Player, target: String) {
        val team = teamManager.getPlayerTeam(player.name)
        if (team == null) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        if (team.owner != player.name) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fOnly the team owner can kick players.".translate())
            return
        }

        if (target == team.owner) {
            player.sendMessage("&#FF0000&lTEAMS &8| &fYou cannot kick the team owner.".translate())
            return
        }

        if (teamManager.kickPlayer(team.name, player.name, target)) {
            player.sendMessage("&#00FF00&lTEAMS &8| &fKicked &c$target&f from the team.".translate())
            val targetPlayer = Bukkit.getPlayer(target)
            targetPlayer?.sendMessage("&#FF0000&lTEAMS &8| &fYou have been kicked from team '&c${team.name}&f'".translate())
        } else {
            player.sendMessage("&#FF0000&lTEAMS &8| &fCould not kick player. Make sure they are in your team.".translate())
        }
    }

    @Subcommand("list")
    fun onList(sender: CommandSender) {
        if (sender !is Player) {
            val allTeams = teamManager.getAllTeamNames()
            if (allTeams.isEmpty()) {
                sender.sendMessage("&#FF0000&lTEAMS &8| &fThere are no teams created yet.".translate())
                return
            }

            sender.sendMessage("""
                &#00FF00&lTEAM LIST &7(${allTeams.size} teams)
                &8&l» &fTeams: &a${allTeams.joinToString("&f, &a")}
            """.trimIndent().translate())
            return
        }

        val team = teamManager.getPlayerTeam(sender.name)
        if (team == null) {
            sender.sendMessage("&#FF0000&lTEAMS &8| &fYou are not in a team.".translate())
            return
        }

        sender.sendMessage("""
            &#00FF00&lTEAM MEMBERS &7(${team.members.size} members)
            &8&l» &fTeam: &a${team.name}
            &8&l» &fTag: &b${team.tag}
            &8&l» &fOwner: &6${team.owner}
            &8&l» &fMembers: &a${team.members.joinToString("&f, &a")}
            &8&l» &fPvP: ${if (team.allowTeamPvp) "&aEnabled" else "&cDisabled"}
        """.trimIndent().translate())
    }
} 