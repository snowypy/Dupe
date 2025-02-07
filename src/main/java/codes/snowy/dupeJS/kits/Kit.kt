import org.bukkit.inventory.ItemStack

data class Kit(
    val name: String,
    val permission: String,
    val displayItem: String,
    val items: List<ItemStack>,
    val cooldownHours: Int = 24
) 