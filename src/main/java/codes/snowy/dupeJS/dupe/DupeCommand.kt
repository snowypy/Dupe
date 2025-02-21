package codes.snowy.dupeJS.dupe

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.translate
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("dupe")
class DupeCommand(val dupeManager: DupeManager) : BaseCommand() {

    @HelpCommand
    @Syntax("[query]")
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Default
    @CommandCompletion("1|2|3|4|5|6|7|8|9")
    @Description("Duplicate the item in your hand")
    fun onDupeCommand(sender: CommandSender, @Optional amount: Int?) {
        val player = sender as Player
        val dupeAmount = amount ?: 1

        if (dupeAmount !in 1..9) {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fPlease specify a number between 1 and 9.".translate())
            return
        }

        if (!player.hasPermission("dupe.use.${dupeAmount}")) {
            player.sendMessage("&#ff3358&lDUPE &8| &#ff0000&nHey!&r &fYou don't have permission to dupe ${dupeAmount} items at once.".translate())
            return
        }

        dupeManager.dupe(player, dupeAmount)
    }
}