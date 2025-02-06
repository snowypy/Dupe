package codes.snowy.dupeJS.adminutils.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import codes.snowy.dupeJS.utils.DatabaseHelper
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("admin")
@CommandPermission("dupe.admin")
class AdminCommand : BaseCommand() {

    private val dbHelper = DatabaseHelper()

    @Subcommand("setdonorarea")
    fun onSetDonorAreaCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("&cYou must be a player to use this command.".translate())
            return
        }

        val location = sender.location
        location.world?.let {
            dbHelper.saveDonorLocation(
                it.name,
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch
            )
        }
        sender.sendMessage("&aDonor area location set.".translate())
    }
}