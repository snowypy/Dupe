package codes.snowy.dupeJS.utils

import org.bukkit.ChatColor

fun applyRainbowText(text: String): String {
    val colors = arrayOf(
        ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
        ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE,
        ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE
    )
    val sb = StringBuilder()
    var colorIndex = 0
    text.forEach { char ->
        if (char.isWhitespace()) {
            sb.append(char)
        } else {
            sb.append(colors[colorIndex % colors.size]).append(char)
            colorIndex++
        }
    }
    sb.append(ChatColor.RESET)
    return sb.toString()
}

