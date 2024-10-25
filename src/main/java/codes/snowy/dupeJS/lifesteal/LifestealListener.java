package codes.snowy.dupeJS.lifesteal;

import codes.snowy.dupeJS.utils.Logger;
import codes.snowy.dupeJS.utils.TranslationKt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LifestealListener implements Listener {
    private final LifestealManager manager;

    public LifestealListener(LifestealManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer.getMaxHealth() <= 1) {
            Logger.INSTANCE.log("Player " + killer.getName() + " has no hearts to steal!", "info");
            return;
        }
        Logger.INSTANCE.log("Player " + victim.getName() + " has died!", "info");

        if (killer != null) {
            if (manager.removeHearts(victim, 2)) {
                manager.addHearts(killer, 2);
                killer.sendMessage(TranslationKt.translate("&cYou have stolen a heart"));
                victim.sendMessage(TranslationKt.translate("&c" + killer + " has stolen a heart from you"));
            }
        }
    }

    @EventHandler
    public void onHeartClaim(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (manager.getHearts(player) >= 50) {
                player.sendMessage(TranslationKt.translate("&cYou have reached the maximum amount of hearts"));
                return;
            }
            if (manager.addHearts(player, 2)) {
                player.sendMessage(TranslationKt.translate("&cYou have claimed a heart"));
                event.setCancelled(true);
                event.getItem().setAmount(event.getItem().getAmount() - 1);
            }
        }
    }
}
