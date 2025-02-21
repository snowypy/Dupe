package codes.snowy.dupeJS.lifesteal;

import codes.snowy.dupeJS.DupeJS;
import codes.snowy.dupeJS.utils.Config;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifestealManager {

    Config config = new Config(DupeJS.getInstance());

    private final int maxHearts = 50;
    private final Map<UUID, Integer> playerHearts = new HashMap<>();

    public int getHearts(Player player) {
        return (int) player.getMaxHealth();
    }

    public boolean addHearts(Player player, int amount) {
        int currentHearts = getHearts(player);
        int newHearts = Math.min(currentHearts + (amount * 2), maxHearts);
        playerHearts.put(player.getUniqueId(), newHearts);
        player.setMaxHealth(newHearts);
        return newHearts != currentHearts;
    }

    public boolean removeHearts(Player player, int healthAmount) {
        int currentHearts = getHearts(player);
        int newHearts = Math.max(currentHearts - (healthAmount * 2), 2);
        playerHearts.put(player.getUniqueId(), newHearts);
        player.setMaxHealth(newHearts);
        return newHearts != currentHearts;
    }

    public void giveAllHearts(int amount) {
        Bukkit.getOnlinePlayers().forEach(player -> addHearts(player, amount));
    }

    public boolean payHearts(Player sender, Player recipient, int amount) {
        int senderHearts = getHearts(sender);
        int recipientHearts = getHearts(recipient);

        if (senderHearts < amount || recipientHearts + amount > maxHearts) {
            return false;
        }

        removeHearts(sender, (amount));
        addHearts(recipient, (amount));
        return true;
    }

    public void giveHeartItem(Player player, int amount) {
        ItemStack heartItem = new ItemStack(Material.RED_DYE, amount);
        ItemMeta meta = heartItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§cHeart");
            heartItem.setItemMeta(meta);
        }

        NBTItem nbtItem = new NBTItem(heartItem);
        nbtItem.setInteger("custom_model_data", config.getInt("dupe.modeldata", 1111));

        player.getInventory().addItem(nbtItem.getItem());
    }

    public void giveAllHeartItems(int amount) {
        Bukkit.getOnlinePlayers().forEach(player -> giveHeartItem(player, amount));
    }
}
