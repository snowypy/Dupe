package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("store")
class StoreCommand: BaseCommand() {

    @Default
    fun onStoreCommand(sender: CommandSender) {
        sender.sendMessage("&#6bf52f&l|".translate())
        sender.sendMessage("&#6bf52f&l| SERVER STORE".translate())
        sender.sendMessage("&#6bf52f&l| &fPurchase from our store to get stacked faster!".translate())

        val discordLink = TextComponent("§x§6§B§F§5§2§F&l| §x§6§B§F§5§2§F&nhttps://store.dupeystealy.com".translate())
        discordLink.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.dupeystealy.com")
        discordLink.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("&#6bf52f&nClick to browse!".translate()))
        sender.spigot().sendMessage(discordLink)

        sender.sendMessage("&#6bf52f&l|".translate())

        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        }
    }

}