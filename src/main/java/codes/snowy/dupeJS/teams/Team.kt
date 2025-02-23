package codes.snowy.dupeJS.teams

data class Team(
    val name: String,
    val tag: String,
    var owner: String,
    val members: MutableSet<String> = mutableSetOf(),
    val invites: MutableSet<String> = mutableSetOf(),
    var allowTeamPvp: Boolean = false
) 