package codes.snowy.dupeJS.store

import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import net.md_5.bungee.api.ChatColor

class StoreManager {

    fun sendStoreMessage(player: Player, packageName: String, price: String) {
        val skinLines = getSkinPixels(player.uniqueId.toString())

        val message = """
            &8&m-----------------------------------------------------
            ${skinLines[0]}
            ${skinLines[1]}   &#FF0000&lP&#FF2A00&lU&#FF5500&lR&#FF7F00&lC&#FFBF00&lH&#FFFF00&lA&#AAFF00&lS&#55FF00&lE &#00BF40&lC&#008080&lO&#0040BF&lM&#0000FF&lP&#2600C1&lL&#4B0082&lE&#7000AB&lT&#9400D3&lE
            ${skinLines[2]}   
            ${skinLines[3]}   &#FF7F00&n${player.name}&f has purchased &#FF7F00&n$packageName&f for &#00FF00&n$$price
            ${skinLines[4]}   &ffrom the store! Thank you for your support &#ee72fd&l❤
            ${skinLines[5]}   
            ${skinLines[6]}   &7&oSay &6&lGG &7&oto congratulate them!
            ${skinLines[7]}
            &8&m-----------------------------------------------------
        """.trimIndent().translate()

        Bukkit.broadcastMessage(message)
    }

    private fun getPlayerSkullTexture(uuid: String): String {
        val url = URL("https://crafatar.com/avatars/$uuid?size=8&overlay")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode == 200) {
            val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonResponse)
            return jsonObject.getString("texture")
        } else {
            return "§x§0§0§0§0§0§0"
        }
    }

    fun getSkinPixels(playerUUID: String, displayChar: String = "█"): List<String> {
        val imageUrl = URL("https://crafatar.com/avatars/$playerUUID?size=8")
        val image: BufferedImage = ImageIO.read(imageUrl)
        val skinLines = mutableListOf<String>()

        for (y in 0 until 8) {
            var line = ""
            for (x in 0 until 8) {
                val color = Color(image.getRGB(x, y))
                val chatColor = ChatColor.of(color)
                line += "$chatColor$displayChar"
            }
            skinLines.add(line)
        }

        return skinLines
    }

}