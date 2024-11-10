package codes.snowy.dupeJS.lifesteal

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.translate
import de.tr7zw.changeme.nbtapi.NBTItem
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("withdraw")
class WithdrawCommand(private val manager: LifestealManager) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    private val config = Config(DupeJS.getInstance())

    @Default
    fun onWithdraw(player: Player, @Optional amount: Int?) {
        val heartsToWithdraw = amount ?: 1
        val healthToWithdraw = heartsToWithdraw * 2

        if (healthToWithdraw < 2) {
            player.sendMessage("&#f6294b&lLIFESTEAL &8| &cYou must withdraw at least 1 heart.".translate())
            return
        }

        if ((manager.getHearts(player) - 2) < healthToWithdraw) {
            player.sendMessage("&#f6294b&lLIFESTEAL &8| &cNot enough hearts to withdraw.".translate())
            return
        }

        manager.removeHearts(player, healthToWithdraw)

        val heartItem = ItemStack(Material.RED_DYE, heartsToWithdraw).apply {
            itemMeta = itemMeta?.apply { setDisplayName("§cHeart") }

        }
        val nbtItem = NBTItem(heartItem)
        nbtItem.setInteger("custom_model_data", config.getInt("dupe.modeldata", 1111))

        player.inventory.addItem(nbtItem.item)
        player.sendMessage("&#f6294b&lLIFESTEAL &8| &aYou have withdrawn $heartsToWithdraw heart(s).".translate())
    }
}
