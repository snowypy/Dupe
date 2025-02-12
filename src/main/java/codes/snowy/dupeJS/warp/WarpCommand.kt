package codes.snowy.dupeJS.warp

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.command.CommandSender
import codes.snowy.dupeJS.utils.translate
import org.bukkit.entity.Player

@CommandAlias("warp")
class WarpCommand(private val warpGUI: WarpGUI) : BaseCommand() {

    @Default
    fun onWarpCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }
        warpGUI.open(sender)
    }
} 