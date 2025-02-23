package codes.snowy.dupeJS.teams

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import codes.snowy.dupeJS.utils.translate
import java.util.UUID

class TeamListener(private val teamManager: TeamManager) : Listener {
    private val pendingDeletions = mutableMapOf<UUID, String>()

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        if (event.view.title != "&cConfirm Team Deletion".translate()) return

        event.isCancelled = true
        val item = event.currentItem ?: return

        when (item.type) {
            Material.LIME_CONCRETE -> {
                val teamName = pendingDeletions[player.uniqueId] ?: return
                if (teamManager.deleteTeam(teamName, player.name)) {
                    player.sendMessage("&#00FF00&lTEAMS &8| &fTeam '&a$teamName&f' has been deleted.".translate())
                }
                pendingDeletions.remove(player.uniqueId)
                player.closeInventory()
            }
            Material.RED_CONCRETE -> {
                player.sendMessage("&#FF0000&lTEAMS &8| &fTeam deletion cancelled.".translate())
                pendingDeletions.remove(player.uniqueId)
                player.closeInventory()
            }
            else -> {}
        }
    }

    fun openDeleteConfirmation(player: Player, teamName: String) {
        val inventory = Bukkit.createInventory(null, 27, "&cConfirm Team Deletion".translate())

        val confirmItem = ItemStack(Material.LIME_CONCRETE)
        val confirmMeta = confirmItem.itemMeta
        confirmMeta?.setDisplayName("&aConfirm Delete".translate())
        confirmItem.itemMeta = confirmMeta
        inventory.setItem(12, confirmItem)

        val cancelItem = ItemStack(Material.RED_CONCRETE)
        val cancelMeta = cancelItem.itemMeta
        cancelMeta?.setDisplayName("&cCancel".translate())
        cancelItem.itemMeta = cancelMeta
        inventory.setItem(14, cancelItem)

        pendingDeletions[player.uniqueId] = teamName
        player.openInventory(inventory)
    }
} 