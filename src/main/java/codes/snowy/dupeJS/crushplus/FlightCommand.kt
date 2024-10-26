package codes.snowy.dupeJS.crushplus

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("fly")
class FlightCommand(private val crushPlusManager: CrushPlusManager) : BaseCommand() {

    @Default
    fun onFlyCommand(player: Player) {
        crushPlusManager.toggleFlight(player)
    }
}
