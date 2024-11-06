package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.bundles.Bundle
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

object BundleManager {
    private val bundles = mutableMapOf<String, Bundle>()
    private val dataFolder = File("plugins/DupeJS/bundles")

    init {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        loadAllBundles()
    }

    private fun loadAllBundles() {
        dataFolder.listFiles()?.forEach { file ->
            if (file.extension == "yml") {
                loadBundle(file)
            }
        }
    }

    private fun loadBundle(file: File) {
        val yamlConfig = YamlConfiguration.loadConfiguration(file)
        val bundleName = yamlConfig.getString("name") ?: return
        val color = yamlConfig.getString("color") ?: "WHITE"
        val displayName = yamlConfig.getString("displayName") ?: bundleName
        val itemList = yamlConfig.getList("items")?.mapNotNull { item ->
            if (item is ItemStack) item else null
        } ?: emptyList()

        bundles[bundleName.lowercase()] = Bundle(bundleName, color, displayName, itemList)
    }

    fun getBundle(name: String): Bundle? {
        return bundles[name.lowercase()]
    }

    fun saveBundle(bundle: Bundle) {
        val file = File(dataFolder, "${bundle.name}.yml")
        val yamlConfig = YamlConfiguration()

        yamlConfig.set("name", bundle.name)
        yamlConfig.set("color", bundle.color)
        yamlConfig.set("displayName", bundle.displayName)
        yamlConfig.set("items", bundle.items)

        yamlConfig.save(file)

        loadBundle(file)
    }

    fun getAllBundleNames(): List<String> {
        return bundles.keys.toList()
    }
}
