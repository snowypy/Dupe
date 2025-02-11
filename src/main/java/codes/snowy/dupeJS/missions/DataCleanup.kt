package codes.snowy.dupeJS.missions

import java.sql.Connection

class DataCleanup(private val dbConnection: Connection) {

    fun cleanupInactivePlayers() {
        val statement = dbConnection.prepareStatement("""
            DELETE FROM player_missions
            WHERE player_uuid IN (
                SELECT player_uuid FROM player_activity
                WHERE join_count < 5 AND last_join < ?
            )
        """)
        statement.setLong(1, System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
        statement.executeUpdate()
        statement.close()
    }
} 