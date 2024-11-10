package codes.snowy.dupeJS.utils

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class Language(private val plugin: Plugin, private val configManager: Config) {

    var filePath: String = "language.yml"
        private set
    private var configFile: File = File(plugin.dataFolder, filePath)
    private var config: FileConfiguration = YamlConfiguration.loadConfiguration(configFile)

    init {
        createConfig()
    }

    private fun createConfig() {
        if (!configFile.exists()) {
            plugin.saveResource(filePath, false)
        }
        config = YamlConfiguration.loadConfiguration(configFile)
    }

    fun getMessages(key: String): String {
        val message = config.getString(key) ?: return ""
        return message.translate()
    }

    fun getReplacedMessage(key: String): String {
        val message = config.getString(key) ?: return ""
        return message.translate()
            .replace("%p%", getMessages("prefix"))
            .replace("%tp%", getMessages("team-prefix"))
    }

    fun getArrayMessages(key: String): List<String> {
        val messages = config.getStringList(key)
        if (messages.isEmpty()) {
            return emptyList()
        }

        return messages.map { it.translate() }
    }

    fun reloadConfig() {
        if (!configFile.exists()) {
            configFile = File(plugin.dataFolder, filePath)
        }
        config = YamlConfiguration.loadConfiguration(configFile)
    }

    fun saveConfig() {
        try {
            config.save(configFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}