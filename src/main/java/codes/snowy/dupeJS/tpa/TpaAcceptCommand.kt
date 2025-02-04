package codes.snowy.dupeJS.tpa

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("tpaccept|tpyes|tpaaccept")
class TpaAcceptCommand: BaseCommand() {

    private val tpaManager = TpaManager()

    @Default
    @CommandCompletion("@players")
    fun onTpaAcceptCommand(acceptee: CommandSender, senderName: String?) {
        if (acceptee !is Player) {
            acceptee.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        if (senderName == null) {
            acceptee.sendMessage("&cYou must specify a player to accept the teleport request from.".translate())
            return
        }

        val sender = Bukkit.getPlayer(senderName)
        if (sender == null) {
            acceptee.sendMessage("&cPlayer not found.".translate())
            return
        }

        tpaManager.acceptTeleport(acceptee, sender)
    }
}