package codes.snowy.dupeJS.missions

import java.util.concurrent.TimeUnit

data class Mission(
    val type: String,
    val missionType: String,
    val progress: Int,
    val target: Int,
    val lastUpdated: Long,
    val frequency: String
) {
    fun timeLeft(): String {
        val currentTime = System.currentTimeMillis()
        val duration = when (frequency) {
            "daily" -> 24 * 60 * 60 * 1000
            "weekly" -> 7 * 24 * 60 * 60 * 1000
            else -> 0
        }
        val timeLeft = duration - (currentTime - lastUpdated)
        val days = TimeUnit.MILLISECONDS.toDays(timeLeft)
        val hours = TimeUnit.MILLISECONDS.toHours(timeLeft) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60
        return "${days}d ${hours}h ${minutes}m"
    }
}