package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.DatabaseHelper
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("setspawn")
@CommandPermission("dupe.setspawn")
class SetSpawnCommand : BaseCommand() {

    private val dbHelper = DatabaseHelper()

    @Default
    fun onSetSpawnCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage("&cYou must be a player to use this command.".translate())
            return
        }
        sender.sendMessage("&aSetting the spawn point".translate())
        if (sender.world.setSpawnLocation(sender.location)) {
            sender.sendMessage("&aSpawn point set.".translate())
            dbHelper.saveSpawn(
                sender.world.name,
                sender.location.x,
                sender.location.y,
                sender.location.z,
                sender.location.yaw,
                sender.location.pitch
            )
        } else {
            sender.sendMessage("&cFailed to set spawn point.".translate())
        }
    }
}