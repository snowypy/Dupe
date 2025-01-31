package codes.snowy.dupeJS.dupe

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Logger
import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBT
import de.tr7zw.changeme.nbtapi.NBTItem
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.UUID
import formatMaterial
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.HashMap

class DupeManager {

    private val config = Config(DupeJS.getInstance())
    private val dupeCounts: MutableMap<UUID, Int> = HashMap()
    private val dbConnection: Connection

    private val rankDupeLimits: Map<String, Int> = mapOf(
        "default" to 30,
        "vip" to 50,
        "pro" to 100,
        "reaper" to 175,
        "patron" to 250
    )

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:dupeCounts.db")
        createTable()
        loadDupeCounts()
    }

    private fun createTable() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS dupe_counts (uuid TEXT PRIMARY KEY, count INTEGER)")
        statement.close()
    }

    private fun loadDupeCounts() {
        val statement = dbConnection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM dupe_counts")
        while (resultSet.next()) {
            val uuid = UUID.fromString(resultSet.getString("uuid"))
            val count = resultSet.getInt("count")
            dupeCounts[uuid] = count
        }
        resultSet.close()
        statement.close()
    }

    private fun saveDupeCount(uuid: UUID, count: Int) {
        val statement: PreparedStatement = dbConnection.prepareStatement("REPLACE INTO dupe_counts (uuid, count) VALUES (?, ?)")
        statement.setString(1, uuid.toString())
        statement.setInt(2, count)
        statement.executeUpdate()
        statement.close()
    }

    fun rechargeDupe(player: Player) {
        val playerUUID = player.uniqueId
        val playerRank = getPlayerRank(player)
        val maxDupes = rankDupeLimits[playerRank] ?: rankDupeLimits["default"]!!

        dupeCounts[playerUUID] = maxDupes
        saveDupeCount(playerUUID, maxDupes)
        player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have recharged your dupe limit.".translate())
    }

    fun dupe(player: Player) {
        val playerUUID = player.uniqueId
        val playerRank = getPlayerRank(player)
        val maxDupes = rankDupeLimits[playerRank] ?: rankDupeLimits["default"]!!

        val currentDupes = dupeCounts.getOrDefault(playerUUID, 0)
        if (currentDupes >= maxDupes) {
            player.sendMessage("&#7723ea&lDUPE &8| &cYou have reached your dupe limit for today.".translate())
            return
        }

        var item = player.itemInHand
        val itemamount = item.amount

        if (item.type.toString() == "AIR") {
            player.sendMessage("&#7723ea&lDUPE &8| &cYou are not holding an item.".translate())
            return
        }

        if (player.inventory.size <= 36) {
            player.sendMessage("&#7723ea&lDUPE &8| &cYour inventory is full.".translate())
            return
        }

        if (nbtCheck(item)) {
            player.sendMessage("&#7723ea&lDUPE &8| &cThis item is not allowed to be duplicated.".translate())
            return
        }

        player.inventory.addItem(item)
        val newCount = currentDupes + 1
        dupeCounts[playerUUID] = newCount
        saveDupeCount(playerUUID, newCount)
        val itemformat = item.type.toString().formatMaterial()

        if (itemamount > 1) {
            player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have duplicated &#ac9ac6$itemamount&#ac9ac6x $itemformat".translate())
        } else {
            player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have duplicated a &#ac9ac6$itemformat".translate())
        }
    }

    fun getDupeCount(player: Player): Int {
        return dupeCounts.getOrDefault(player.uniqueId, 0)
    }

    fun blacklistDupe(player: Player) {
        val player = player
        var item = player.itemInHand

        if (item.type.toString() == "AIR") {
            player.sendMessage("&#7723ea&lDUPE &8| &cYou are not holding an item.".translate())
            return
        }

        if (nbtCheck(item)) {
            player.sendMessage("&#7723ea&lDUPE &8| &cThis item is already blacklisted.".translate())
            return
        }

        val nbtItem = NBTItem(item)
        nbtItem.setInteger("custom_model_data", config.getInt("dupe.modeldata", 1111))
        player.setItemInHand(nbtItem.item)
        player.sendMessage("&#7723ea&lDUPE &8| &#98f81dYou have blacklisted this item.".translate())
    }

    fun nbtCheck(stack: ItemStack): Boolean {

        val nbtItem = NBT.itemStackToNBT(stack)

        val customModelData = nbtItem.toString()
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log(customModelData, "debug")
        }
        if ("custom_model_data:" + config.getInt("dupe.modeldata", 1111) in customModelData) {
            return true
        } else {
            return false
        }
    }

    private fun getPlayerRank(player: Player): String {
        return when {
            player.hasPermission("dupe.rank.patron") -> "patron"
            player.hasPermission("dupe.rank.reaper") -> "reaper"
            player.hasPermission("dupe.rank.pro") -> "pro"
            player.hasPermission("dupe.rank.vip") -> "vip"
            else -> "default"
        }
    }
}