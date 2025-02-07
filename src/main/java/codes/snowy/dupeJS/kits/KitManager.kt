package codes.snowy.dupeJS.kits

import Kit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import java.io.File
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.translate

class KitManager {
    private val kits = mutableMapOf<String, Kit>()
    private val configFile = File(DupeJS.getInstance().dataFolder, "kits.yml")
    private val config: YamlConfiguration

    init {
        if (!configFile.exists()) {
            DupeJS.getInstance().saveResource("kits.yml", false)
        }
        config = YamlConfiguration.loadConfiguration(configFile)
        loadKits()
    }

    private fun loadKits() {
        val kitsSection = config.getConfigurationSection("kits") ?: return
        
        for (kitName in kitsSection.getKeys(false)) {
            val section = kitsSection.getConfigurationSection(kitName) ?: continue
            
            val kit = Kit(
                name = kitName,
                permission = section.getString("permission") ?: "dupe.kit.$kitName",
                displayItem = section.getString("displayItem") ?: "CHEST",
                items = section.getList("items")?.filterIsInstance<ItemStack>() ?: emptyList(),
                cooldownHours = section.getInt("cooldownHours", 24)
            )
            kits[kitName.lowercase()] = kit
        }
    }

    fun saveKit(kit: Kit) {
        val kitsSection = config.getConfigurationSection("kits") ?: config.createSection("kits")
        val kitSection = kitsSection.createSection(kit.name)
        
        kitSection.set("permission", kit.permission)
        kitSection.set("displayItem", kit.displayItem)
        kitSection.set("items", kit.items)
        kitSection.set("cooldownHours", kit.cooldownHours)
        
        config.save(configFile)
        kits[kit.name.lowercase()] = kit
    }

    fun getKit(name: String): Kit? = kits[name.lowercase()]
    
    fun getAllKits(): List<Kit> = kits.values.toList()
    
    fun deleteKit(name: String) {
        kits.remove(name.lowercase())
        config.set("kits.${name}", null)
        config.save(configFile)
    }
} 