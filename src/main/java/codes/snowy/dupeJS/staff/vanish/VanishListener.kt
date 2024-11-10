package codes.snowy.dupeJS.staff.vanish

import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.Config
import codes.snowy.dupeJS.utils.Language
import codes.snowy.dupeJS.utils.translate
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin



class VanishListener(private val plugin: JavaPlugin) : Listener {

    private val config = Config(DupeJS.getInstance())
    private val language = Language(DupeJS.getInstance(), config)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    //@EventHandler
    //fun onBlockBreak(event: BlockBreakEvent) {
    //    val player = event.player
    //    if (VanishManager.isVanished(player)) {
    //        if (player.hasPermission("${config.getValue("permissions.vanish.bypass.interact-block")}")) {
    //            return
    //        }
    //        event.isCancelled = true
    //        player.sendMessage(language.getReplacedMessage("staff.vanish.bypass").translate())
    //    }
    //}

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (VanishManager.isVanished(player)) {
            if (player.hasPermission("${config.getValue("permissions.vanish.bypass")}")) {
                return
            }
            event.isCancelled = true
            player.sendMessage(language.getReplacedMessage("staff.vanish.bypass").translate())
        }
    }

    //@EventHandler
    //fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
    //    if (event.damager is Player && event.entity is Player) {
    //        val damager = event.damager as Player
    //        val entity = event.entity as Player
    //        val bypassPermission = config.getValue("permissions.vanish.bypass.damage-players").toString()
    //
    //        if (VanishManager.isVanished(entity) && !damager.hasPermission(bypassPermission)) {
    //            event.isCancelled = true
    //            event.damager.sendMessage(language.getReplacedMessage("staff.vanish.bypass").translate())
    //        }
    //    }
    //}

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val vanishTogglePermission = config.getValue("permissions.vanish.use").toString()

        if (VanishManager.isVanished(player) && !player.hasPermission(vanishTogglePermission)) {
            event.isCancelled = true
        }
    }
}