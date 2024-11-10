package codes.snowy.dupeJS.integration

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.staff.vanish.VanishManager
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player
import me.clip.placeholderapi.expansion.PlaceholderExpansion

class CoreExpansion(
    private val plugin: DupeJS,
    private val language: Language,
    private val config: Config
) : PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return "ghost"
    }

    override fun getAuthor(): String {
        return "snowyjs & invislol"
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun onPlaceholderRequest(player: Player?, identifier: String): String? {
        if (player == null) return "&cError: Player is null".translate()

        return when (identifier.lowercase()) {
            "vanish-tag" -> {
                if (VanishManager.isVanished(player)) {
                    language.getReplacedMessage("staff.vanish.placeholder.tag.true").translate()
                } else {
                    language.getReplacedMessage("staff.vanish.placeholder.tag.false").translate()

                }
            }
            "vanish-status" -> {
                if (VanishManager.isVanished(player)){
                    language.getReplacedMessage("staff.vanish.placeholder.status.true").translate()
                } else {
                    language.getReplacedMessage("staff.vanish.placeholder.status.false").translate()
                }
            }
            "vanish-amount" -> {
                if (VanishManager.getVanishedPlayers().isEmpty()) {
                    return language.getReplacedMessage("staff.vanish.placeholder.amount.false").translate()
                } else {
                    language.getReplacedMessage("staff.vanish.placeholder.amount.true").replace("%amount%", VanishManager.getVanishedPlayers().size.toString())
                }
            }
            "vanish-abovename" -> {
                if (VanishManager.isVanished(player)) {
                    language.getReplacedMessage("staff.vanish.placeholder.abovename.true").translate()
                } else {
                    language.getReplacedMessage("staff.vanish.placeholder.abovename.false").translate()
                }
            }
            else -> "&cError: Placeholder not found".translate()
        }
    }
}