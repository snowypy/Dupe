package codes.snowy.dupeJS.lifesteal;

import codes.snowy.dupeJS.dupe.DupeManager;
import codes.snowy.dupeJS.utils.Logger;
import codes.snowy.dupeJS.utils.TranslationKt;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LifestealListener implements Listener {
    private final LifestealManager manager;
    private final DupeManager dupeManager;

    public LifestealListener(LifestealManager manager, DupeManager dupeManager) {
        this.manager = manager;
        this.dupeManager = dupeManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        if (killer.getMaxHealth() <= 1) {
            Logger.INSTANCE.log("Player " + killer.getName() + " has no hearts to steal!", "info");
            return;
        }
        Logger.INSTANCE.log("Player " + victim.getName() + " has died!", "info");

        if (manager.removeHearts(victim, 2)) {
            manager.addHearts(killer, 2);
            killer.sendMessage(TranslationKt.translate("&cYou have stolen a heart"));
            victim.sendMessage(TranslationKt.translate("&c" + killer.getName() + " has stolen a heart from you"));
        }
    }

    @EventHandler
    public void onHeartClaim(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (itemInHand.getType() != Material.RED_DYE) {
            return;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName() ||
                !itemMeta.getDisplayName().equals("§cHeart")) {
            return;
        }

        if (!dupeManager.nbtCheck(itemInHand)) {
            player.sendMessage(TranslationKt.translate("&#f6294b&lLIFESTEAL &8| &cThis item is not allowed to be used as a heart. Sorry!"));
            return;
        }

        if (manager.getHearts(player) >= 50) {
            player.sendMessage(TranslationKt.translate("&#f6294b&lLIFESTEAL &8| &cYou have reached the maximum amount of hearts"));
            return;
        }

        if (manager.addHearts(player, 2)) {
            player.sendMessage(TranslationKt.translate("&#f6294b&lLIFESTEAL &8| &cYou have claimed a heart"));
            event.setCancelled(true);

            int newAmount = itemInHand.getAmount() - 1;
            if (newAmount <= 0) {
                player.getInventory().setItemInMainHand(null);
            } else {
                itemInHand.setAmount(newAmount);
            }
        }
    }
}