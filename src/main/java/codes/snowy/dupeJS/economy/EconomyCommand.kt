package codes.snowy.dupeJS.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.utils.NumberConverter.convertCompact
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("economy|eco|money")
@CommandPermission("dupe.admin")
class EconomyCommand : BaseCommand() {

    @HelpCommand
    fun help(sender: CommandSender, help: co.aikar.commands.CommandHelp) {
        help.showHelp()
    }

    @Subcommand("give")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players 1|10|100|1000|10000|100000|1000000|10000000|100000000|1000000000")
    @Description("Add money to a player's account")
    fun addMoney(sender: CommandSender, playerName: String, amount: Double) {
        val target = Bukkit.getPlayerExact(playerName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't find a player by the name &#ff0000&n$playerName".translate())
            return
        }

        if (!VaultHook.hasEconomy()) {
            sender.sendMessage("&#FF0000&lERROR &8| &fThere was an error with the Economy.".translate())
            return
        }

        val result = VaultHook.deposit(target, amount)
        if (result.isEmpty()) {
            sender.sendMessage("&#00FF00&lSUCCESS &8| &fAdded &#00FF00$${convertCompact(amount)} &7[$${amount.toInt()}]&f to ${target.name}'s balance.".translate())
        } else {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't add the money: $result".translate())
        }
    }

    @Subcommand("take")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players 1|10|100|1000|10000|100000|1000000|10000000|100000000|1000000000")
    @Description("Remove money from a player's account")
    fun removeMoney(sender: CommandSender, playerName: String, amount: Double) {
        val target = Bukkit.getPlayerExact(playerName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't find a player by the name &#ff0000&n$playerName".translate())
            return
        }

        if (!VaultHook.hasEconomy()) {
            sender.sendMessage("&#FF0000&lERROR &8| &fThere was an error with the Economy.".translate())
            return
        }

        val result = VaultHook.withdraw(target, amount)
        if (result.isEmpty()) {
            sender.sendMessage("&#00FF00&lSUCCESS &8| &fTook &#00FF00$${convertCompact(amount)} &7[$${amount.toInt()}]&f from $playerName".translate())
        } else {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't take the money: $result".translate())
        }
    }
} 