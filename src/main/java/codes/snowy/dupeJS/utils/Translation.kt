package codes.snowy.dupeJS.utils

import org.bukkit.ChatColor
import java.util.regex.Matcher
import java.util.regex.Pattern

// [@] Authored by: Snowy

fun String.translate(): String
{
    val HEX_PATTERN: Pattern = Pattern.compile("&#([0-9A-Fa-f]{6})")
    val matcher: Matcher = HEX_PATTERN.matcher(this)
    val buffer = StringBuffer()
    while (matcher.find()) {
        try {
            matcher.appendReplacement(
                buffer,
                net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString()
            )
        } catch (e: NoSuchMethodError) {
            return this
        }
    }
    return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString())
}

fun String.extractLastHexCode(): String {
    return this.replace(" ", "").split("").reversed().take(8).reversed().joinToString("")
}