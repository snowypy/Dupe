package codes.snowy.dupeJS.shards

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.translate
import codes.snowy.dupeJS.utils.Logger
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

data class ShopItem(
    val displayName: String,
    val material: Material,
    val lore: List<String>,
    val price: Int,
    val cashPrice: Double?,
    val commands: List<String>
)

object ShardShop {
    private val config = Config(DupeJS.getInstance())
    private val items = mutableMapOf<String, ShopItem>()
    private val configFile = File(DupeJS.getInstance().dataFolder, "shards.yml")
    
    init {
        loadConfig()
    }

    fun loadConfig() {
        if (!configFile.exists()) {
            DupeJS.getInstance().saveResource("shards.yml", false)
        }

        val config = YamlConfiguration.loadConfiguration(configFile)
        val shopSection = config.getConfigurationSection("shop.items") ?: return

        items.clear()
        if (config.getBoolean("dupe.debug")) {
            Logger.log("[ShardShop] Loading shop items...", "debug")
        }

        for (key in shopSection.getKeys(false)) {
            val section = shopSection.getConfigurationSection(key) ?: continue
            
            val cashPrice = if (section.contains("cashPrice")) section.getDouble("cashPrice") else null
            if (config.getBoolean("dupe.debug")) {
                Logger.log("[ShardShop] Loading item $key:", "debug")
                Logger.log("  - Display Name: ${section.getString("displayName")}", "debug")
                Logger.log("  - Material: ${section.getString("material")}", "debug")
                Logger.log("  - Price: ${section.getInt("price")}", "debug")
                Logger.log("  - Cash Price: $cashPrice", "debug")
            }
            
            items[key] = ShopItem(
                displayName = section.getString("displayName", "")?.translate() ?: "",
                material = Material.valueOf(section.getString("material", "STONE")!!),
                lore = section.getStringList("lore").map { it.translate() },
                price = section.getInt("price"),
                cashPrice = cashPrice,
                commands = section.getStringList("commands")
            )
        }
        
        if (config.getBoolean("dupe.debug")) {
            Logger.log("[ShardShop] Loaded ${items.size} items", "debug")
        }
    }

    fun getItems(): Map<String, ShopItem> = items
} 