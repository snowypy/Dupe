package codes.snowy.dupeJS.warp

import WarpDetails
import org.bukkit.Location

class WarpManager(private val database: WarpDatabase) {

    fun getWarpLocation(name: String): Location? {
        return database.getWarp(name)
    }

    fun getAllWarpNames(): List<String> {
        return database.getAllWarps()
    }

    fun getWarpDetails(name: String): WarpDetails {
        return database.getWarpDetails(name)
    }

    fun getVisitCount(name: String): Int {
        return database.getVisitCount(name)
    }

    fun incrementVisitCount(name: String) {
        database.incrementVisitCount(name)
    }
} 