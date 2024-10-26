package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("withdraw")
class WithdrawCommand(private val manager: LifestealManager) : BaseCommand() {

    @Default
    fun onWithdraw(player: Player, @Optional amount: Int?) {
        val heartsToWithdraw = amount ?: 1
        val healthToWithdraw = heartsToWithdraw * 2

        if (manager.getHearts(player) < healthToWithdraw) {
            player.sendMessage("Not enough hearts to withdraw.")
            return
        }

        manager.removeHearts(player, healthToWithdraw)

        val heartItem = ItemStack(Material.RED_DYE, heartsToWithdraw).apply {
            itemMeta = itemMeta?.apply { setDisplayName("§cHeart") }
        }

        player.inventory.addItem(heartItem)
        player.sendMessage("You have withdrawn $heartsToWithdraw heart(s).")
    }
}
