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

@CommandAlias("discord")
class DiscordCommand: BaseCommand() {

    @Default
    fun onDiscordCommand(sender: CommandSender) {
        sender.sendMessage("&#2f65f5&l|".translate())
        sender.sendMessage("&#2f65f5&l| DISCORD SERVER".translate())
        sender.sendMessage("&#2f65f5&l| &fJoin our discord server to get help, report bugs, and more!".translate())

        val discordLink = TextComponent("§x§2§F§6§5§F§5&l| §x§2§F§6§5§F§5&nhttps://discord.gg/dupeystealy".translate())
        discordLink.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/dupeystealy")
        discordLink.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("&#2f65f5&nClick to join!".translate()))
        sender.spigot().sendMessage(discordLink)

        sender.sendMessage("&#2f65f5&l|".translate())

        if (sender is Player) {
            sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        }
    }
}