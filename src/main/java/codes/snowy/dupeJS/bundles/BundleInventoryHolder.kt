package codes.snowy.dupeJS.bundles

import codes.snowy.dupeJS.bundles.Bundle
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class BundleInventoryHolder(val bundle: Bundle, val type: String) : InventoryHolder {
    private val inventory: Inventory = Bukkit.createInventory(this, 27)

    override fun getInventory(): Inventory {
        return inventory
    }
}
