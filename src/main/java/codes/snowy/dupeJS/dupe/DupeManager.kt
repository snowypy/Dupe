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
    private val dbConnection: Connection

    private val rankDupeLimits: Map<String, Int> = mapOf(
        "default" to 30,
        "titan" to 50,
        "pro" to 100,
        "ultra" to 175,
        "hero" to 250
    )

    init {
        dbConnection = DriverManager.getConnection("jdbc:sqlite:Databases/dupe.db")
        createTable()
    }

    private fun createTable() {
        val statement = dbConnection.createStatement()
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS dupe_counts (uuid TEXT PRIMARY KEY, count INTEGER)")
        statement.close()
    }

    private fun saveDupeCount(uuid: UUID, count: Int) {
        val statement: PreparedStatement = dbConnection.prepareStatement("REPLACE INTO dupe_counts (uuid, count) VALUES (?, ?)")
        statement.setString(1, uuid.toString())
        statement.setInt(2, count)
        statement.executeUpdate()
        statement.close()
    }

    private fun getDupeCountFromDB(uuid: UUID): Int {
        val statement: PreparedStatement = dbConnection.prepareStatement("SELECT count FROM dupe_counts WHERE uuid = ?")
        statement.setString(1, uuid.toString())
        val resultSet: ResultSet = statement.executeQuery()
        val count = if (resultSet.next()) resultSet.getInt("count") else 0
        resultSet.close()
        statement.close()
        return count
    }

    fun rechargeDupe(player: Player) {
        val playerUUID = player.uniqueId
        val playerRank = getPlayerRank(player)
        val maxDupes = rankDupeLimits[playerRank] ?: rankDupeLimits["default"]!!

        saveDupeCount(playerUUID, maxDupes)
        player.sendMessage("&#ff3358&lDUPE &8| &fYou have &#98f81d&nRECHARGED&f your dupe limit!".translate())
    }

    fun dupe(player: Player) {
        val playerUUID = player.uniqueId
        val playerRank = getPlayerRank(player)
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log("Player rank: $playerRank", "debug")
        }
        val maxDupes = rankDupeLimits[playerRank] ?: rankDupeLimits["default"]!!
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log("Max dupes: $maxDupes", "debug")
        }

        val currentDupes = getDupeCountFromDB(playerUUID)
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log("Current dupes: $currentDupes", "debug")
        }
        if (currentDupes >= maxDupes) {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fYou have reached your &#ff0000&nDUPE LIMIT&f. You can UPGRADE it by buying a rank from our &#9ef52f&n/store".translate())
            return
        }

        var item = player.itemInHand
        val itemamount = item.amount

        if (item.type.toString() == "AIR") {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fYou are not holding an item.".translate())
            return
        }

        if (player.inventory.size <= 36) {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fYour inventory is full.".translate())
            return
        }

        if (nbtCheck(item)) {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fYou are trying to dupe a &#ff0000&nBLACKLISTED&f item.".translate())
            return
        }

        player.inventory.addItem(item)
        val newCount = currentDupes + 1
        saveDupeCount(playerUUID, newCount)
        val itemformat = item.type.toString().formatMaterial()

        if (itemamount > 1) {
            player.sendMessage("&#ff3358&lDUPE &8| &fYou have duplicated &#ff3358$itemamount&#ff3358x $itemformat".translate())
        } else {
            player.sendMessage("&#ff3358&lDUPE &8| &fYou have duplicated a &#ff3358$itemformat".translate())
        }
    }

    fun getDupeCount(player: Player): Int {
        val dupeCountz = getDupeCountFromDB(player.uniqueId)
        return dupeCountz
    }

    fun getMaxDupeCount(player: Player): Int {
        val playerRank = getPlayerRank(player)
        val maxDupes = rankDupeLimits[playerRank] ?: rankDupeLimits["default"]!!
        return maxDupes
    }

    fun getRechargeTime(player: Player): Int {
        val playerRank = getPlayerRank(player)
        val maxDupes = rankDupeLimits[playerRank] ?: rankDupeLimits["default"]!!
        val currentDupes = getDupeCountFromDB(player.uniqueId)
        val remainingDupes = maxDupes - currentDupes
        val rechargeTime = remainingDupes * config.getInt("dupe.recharge-time", 60)
        if (config.getBoolean("dupe.debug", false)) {
            Logger.log("Recharge time for ${player.name}: $rechargeTime", "debug")
        }
        return rechargeTime
    }

    fun blacklistDupe(player: Player) {
        val player = player
        var item = player.itemInHand

        if (item.type.toString() == "AIR") {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fYou must be holding an item!".translate())
            return
        }

        if (nbtCheck(item)) {
            player.sendMessage("&#ff3358&lDUPE &8| &fThis item is &#ff0000&nalready blacklisted&f.".translate())
            return
        }

        val nbtItem = NBTItem(item)
        nbtItem.setInteger("custom_model_data", config.getInt("dupe.modeldata", 1111))
        player.setItemInHand(nbtItem.item)
        player.sendMessage("&#ff3358&lDUPE &8| &fThis item is now &#98f81d&nBLOCKED".translate())
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
            player.hasPermission("dupe.rank.hero") -> "hero"
            player.hasPermission("dupe.rank.ultra") -> "ultra"
            player.hasPermission("dupe.rank.pro") -> "pro"
            player.hasPermission("dupe.rank.titan") -> "vip"
            else -> "default"
        }
    }
}