package codes.snowy.dupeJS.utils

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.dupe.DupeManager
import codes.snowy.dupeJS.economy.VaultHook
import codes.snowy.dupeJS.utils.NumberConverter.convertCompact
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class PlaceholderHandler(plugin: DupeJS) : PlaceholderExpansion() {
    private val plugin: DupeJS = plugin
    private val dupeManager = DupeManager()

    override fun getAuthor(): String {
        return "snowi"
    }

    override fun getIdentifier(): String {
        return "dupey"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player, params: String): String? {
        if (params.equals("dupe-charges", ignoreCase = true)) {
            val charges = (dupeManager.getMaxDupeCount(player) - dupeManager.getDupeCount(player)).toString()
            return charges
        } else if (params.equals("dupe-max-charges", ignoreCase = true)) {
            val maxCharges = dupeManager.getMaxDupeCount(player).toString()
            return maxCharges
        } else if (params.equals("dupe-recharge-time", ignoreCase = true)) {
            val rechargeTime = dupeManager.getRechargeTime(player).toString()
            return rechargeTime
        } else if (params.equals("balance-raw", ignoreCase = true)) {
            val balanceRaw = VaultHook.getBalance(player).toLong().toString().replace(".0", "")
            return balanceRaw
        } else if (params.equals("balance-short", ignoreCase = true)) {
            val balance = VaultHook.getBalance(player)
            val formatted = convertCompact(balance.toLong())
            return formatted
        }
        return "&cInvalid placeholder.".translate()
    }
}