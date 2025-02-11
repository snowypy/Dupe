package codes.snowy.dupeJS.missions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate

class RewardSystem {

    fun spinRewardWheel(player: Player) {
        val inventory = Bukkit.createInventory(null, 27, "&#feda36&lSPINNING WHEEL".translate())
        player.openInventory(inventory)

        object : BukkitRunnable() {
            var ticks = 0

            override fun run() {
                if (ticks >= 20) {
                    val rewards = getRewardsFromConfig()
                    val selectedReward = rewards.random()
                    inventory.setItem(13, selectedReward.previewItem)
                    executeRewardCommand(player, selectedReward.command)
                    cancel()
                } else {
                    for (i in 0 until inventory.size) {
                        inventory.setItem(i, getRewardsFromConfig().random().previewItem)
                    }
                    ticks++
                }
            }
        }.runTaskTimer(DupeJS.getInstance(), 0L, 5L)
    }

    private fun getRewardsFromConfig(): List<Reward> {
        return listOf()
    }

    private fun executeRewardCommand(player: Player, command: String) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.name))
    }
}

data class Reward(val previewItem: ItemStack, val command: String) 