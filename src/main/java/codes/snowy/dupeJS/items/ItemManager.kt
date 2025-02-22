package codes.snowy.dupeJS.items

import codes.snowy.dupeJS.DupeJS
import com.google.gson.GsonBuilder
import org.bukkit.inventory.ItemStack
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder

class ItemManager {
    private val plugin = DupeJS.getInstance()
    private val itemsFolder = File(plugin.dataFolder, "items")

    init {
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs()
        }
    }

    fun saveItem(name: String, item: ItemStack) {
        val file = File(itemsFolder, "$name.json")
        val itemConfig = YamlConfiguration()
        val baos = ByteArrayOutputStream()
        BukkitObjectOutputStream(baos).use { it.writeObject(item) }
        val serializedItem = Base64.getEncoder().encodeToString(baos.toByteArray())
        
        val json = mapOf(
            "name" to name,
            "serializedItem" to serializedItem
        )
        
        val gson = GsonBuilder().setPrettyPrinting().create()
        FileWriter(file).use { writer ->
            gson.toJson(json, writer)
        }
    }

    fun getItem(name: String): ItemStack? {
        val file = File(itemsFolder, "$name.json")
        if (!file.exists()) return null

        val gson = GsonBuilder().create()
        val json = FileReader(file).use { reader ->
            gson.fromJson(reader, Map::class.java)
        }

        val serializedItem = json["serializedItem"] as String
        val data = Base64.getDecoder().decode(serializedItem)
        
        return BukkitObjectInputStream(ByteArrayInputStream(data)).use { 
            it.readObject() as ItemStack 
        }
    }

    fun itemExists(name: String): Boolean {
        return File(itemsFolder, "$name.json").exists()
    }
} 