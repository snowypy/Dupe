package codes.snowy.dupeJS.teams

import codes.snowy.dupeJS.teams.Team
import codes.snowy.dupeJS.utils.translate
import java.sql.Connection
import java.sql.DriverManager
import org.bukkit.Bukkit

class TeamManager {
    private val dbConnection: Connection
    private val teams = mutableMapOf<String, Team>()

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/teams.db")
        createTables()
        loadTeams()
    }

    private fun createTables() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS teams (
                name TEXT PRIMARY KEY,
                tag TEXT,
                owner TEXT,
                members TEXT,
                invites TEXT,
                allow_team_pvp BOOLEAN DEFAULT 0
            )
        """)
        statement.close()
    }

    private fun loadTeams() {
        val statement = dbConnection.prepareStatement("SELECT * FROM teams")
        val resultSet = statement.executeQuery()

        while (resultSet.next()) {
            val team = Team(
                name = resultSet.getString("name"),
                tag = resultSet.getString("tag"),
                owner = resultSet.getString("owner"),
                members = resultSet.getString("members").split(",").toMutableSet(),
                invites = resultSet.getString("invites").split(",").toMutableSet(),
                allowTeamPvp = resultSet.getBoolean("allow_team_pvp")
            )
            teams[team.name.lowercase()] = team
        }

        resultSet.close()
        statement.close()
    }

    fun createTeam(name: String, tag: String, owner: String): Boolean {
        if (teams.containsKey(name.lowercase())) {
            return false
        }

        val team = Team(name, tag, owner)
        team.members.add(owner)
        teams[name.lowercase()] = team

        val statement = dbConnection.prepareStatement(
            "INSERT INTO teams (name, tag, owner, members, invites) VALUES (?, ?, ?, ?, ?)"
        )
        statement.setString(1, name)
        statement.setString(2, tag)
        statement.setString(3, owner)
        statement.setString(4, owner)
        statement.setString(5, "")
        statement.executeUpdate()
        statement.close()

        return true
    }

    fun deleteTeam(name: String, requester: String): Boolean {
        val team = teams[name.lowercase()] ?: return false
        if (team.owner != requester) return false

        notifyTeam(name, "The team has been disbanded by ${team.owner}")
        teams.remove(name.lowercase())
        val statement = dbConnection.prepareStatement("DELETE FROM teams WHERE name = ?")
        statement.setString(1, name)
        statement.executeUpdate()
        statement.close()
        return true
    }

    fun invitePlayer(teamName: String, inviter: String, invitee: String): Boolean {
        val team = teams[teamName.lowercase()] ?: return false
        if (!team.members.contains(inviter)) return false
        if (team.members.contains(invitee)) return false

        team.invites.add(invitee)
        updateTeam(team)
        return true
    }

    fun getPlayerTeam(player: String): Team? {
        return teams.values.find { it.members.contains(player) }
    }

    fun updateTeam(team: Team) {
        val statement = dbConnection.prepareStatement(
            "UPDATE teams SET members = ?, invites = ?, allow_team_pvp = ? WHERE name = ?"
        )
        statement.setString(1, team.members.joinToString(","))
        statement.setString(2, team.invites.joinToString(","))
        statement.setBoolean(3, team.allowTeamPvp)
        statement.setString(4, team.name)
        statement.executeUpdate()
        statement.close()
    }

    fun transferOwnership(teamName: String, newOwner: String): Boolean {
        val team = teams[teamName.lowercase()] ?: return false
        if (!team.members.contains(newOwner)) return false

        val oldOwner = team.owner
        team.owner = newOwner
        updateTeam(team)
        notifyTeam(teamName, "Team ownership has been transferred from &a$oldOwner&f to &a$newOwner")
        return true
    }

    fun forceDeleteTeam(teamName: String): Boolean {
        val team = teams[teamName.lowercase()] ?: return false
        
        notifyTeam(teamName, "The team has been forcefully disbanded by an administrator")
        teams.remove(teamName.lowercase())
        val statement = dbConnection.prepareStatement("DELETE FROM teams WHERE name = ?")
        statement.setString(1, teamName)
        statement.executeUpdate()
        statement.close()
        return true
    }

    fun toggleTeamPvp(teamName: String, requester: String, allow: Boolean): Boolean {
        val team = teams[teamName.lowercase()] ?: return false
        if (team.owner != requester) return false

        team.allowTeamPvp = allow
        updateTeam(team)
        return true
    }

    fun getTeamByName(name: String): Team? {
        return teams[name.lowercase()]
    }

    fun getAllTeamNames(): List<String> {
        return teams.values.map { it.name }
    }

    fun joinTeam(teamName: String, player: String): Boolean {
        val team = teams[teamName.lowercase()] ?: return false
        if (!team.invites.contains(player)) return false
        if (getPlayerTeam(player) != null) return false

        team.members.add(player)
        team.invites.remove(player)
        updateTeam(team)
        notifyTeam(teamName, "&a$player&f has joined the team")
        return true
    }

    fun getPlayerInvites(player: String): List<String> {
        return teams.values.filter { it.invites.contains(player) }.map { it.name }
    }

    fun getTeamMembers(teamName: String): List<String> {
        return teams[teamName.lowercase()]?.members?.toList() ?: emptyList()
    }

    fun notifyTeam(teamName: String, message: String) {
        val team = teams[teamName.lowercase()] ?: return
        team.members.forEach { member ->
            Bukkit.getPlayer(member)?.sendMessage("&#00FF00&lTEAMS &8| &f$message".translate())
        }
    }

    fun kickPlayer(teamName: String, kicker: String, target: String): Boolean {
        val team = teams[teamName.lowercase()] ?: return false
        if (team.owner != kicker) return false
        if (!team.members.contains(target)) return false
        if (target == team.owner) return false

        team.members.remove(target)
        updateTeam(team)
        notifyTeam(teamName, "&c$target&f has been kicked from the team")
        return true
    }
} 