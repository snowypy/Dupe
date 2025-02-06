package codes.snowy.dupeJS.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseHelper {

    private val url = "jdbc:sqlite:Databases/basic.db"

    init {
        createTables()
    }

    private fun createTables() {
        val spawnTableSql = """
            CREATE TABLE IF NOT EXISTS spawn (
                id INTEGER PRIMARY KEY,
                world TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL
            );
        """.trimIndent()

        val donorTableSql = """
            CREATE TABLE IF NOT EXISTS donor_location (
                id INTEGER PRIMARY KEY,
                world TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                yaw REAL NOT NULL,
                pitch REAL NOT NULL
            );
        """.trimIndent()

        try {
            DriverManager.getConnection(url).use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute(spawnTableSql)
                    stmt.execute(donorTableSql)
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun saveSpawn(world: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        val deleteSql = "DELETE FROM spawn"
        val insertSql = "INSERT INTO spawn (world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)"

        try {
            DriverManager.getConnection(url).use { conn ->
                conn.prepareStatement(deleteSql).use { deleteStmt ->
                    deleteStmt.executeUpdate()
                }
                conn.prepareStatement(insertSql).use { insertStmt ->
                    insertStmt.setString(1, world)
                    insertStmt.setDouble(2, x)
                    insertStmt.setDouble(3, y)
                    insertStmt.setDouble(4, z)
                    insertStmt.setFloat(5, yaw)
                    insertStmt.setFloat(6, pitch)
                    insertStmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getSpawn(): Location? {
        val sql = "SELECT world, x, y, z, yaw, pitch FROM spawn LIMIT 1"

        try {
            DriverManager.getConnection(url).use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val world = Bukkit.getWorld(rs.getString("world"))
                            val x = rs.getDouble("x")
                            val y = rs.getDouble("y")
                            val z = rs.getDouble("z")
                            val yaw = rs.getFloat("yaw")
                            val pitch = rs.getFloat("pitch")
                            return Location(world, x, y, z, yaw, pitch)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    fun saveDonorLocation(world: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        val deleteSql = "DELETE FROM donor_location"
        val insertSql = "INSERT INTO donor_location (world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)"

        try {
            DriverManager.getConnection(url).use { conn ->
                conn.prepareStatement(deleteSql).use { deleteStmt ->
                    deleteStmt.executeUpdate()
                }
                conn.prepareStatement(insertSql).use { insertStmt ->
                    insertStmt.setString(1, world)
                    insertStmt.setDouble(2, x)
                    insertStmt.setDouble(3, y)
                    insertStmt.setDouble(4, z)
                    insertStmt.setFloat(5, yaw)
                    insertStmt.setFloat(6, pitch)
                    insertStmt.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getDonorLocation(): Location? {
        val sql = "SELECT world, x, y, z, yaw, pitch FROM donor_location LIMIT 1"

        try {
            DriverManager.getConnection(url).use { conn ->
                conn.prepareStatement(sql).use { pstmt ->
                    pstmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val world = Bukkit.getWorld(rs.getString("world"))
                            val x = rs.getDouble("x")
                            val y = rs.getDouble("y")
                            val z = rs.getDouble("z")
                            val yaw = rs.getFloat("yaw")
                            val pitch = rs.getFloat("pitch")
                            return Location(world, x, y, z, yaw, pitch)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }
}