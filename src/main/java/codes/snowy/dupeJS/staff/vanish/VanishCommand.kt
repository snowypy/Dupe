package codes.snowy.dupeJS.staff.vanish

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Description
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import org.bukkit.entity.Player

class VanishCommand : BaseCommand() {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)
    private val luckPerms: LuckPerms = LuckPermsProvider.get()

    @CommandAlias("v|vanish")
    @Description("Toggle vanish mode")
    fun onVanishCommand(player: Player) {

        if (!player.hasPermission("${config.getValue("permissions.vanish.use")}")) {
            player.sendMessage(language.getReplacedMessage("staff.vanish.no-permission").translate())
            return
        }

        val isVanished = VanishManager.toggleVanish(player)

        if (isVanished) {
            player.sendMessage(language.getReplacedMessage("staff.vanish.toggle-self").translate())
        } else {
            player.sendMessage(language.getReplacedMessage("staff.vanish.untoggle-self").translate())
        }

        val vanishStatus = if (isVanished) "vanished" else "visible"
        val notifyPermission = config.getValue("permissions.vanish.notify-others").toString()
        val user: User = luckPerms.userManager.getUser(player.uniqueId) ?: return
        val prefix = user.cachedData.metaData.prefix ?: ""

        player.server.onlinePlayers
            .filter { it.hasPermission(notifyPermission) }
            .forEach { it.sendMessage(
                language.getReplacedMessage("staff.vanish.gen-toggle-others")
                    .translate()
                    .replace("%player%", player.name)
                    .replace("%vanish-status%", vanishStatus)
                    .replace("%prefix%", prefix.translate())
            ) }
    }
}