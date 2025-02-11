package codes.snowy.dupeJS.missions

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Material
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.io.File

class RewardSystem(private val plugin: DupeJS) {

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
        return loadRewardsConfig()
    }

    private fun executeRewardCommand(player: Player, command: String) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.name))
    }

    private fun loadRewardsConfig(): List<Reward> {
        val file = File(plugin.dataFolder, "missions.yml")
        if (!file.exists()) {
            plugin.saveResource("missions.yml", false)
        }
        val inputStream: InputStream = file.inputStream()
        val yaml = Yaml()
        val data: Map<String, List<Map<String, Any>>> = yaml.load(inputStream)
        return data["rewards"]?.map { rewardMap ->
            val previewItemMap = rewardMap["previewItem"] as Map<String, Any>
            val itemStack = ItemStack(Material.valueOf(previewItemMap["type"] as String), previewItemMap["amount"] as Int)
            Reward(itemStack, rewardMap["command"] as String)
        } ?: emptyList()
    }
}

data class Reward(val previewItem: ItemStack, val command: String) 