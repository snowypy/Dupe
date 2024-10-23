package codes.snowy.dupeJS.utils

import org.bukkit.Bukkit

object Logger {
    fun log(msg: String, status: String) {
        if ("$status" == "err") {
            Bukkit.getConsoleSender().sendMessage("&#ae4ff7[DUPE] &#FF0000[ERROR] &c$msg".translate())
        } else if ("$status" == "warning") {
            Bukkit.getConsoleSender().sendMessage("&#ae4ff7[DUPE] &#ed480c[WARNING] &#f78928$msg".translate())
        } else if ("$status" == "success") {
            Bukkit.getConsoleSender().sendMessage("&#ae4ff7[DUPE] &#0ced35[SUCCESS] &a$msg".translate())
        } else if ("$status" == "info") {
            Bukkit.getConsoleSender().sendMessage("&#ae4ff7[DUPE] &#03b1fc[INFO] &#69cefa$msg".translate())
        } else if ("$status" == "debug"){
            Bukkit.getConsoleSender().sendMessage("&#ae4ff7[DUPE] &#03b1fc[DEBUG] &#69cefa$msg".translate())
        }
    }
}