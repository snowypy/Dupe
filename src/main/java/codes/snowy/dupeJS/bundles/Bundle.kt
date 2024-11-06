package codes.snowy.dupeJS.bundles

import org.bukkit.inventory.ItemStack

data class Bundle(
    val name: String,
    val color: String,
    val displayName: String,
    val items: List<ItemStack>
)
