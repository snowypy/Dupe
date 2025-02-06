package codes.snowy.dupeJS.shards

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

data class ShopItem(
    val displayName: String,
    val material: Material,
    val lore: List<String>,
    val price: Int,
    val commands: List<String>
)

object ShardShop {
    private val items = mutableMapOf<String, ShopItem>()
    private val configFile = File(DupeJS.getInstance().dataFolder, "shards.yml")
    
    init {
        loadConfig()
    }

    private fun loadConfig() {
        if (!configFile.exists()) {
            DupeJS.getInstance().saveResource("shards.yml", false)
        }

        val config = YamlConfiguration.loadConfiguration(configFile)
        val shopSection = config.getConfigurationSection("shop.items") ?: return

        for (key in shopSection.getKeys(false)) {
            val section = shopSection.getConfigurationSection(key) ?: continue
            
            items[key] = ShopItem(
                displayName = section.getString("displayName", "")?.translate() ?: "",
                material = Material.valueOf(section.getString("material", "STONE")!!),
                lore = section.getStringList("lore").map { it.translate() },
                price = section.getInt("price"),
                commands = section.getStringList("commands")
            )
        }
    }

    fun getItems(): Map<String, ShopItem> = items
} 