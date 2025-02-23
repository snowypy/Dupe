package codes.snowy.dupeJS.basic

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import codes.snowy.dupeJS.utils.translate
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("store")
class StoreCommand: BaseCommand() {

    @Default
    fun onDiscordCommand(sender: CommandSender) {
        sender.sendMessage("&#bc28fd§m------------------------------------------".translate())
        sender.sendMessage("&e".translate())
        sender.sendMessage("&#bc28fd§lSERVER STORE".translate())
        sender.sendMessage("&d".translate())
        sender.sendMessage("&fSupport devs by buying &#bc28fdRanks & Recharges".translate())
        sender.sendMessage("&c".translate())

        val discordLink = TextComponent("&7[Click to Open]".translate())
        discordLink.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/buy")
        discordLink.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("&#bc28fd&nClick to visit!".translate()))
        sender.spigot().sendMessage(discordLink)

        sender.sendMessage("&l".translate())
        sender.sendMessage("&#bc28fd&m------------------------------------------".translate())

        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        }
    }
}