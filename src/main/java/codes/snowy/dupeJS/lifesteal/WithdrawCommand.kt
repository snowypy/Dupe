package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@CommandAlias("withdraw")
class WithdrawCommand(private val manager: LifestealManager) : BaseCommand() {

    @Default
    fun onWithdraw(player: Player, @Optional amount: Int?) {
        val heartsToWithdraw = amount ?: 2
        val heartsToWithdrawPhyc = amount ?: 1
        if (manager.getHearts(player) <= heartsToWithdraw) {
            player.sendMessage("Not enough hearts to withdraw.")
            return
        }
        manager.removeHearts(player, heartsToWithdraw)

        val heartItem = ItemStack(Material.RED_DYE, heartsToWithdrawPhyc).apply {
            itemMeta = itemMeta?.apply { setDisplayName("§cHeart") }
        }

        player.inventory.addItem(heartItem)
        player.sendMessage("You have withdrawn $heartsToWithdrawPhyc heart(s).")
    }
}
