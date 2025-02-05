import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.managers.RegionManager
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.entity.Player

fun isPlayerInAFKRegion(player: Player): Boolean {
    val worldGuard = WorldGuard.getInstance()
    val world = BukkitAdapter.adapt(player.world)
    val regionManager: RegionManager = worldGuard.platform.regionContainer.get(world) ?: return false
    val location = BukkitAdapter.asBlockVector(player.location)
    val applicableRegions: ApplicableRegionSet = regionManager.getApplicableRegions(location)
    for (region: ProtectedRegion in applicableRegions) {
        if (region.id.equals("afk", ignoreCase = true)) {
            return true
        }
    }
    return false
}