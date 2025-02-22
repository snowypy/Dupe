package codes.snowy.dupeJS.shards

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import codes.snowy.dupeJS.DupeJS

@CommandAlias("adminshardshop|ashop")
@CommandPermission("dupe.admin.shardshop")
class AdminShardShopCommand : BaseCommand() {

    private val configFile = File(DupeJS.getInstance().dataFolder, "shards.yml")

    @Subcommand("set")
    @Syntax("<shardPrice> [cashPrice]")
    fun onSet(player: Player, shardPrice: Int, cashPrice: Double?) {
        val item = player.inventory.itemInMainHand
        
        if (item.type.toString() == "AIR") {
            player.sendMessage("&#9436fe&lSHARDS &8| &cYou must be holding an item.".translate())
            return
        }

        if (shardPrice <= 0) {
            player.sendMessage("&#9436fe&lSHARDS &8| &cPrice must be greater than 0.".translate())
            return
        }

        val config = YamlConfiguration.loadConfiguration(configFile)
        val itemId = generateItemId(item.type.toString().lowercase())
        
        val shopSection = config.getConfigurationSection("shop.items") 
            ?: config.createSection("shop.items")
        
        val itemSection = shopSection.createSection(itemId)
        
        itemSection.set("displayName", item.itemMeta?.displayName ?: "&f${item.type.toString().replace("_", " ")}")
        itemSection.set("material", item.type.toString())
        
        val lore = mutableListOf(
            "&7Purchase this item for &#9436fe$shardPrice Shards"
        )
        if (cashPrice != null) {
            lore.add("&7or &a$${cashPrice} &7cash")
        }
        lore.add("")
        lore.add("&7[Click to Purchase]")
        
        itemSection.set("lore", lore)
        itemSection.set("price", shardPrice)
        if (cashPrice != null) {
            itemSection.set("cashPrice", cashPrice)
        }
        itemSection.set("commands", listOf("example %player%"))

        config.save(configFile)
        ShardShop.loadConfig()

        player.sendMessage("&#9436fe&lSHARDS &8| &fAdded new shop item with price: &#9436fe$shardPrice Shards${if (cashPrice != null) " &for &a$${cashPrice}" else ""}".translate())
    }

    private fun generateItemId(baseName: String): String {
        val config = YamlConfiguration.loadConfiguration(configFile)
        val shopSection = config.getConfigurationSection("shop.items")
        
        var counter = 1
        var itemId = baseName.replace("_", "").lowercase()
        
        while (shopSection?.contains(itemId) == true) {
            itemId = "${baseName}_${counter++}".replace("_", "").lowercase()
        }
        
        return itemId
    }
} 