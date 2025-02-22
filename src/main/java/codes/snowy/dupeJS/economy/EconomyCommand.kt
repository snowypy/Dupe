package codes.snowy.dupeJS.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import codes.snowy.dupeJS.DupeJS
import codes.snowy.dupeJS.utils.NumberConverter.convertCompact
import codes.snowy.dupeJS.utils.translate
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.random.Random

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
            sender.sendMessage("&#00FF00&lSUCCESS &8| &fAdded &#00FF00$${convertCompact(amount.toLong())} &7[$${amount.toLong().toString().replace(".0", "")}]&f to ${target.name}'s balance.".translate())
        } else {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't add the money: $result".translate())
        }
    }

    @Subcommand("givepouch")
    @Syntax("<player> <size>")
    @CommandCompletion("@players small|medium|sizeable|huge")
    @Description("Gives a money pouch to a player.")
    fun givePouch(sender: CommandSender, playerName: String, size: String) {
        val target = Bukkit.getPlayerExact(playerName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't find a player by the name &#ff0000&n$playerName".translate())
            return
        }

        val amount = when (size.toLowerCase()) {
            "small" -> Random.nextInt(25000, 100000)
            "medium" -> Random.nextInt(75000, 255000)
            "sizeable" -> Random.nextInt(235000, 465000)
            "huge" -> Random.nextInt(450000, 1000000)
            else -> {
                sender.sendMessage("&#FF0000&lERROR &8| &fInvalid pouch size specified.".translate())
                return
            }
        }

        VaultHook.deposit(target, amount.toDouble())
        
        target.sendTitle("&6&lOPENING...".translate(), "&fYou are opening a $size money pouch!".translate(), 10, 70, 20)
        target.playSound(target.location, "entity.firework.blast", 1f, 1f)
        Bukkit.getScheduler().runTaskLater(DupeJS.getInstance(), object : Runnable {
            override fun run() {
                target.sendTitle("&6&lOPENED".translate(), "&fYou found a whopping &#00FF00&n$${convertCompact(amount.toLong())}".translate(), 10, 70, 20)
            target.sendMessage("&#00FF00&lSUCCESS &8| &fYou opened a $size money pouch with &#00FF00&n${convertCompact(amount.toLong())}&f in it".translate())
                target.playSound(target.location, "entity.firework.blast", 1f, 1f)
            }
        }, 60L)

        sender.sendMessage("&#00FF00&lSUCCESS &8| &fYou gave ${target.name} a $size money pouch with &#00FF00&n${convertCompact(amount.toLong())}&f in it!".translate())
        
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
            sender.sendMessage("&#00FF00&lSUCCESS &8| &fTook &#00FF00$${convertCompact(amount.toLong())} &7[$${amount.toLong().toString().replace(".0", "")}]&f from $playerName".translate())
        } else {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't take the money: $result".translate())
        }
    }

    @Subcommand("set")
    @Syntax("<player> <amount>")
    @CommandCompletion("@players 1|10|100|1000|10000|100000|1000000|10000000|100000000|1000000000")
    @Description("Set a player's balance")
    fun setBalance(sender: CommandSender, playerName: String, amount: Double) {
        val target = Bukkit.getPlayerExact(playerName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't find a player by the name &#ff0000&n$playerName".translate())
            return
        }

        if (!VaultHook.hasEconomy()) {
            sender.sendMessage("&#FF0000&lERROR &8| &fThere was an error with the Economy.".translate())
            return
        }

        val oldbal = VaultHook.getBalance(target)
        VaultHook.withdraw(target, oldbal)
        VaultHook.deposit(target, amount)
        sender.sendMessage("&#00FF00&lSUCCESS &8| &fSet ${target.name}'s balance to &#00FF00&n$${convertCompact(amount.toLong())}".translate())
    }

    @Subcommand("reset")
    @Syntax("<player>")
    @CommandCompletion("@players")
    @Description("Reset a player's balance")
    fun resetBalance(sender: CommandSender, playerName: String) {
        val target = Bukkit.getPlayerExact(playerName)
        if (target == null) {
            sender.sendMessage("&#FF0000&lERROR &8| &fCouldn't find a player by the name &#ff0000&n$playerName".translate())
            return
        }

        if (!VaultHook.hasEconomy()) {
            sender.sendMessage("&#FF0000&lERROR &8| &fThere was an error with the Economy.".translate())
            return
        }
        val oldbal = VaultHook.getBalance(target)
        VaultHook.withdraw(target, oldbal)
        sender.sendMessage("&#00FF00&lSUCCESS &8| &fReset ${target.name}'s balance.".translate())
    }
    
} 