package codes.snowy.dupeJS.tpa

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("tpadeny|tpdeny")
class TpaDenyCommand: BaseCommand() {

    private val tpaManager = TpaManager()

    @Default
    @CommandCompletion("@players")
    fun onTpaDenyCommand(acceptee: CommandSender, senderName: String) {
        if (acceptee !is Player) {
            acceptee.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        if (senderName == null) {
            acceptee.sendMessage("&cPlease specify a player to teleport to.".translate())
            return
        }

        val sender = Bukkit.getPlayer(senderName)
        if (sender == null) {
            acceptee.sendMessage("&cPlayer not found.".translate())
            return
        }

        tpaManager.denyTeleport(acceptee, sender)

    }

}