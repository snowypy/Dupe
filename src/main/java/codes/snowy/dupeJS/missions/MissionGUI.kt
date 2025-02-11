package codes.snowy.dupeJS.missions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import codes.snowy.dupeJS.utils.translate

class MissionGUI(private val missionManager: MissionManager, private val rewardSystem: RewardSystem) {

    fun openMissionSelector(player: Player) {
        missionManager.ensureMissionsAssigned(player)
        val inventorySize = 27
        val inventory = Bukkit.createInventory(null, inventorySize, "&#feda36&lMISSIONS".translate())

        val missions = missionManager.getPlayerMissions(player)
        missions.forEachIndexed { index, mission ->
            val isComplete = mission.progress >= mission.target
            val status = if (isComplete) "&a&lCOMPLETE" else "&#FF0000&lNOT COMPLETE"
            val missionItem = ItemStack(Material.PAPER).apply {
                itemMeta = itemMeta?.apply {
                    setDisplayName("&#feda36&l${mission.type}".translate())
                    lore = listOf(
                        "&#feda36&l| &7Progress: ${mission.progress}/${mission.target}".translate(),
                        "&#feda36&l| &7Last Updated: ${mission.lastUpdated}".translate(),
                        status.translate()
                    )
                }
            }
            inventory.setItem(index, missionItem)
        }

        player.openInventory(inventory)
    }

    fun openMissionStats(player: Player) {
        val inventorySize = 27
        val inventory = Bukkit.createInventory(null, inventorySize, "&#feda36&lMISSION STATS".translate())

        val completedMissions = missionManager.getCompletedMissions(player)
        val statsItem = ItemStack(Material.BOOK).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("&#feda36&lMission Stats".translate())
                lore = listOf(
                    "&#feda36&l| &7Completed Missions: $completedMissions".translate()
                )
            }
        }
        inventory.setItem(13, statsItem)

        player.openInventory(inventory)
    }
} 