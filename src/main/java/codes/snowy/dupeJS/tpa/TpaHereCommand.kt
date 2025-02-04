package codes.snowy.dupeJS.tpa

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("tpahere")
class TpaHereCommand: BaseCommand() {

    val tpaManager = TpaManager()

    @Default
    @CommandCompletion("@players")
    fun onTpaHereCommand(sender: CommandSender, targetName: String) {

        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        if (targetName == null) {
            sender.sendMessage("&cPlease specify a player to teleport to.".translate())
            return
        }

        if (targetName == sender.name) {
            sender.sendMessage("&cYou cannot teleport to yourself.".translate())
            return
        }

        val target = Bukkit.getPlayer(targetName)

        if (target == null) {
            sender.sendMessage("&cPlayer not found.".translate())
            return
        }

        tpaManager.requestTeleportHere(sender, target)

    }
}