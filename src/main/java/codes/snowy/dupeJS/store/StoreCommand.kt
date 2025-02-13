package codes.snowy.dupeJS.store

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("authstore")
@CommandPermission("dupejs.admin")
class StoreCommand: BaseCommand() {

    private val storeManager: StoreManager = StoreManager()

    @Default
    @CommandCompletion("@players 1.99|2.99|3.99 &e&lTITAN|&d&lULTRA")
    fun onStoreCommand(sender: CommandSender, targetName: String, price: String, packageString: String) {
        if (sender is Player) {
            sender.sendMessage("&#FF0000&lADMIN &8| &fOnly &#FF0000&nCONSOLE &fcan use this command.".translate())
            return
        }

        val target = Bukkit.getPlayer(targetName)

        if (target != null) {
            storeManager.sendStoreMessage(target, packageString, price)
        } else {
            sender.sendMessage("&#FF0000&lADMIN &8| &fPlayer not found.".translate())
        }

    }

}