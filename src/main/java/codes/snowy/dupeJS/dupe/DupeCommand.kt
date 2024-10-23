package codes.snowy.dupeJS.dupe

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("dupe")
class DupeCommand(val dupeManager: DupeManager) : BaseCommand() {

    @Default
    @CommandCompletion("1|2|3|4|5|6|7|8|9")
    @Description("Duplicate the item in your hand")
    fun onDupeCommand(sender: CommandSender) {
        dupeManager.dupe(sender as Player)
    }
}