package codes.snowy.dupeJS.lifesteal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifestealManager {
    private final int maxHearts = 50;
    private final Map<UUID, Integer> playerHearts = new HashMap<>();

    public int getHearts(Player player) {
        return (int) player.getMaxHealth();
    }

    public boolean addHearts(Player player, int amount) {
        int currentHearts = getHearts(player);
        int newHearts = Math.min(currentHearts + amount, maxHearts);
        playerHearts.put(player.getUniqueId(), newHearts);
        player.setMaxHealth(newHearts);
        return newHearts != currentHearts;
    }

    public boolean removeHearts(Player player, int healthAmount) {
        int currentHearts = getHearts(player);
        int newHearts = Math.max(currentHearts - healthAmount, 2);
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

        removeHearts(sender, amount);
        addHearts(recipient, amount);
        return true;
    }
}
