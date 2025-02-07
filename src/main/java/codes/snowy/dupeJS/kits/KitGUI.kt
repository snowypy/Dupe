package codes.snowy.dupeJS.kits

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.Material
import codes.snowy.dupeJS.utils.translate

class KitGUI(
    private val kitManager: KitManager,
    private val cooldownManager: KitCooldownManager
) {

    fun openKitSelector(player: Player) {
        val kits = kitManager.getAllKits()
        val inventorySize = ((kits.size + 26) / 9) * 9
        val inventory = Bukkit.createInventory(null, inventorySize, "&#feda36&lKITS".translate())

        kits.forEachIndexed { index, kit ->
            if (player.hasPermission(kit.permission)) {
                val canUse = cooldownManager.canUseKit(player.uniqueId, kit)
                val displayName = if (!canUse) {
                    "&#ff0000&l${kit.name}".toUpperCase().translate()
                } else {
                    "&#feda36&l${kit.name}".toUpperCase().translate()
                }

                val lore = if (!canUse) {
                    val remaining = cooldownManager.getRemainingCooldown(player.uniqueId, kit)
                    val hours = remaining / (1000 * 60 * 60)
                    val minutes = (remaining % (1000 * 60 * 60)) / (1000 * 60)
                    val timesClaimed = cooldownManager.getTimesClaimed(player.uniqueId, kit.name)
                    listOf(
                        "",
                        "&#feda36&l| &7Cooldown: &#FF0000${hours}h ${minutes}m".translate(),
                        "&#feda36&l| &7Times Claimed: $timesClaimed".translate(),
                        "",
                        "&#ff0000[On Cooldown]".translate()
                    )
                } else {
                    val timesClaimed = cooldownManager.getTimesClaimed(player.uniqueId, kit.name)
                    listOf(
                        "",
                        "&#feda36&l| &7Times Claimed: $timesClaimed".translate(),
                        "",
                        "&#10f08a[Click to Claim]".translate()
                    )
                }

                val displayItem = ItemStack(Material.valueOf(if (!canUse) "CLOCK" else kit.displayItem)).apply {
                    itemMeta = itemMeta?.apply {
                        setDisplayName(displayName)
                        this.lore = lore
                    }
                }
                inventory.setItem(index, displayItem)
            }
        }

        player.openInventory(inventory)
    }

    fun openKitEditor(player: Player, kitName: String? = null) {
        val inventory = Bukkit.createInventory(
            null, 
            54, 
            "&#feda36&lKIT EDITOR: ${kitName ?: "New Kit"}".translate()
        )
        
        if (kitName != null) {
            val kit = kitManager.getKit(kitName)
            if (kit != null) {
                kit.items.forEachIndexed { index, item ->
                    if (index < 54) {
                        inventory.setItem(index, item.clone())
                    }
                }
            }
        }
        
        player.openInventory(inventory)
    }
} 