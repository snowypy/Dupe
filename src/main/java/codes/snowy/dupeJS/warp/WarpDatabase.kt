package codes.snowy.dupeJS.warp

import WarpDetails
import java.sql.Connection
import java.sql.DriverManager
import org.bukkit.Bukkit
import org.bukkit.Location
import java.sql.SQLException

class WarpDatabase {
    private val dbConnection: Connection

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/warps.db")
        createTable()
    }

    private fun createTable() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("""
            CREATE TABLE IF NOT EXISTS warps (
                name TEXT PRIMARY KEY,
                world TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL,
                preview_item TEXT NOT NULL,
                display_name TEXT NOT NULL,
                visit_count INTEGER DEFAULT 0
            )
        """)
        statement.close()
    }

    fun getWarp(name: String): Location? {
        val statement = dbConnection.prepareStatement("SELECT * FROM warps WHERE name = ?")
        statement.setString(1, name)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            val world = Bukkit.getWorld(resultSet.getString("world"))
            val x = resultSet.getDouble("x")
            val y = resultSet.getDouble("y")
            val z = resultSet.getDouble("z")
            val yaw = resultSet.getFloat("yaw")
            val pitch = resultSet.getFloat("pitch")
            Location(world, x, y, z, yaw, pitch)
        } else {
            null
        }.also {
            resultSet.close()
            statement.close()
        }
    }

    fun getAllWarps(): List<String> {
        val statement = dbConnection.createStatement()
        val resultSet = statement.executeQuery("SELECT name FROM warps")
        val warps = mutableListOf<String>()
        while (resultSet.next()) {
            warps.add(resultSet.getString("name"))
        }
        resultSet.close()
        statement.close()
        return warps
    }

    fun addWarp(name: String, location: Location, previewItem: String, displayName: String) {
        val sql = """
            INSERT OR REPLACE INTO warps (name, world, x, y, z, yaw, pitch, preview_item, display_name) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        try {
            dbConnection.prepareStatement(sql).use { statement ->
                statement.setString(1, name)
                statement.setString(2, location.world?.name)
                statement.setDouble(3, location.x)
                statement.setDouble(4, location.y)
                statement.setDouble(5, location.z)
                statement.setFloat(6, location.yaw)
                statement.setFloat(7, location.pitch)
                statement.setString(8, previewItem)
                statement.setString(9, displayName)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getWarpDetails(name: String): WarpDetails {
        val statement = dbConnection.prepareStatement("SELECT * FROM warps WHERE name = ?")
        statement.setString(1, name)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            WarpDetails(
                name,
                resultSet.getString("preview_item"),
                resultSet.getString("display_name")
            )
        } else {
            throw IllegalArgumentException("Warp not found")
        }.also {
            resultSet.close()
            statement.close()
        }
    }

    fun getVisitCount(name: String): Int {
        val statement = dbConnection.prepareStatement("SELECT visit_count FROM warps WHERE name = ?")
        statement.setString(1, name)
        val resultSet = statement.executeQuery()
        val count = if (resultSet.next()) resultSet.getInt("visit_count") else 0
        resultSet.close()
        statement.close()
        return count
    }

    fun incrementVisitCount(name: String) {
        val statement = dbConnection.prepareStatement("UPDATE warps SET visit_count = visit_count + 1 WHERE name = ?")
        statement.setString(1, name)
        statement.executeUpdate()
        statement.close()
    }
} 