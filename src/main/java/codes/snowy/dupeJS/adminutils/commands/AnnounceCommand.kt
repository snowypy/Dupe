package codes.snowy.dupeJS.adminutils.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("announce")
@CommandPermission("dupe.announce")
class AnnounceCommand: BaseCommand() {

    @Default
    fun onCommand(Sender: CommandSender, message: String) {
        if (message.isEmpty()) {
            Sender.sendMessage("&#ff3358&lDUPEY&f&lSTEALY &8| &fPlease provide a message to announce.".translate())
            return
        }
        Bukkit.broadcastMessage("".translate())
        Bukkit.broadcastMessage("&#ff3358&lDUPEY&f&lSTEALY &8| &f${message}".translate())
        Bukkit.broadcastMessage("".translate())
        Bukkit.getOnlinePlayers().forEach {
                    it.playSound(it.location, "block.note_block.pling", 1.0f, 1.0f)
                    it.sendTitle("&#ff3358&lDUPEY&f&lSTEALY".translate(), message.translate(), 10, 70, 20)
        }
    }

}