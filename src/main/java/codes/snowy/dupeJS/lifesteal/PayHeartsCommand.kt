package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import org.bukkit.entity.Player

@CommandAlias("payhearts")
class PayHeartsCommand(private val manager: LifestealManager) : BaseCommand() {

    @Default
    fun onPayHearts(sender: Player, target: Player, @Optional amount: Int?) {
        val heartsToPay = amount ?: 1
        if (heartsToPay < 1) {
            sender.sendMessage("You must pay at least 1 heart.")
            return
        }

        // Use the manager's payHearts method to handle the transaction and validation
        if (manager.payHearts(sender, target, heartsToPay)) {
            sender.sendMessage("You paid $heartsToPay heart(s) to ${target.name}.")
            target.sendMessage("You received $heartsToPay heart(s) from ${sender.name}.")
        } else {
            sender.sendMessage("Transaction failed. Either you don’t have enough hearts, or ${target.name} would exceed the heart cap.")
        }
    }
}