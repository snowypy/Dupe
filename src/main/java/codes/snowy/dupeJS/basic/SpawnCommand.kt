package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.teleporter.TeleportManager
import codes.snowy.dupeJS.utils.DatabaseHelper
import codes.snowy.dupeJS.utils.Logger
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

@CommandAlias("spawn")
class SpawnCommand: BaseCommand() {

    private val dbHelper = DatabaseHelper()
    private val teleportManager = TeleportManager(DupeJS.getInstance())

    @CommandCompletion("@players")
    @Default
    fun onSpawnCommand(sender: CommandSender, @Optional targetName: String?) {
        if (sender !is Player) {
            sender.sendMessage("&cOnly players can use this command.".translate())
            return
        }

        val spawnLocation = dbHelper.getSpawn() ?: sender.world.spawnLocation
        if (dbHelper.getSpawn() == null) {
            Logger.log("&cSpawn location not set. Using world spawn.".translate(), "warning")
        }

        if (targetName == null) {
            teleportManager.teleportPlayer(sender, spawnLocation, "Spawn")
        } else {
            val target = Bukkit.getPlayer(targetName)
            if (target == null) {
                sender.sendMessage("&cPlayer not found.".translate())
                return
            }
            if (!sender.hasPermission("dupe.sendtospawn")) {
                sender.sendMessage("&cYou do not have permission to teleport others to spawn.".translate())
                return
            }
            sender.sendMessage("&aYou have teleported &f&n${target.name}&a to spawn.".translate())
            target.sendMessage("&#10f08a&lTELEPORTER &8| &#c4f5dfYou have been teleported to \"Spawn\" by &#10f08a&n${sender.name}".translate())
            target.teleport(spawnLocation)
        }
    }
}