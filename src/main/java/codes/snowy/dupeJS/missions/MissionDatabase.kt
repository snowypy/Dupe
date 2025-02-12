package codes.snowy.dupeJS.missions

import java.sql.Connection
import java.sql.DriverManager

class MissionDatabase {
    private val dbConnection: Connection

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/missions.db")
        createTables()
    }

    private fun createTables() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS player_missions (
                player_uuid TEXT,
                mission_uuid TEXT,
                mission_type TEXT,
                progress INT,
                target INT,
                last_updated LONG,
                claimed BOOLEAN DEFAULT 0,
                PRIMARY KEY (player_uuid, mission_uuid)
            )
        """)
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS player_activity (
                player_uuid TEXT PRIMARY KEY,
                join_count INT,
                last_join BIGINT
            )
        """)
        statement.close()
    }
} 