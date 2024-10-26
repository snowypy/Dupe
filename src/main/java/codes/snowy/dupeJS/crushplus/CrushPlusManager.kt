package codes.snowy.dupeJS.crushplus

import codes.snowy.dupeJS.utils.translate
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldedit.bukkit.BukkitAdapter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class CrushPlusManager(private val plugin: JavaPlugin) {

    fun hasStaffPermission(player: Player): Boolean {
        return player.hasPermission("crush.staff")
    }

    fun hasCrushPlus(player: Player): Boolean {
        return player.hasPermission("crush.plus")
    }

    fun isInSpawnRegion(player: Player): Boolean {
        val location = player.location
        val wgInstance = WorldGuard.getInstance()
        val regionContainer = wgInstance.platform.regionContainer
        val regionManager: RegionManager = regionContainer[BukkitAdapter.adapt(location.world)] ?: return false
        val applicableRegions: ApplicableRegionSet = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location))

        for (region: ProtectedRegion in applicableRegions) {
            if (region.id.equals("spawn", ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun toggleFlight(player: Player) {

        if (hasStaffPermission(player)) {
            player.allowFlight = !player.allowFlight
            player.isFlying = player.allowFlight
            val flightStatus = if (player.allowFlight) "enabled" else "disabled"
            if (flightStatus == "disabled") {
                player.sendMessage("&#ee82f9&lCRUSH+ &8| &fYour flight has been &c$flightStatus.".translate())
            } else {
                player.sendMessage("&#ee82f9&lCRUSH+ &8| &fYour flight has been &a$flightStatus.".translate())
            }
            return
        }

        if (!hasCrushPlus(player)) {
            player.sendMessage("&#ff0000&lPERMISSION &8| &fSorry you need an active &#ee82f9'Crush+'&f subscription to use this!".translate())
            return
        }

        if (!isInSpawnRegion(player)) {
            player.sendMessage("&#ee82f9&lCRUSH+ &8| &cYou can only toggle flight while in the Spawn Area.".translate())
            return
        }

        player.allowFlight = !player.allowFlight
        player.isFlying = player.allowFlight
        val flightStatus = if (player.allowFlight) "enabled" else "disabled"

        if (flightStatus == "disabled") {
            player.sendMessage("&#ee82f9&lCRUSH+ &8| &fYour flight has been &c$flightStatus.".translate())
        } else {
            player.sendMessage("&#ee82f9&lCRUSH+ &8| &fYour flight has been &a$flightStatus.".translate())
        }
    }

    fun removeFlight(player: Player) {
        player.allowFlight = false
        player.isFlying = false
        player.sendMessage("&#ee82f9&lCRUSH+ &8| &fYour flight has been &cdisabled&f.".translate())
    }
}
