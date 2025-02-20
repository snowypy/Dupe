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

@CommandAlias("discord")
class DiscordCommand: BaseCommand() {

    @Default
    fun onDiscordCommand(sender: CommandSender) {
        sender.sendMessage("&#2489ee§m------------------------------------------".translate())
        sender.sendMessage("&e".translate())
        sender.sendMessage("&#2489ee§lDISCORD SERVER".translate())
        sender.sendMessage("&d".translate())
        sender.sendMessage("&fJoin our Discord for &#2489eeUpdates & Giveaways".translate())
        sender.sendMessage("&c".translate())

        val discordLink = TextComponent("&7[Click to Join]".translate())
        discordLink.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/dupe")
        discordLink.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("&#2489ee&nClick to join our Discord!".translate()))
        sender.spigot().sendMessage(discordLink)

        sender.sendMessage("&l".translate())
        sender.sendMessage("&#2489ee&m------------------------------------------".translate())

        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        }
    }
}