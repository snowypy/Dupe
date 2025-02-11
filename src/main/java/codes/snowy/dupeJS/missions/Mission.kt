package codes.snowy.dupeJS.missions

data class Mission(
    val type: String,
    val progress: Int,
    val target: Int,
    val lastUpdated: Long
) 