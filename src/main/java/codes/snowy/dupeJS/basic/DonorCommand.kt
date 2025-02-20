package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.DatabaseHelper
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("donor")
@CommandPermission("dupe.donator")
class DonorCommand : BaseCommand() {

    private val dbHelper = DatabaseHelper()
    val teleportManager = TeleportManager(DupeJS.getInstance())

    @Default
    fun onDonorCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        if (!sender.hasPermission("dupe.donator")) {
            sender.sendMessage("&cYou do not have permission to use this command.".translate())
            return
        }

        val donorLocation = dbHelper.getDonorLocation()
        if (donorLocation == null) {
            sender.sendMessage("&#FF0000&nCouldn't grab the donator location from database.".translate())
            return
        }

        teleportManager.teleportPlayer(sender, donorLocation, "Donor")
    }
}