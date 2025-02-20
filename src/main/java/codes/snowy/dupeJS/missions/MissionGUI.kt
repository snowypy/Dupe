package codes.snowy.dupeJS.missions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBTItem
import java.util.concurrent.TimeUnit
import org.bukkit.inventory.meta.ItemMeta

class MissionGUI(private val missionManager: MissionManager, private val rewardSystem: RewardSystem) {

    fun openMissionSelector(player: Player) {
        missionManager.ensureMissionsAssigned(player)
        val inventorySize = 27
        val inventory = Bukkit.createInventory(null, inventorySize, "&#fdfa28&lMISSIONS".translate())

        val dailyResetTimeLeft = missionManager.lastDailyReset + TimeUnit.DAYS.toMillis(1) - System.currentTimeMillis()
        val weeklyResetTimeLeft = missionManager.lastWeeklyReset + TimeUnit.DAYS.toMillis(7) - System.currentTimeMillis()

        val clockItem = ItemStack(Material.CLOCK).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName("&#fdfa28&lMISSION RESETS".translate())
                lore = listOf(
                    "",
                    "&#33e1ff&l| &fDaily Reset: &#33e1ff${formatTime(dailyResetTimeLeft)}".translate(),
                    "&#ff9433&l| &fWeekly Reset: &#ff9433${formatTime(weeklyResetTimeLeft)}".translate(),
                    ""
                )
            }
        }

        for (i in 0 until inventory.size) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName(" ")
                    }
                })
            }
        }

        val missions = missionManager.getPlayerMissions(player)
        val middleRowStart = 9
        val middleRowEnd = 17
        val missionSlots = (middleRowStart..middleRowEnd).toList()

        val totalMissions = missions.size
        val startSlot = (missionSlots.size - totalMissions) / 2

        missions.forEachIndexed { index, mission ->
            val isComplete = mission.progress >= mission.target
            val statusColor = when {
                mission.claimed -> "&#ff0000&n"
                isComplete -> "&#5fff33&n"
                else -> "&7&n"
            }
            val statusText = when {
                mission.claimed -> "&7[ALREADY CLAIMED]"
                isComplete -> "&7[CLICK TO CLAIM]"
                else -> "&#ff0000[CANNOT CLAIM]"
            }
            val frequencyColor = if (mission.frequency == "daily") "&#33e1ff" else "&#ff9433"
            val frequencyText = if (mission.frequency == "daily") "[DAILY]" else "[WEEKLY]"

            val missionItem = ItemStack(Material.PAPER).apply {
                itemMeta = itemMeta?.apply {
                    setDisplayName("$statusColor${mission.type}&r $frequencyColor$frequencyText".translate())
                    lore = listOf(
                        "",
                        "$frequencyColor&l| &fProgress: ${mission.progress}/${mission.target}".translate(),
                        "$frequencyColor&l| &fLast Updated: ${formatTimeSince(mission.lastUpdated)}".translate(),
                        "$frequencyColor&l| &fTime Left: ${mission.timeLeft()}".translate(),
                        "",
                        statusText.translate()
                    )
                }
            }
            val nbtItem = NBTItem(missionItem)
            nbtItem.setString("missionUUID", mission.missionUUID.toString())
            inventory.setItem(missionSlots[startSlot + index], nbtItem.item)
        }

        inventory.setItem(4, clockItem)

        player.openInventory(inventory)
    }

    private fun formatTime(timeInMillis: Long): String {
        val days = TimeUnit.MILLISECONDS.toDays(timeInMillis) % 7
        val hours = TimeUnit.MILLISECONDS.toHours(timeInMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % 60
        return "${days}d ${hours}h ${minutes}m"
    }

    private fun formatTimeSince(lastUpdated: Long): String {
        val currentTime = System.currentTimeMillis()
        val duration = currentTime - lastUpdated

        val days = TimeUnit.MILLISECONDS.toDays(duration)
        val hours = TimeUnit.MILLISECONDS.toHours(duration) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60

        return "${days}d ${hours}h ${minutes}m"
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