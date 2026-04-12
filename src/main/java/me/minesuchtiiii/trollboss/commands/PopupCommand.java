package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PopupCommand implements CommandExecutor {

    private final TrollBoss plugin;

    public PopupCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.popup")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/popup [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        if ("all".equalsIgnoreCase(args[0])) {
            handleAllPlayers(player);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        handleSinglePlayer(player, target);
        return true;
    }

    private void handleAllPlayers(Player player) {
        this.plugin.addTroll();
        plugin.getStats().addStats("Popup", player);
        player.sendMessage(StringManager.PREFIX + "§eYou opened the inventory of everyone!");

        Bukkit.getOnlinePlayers().stream()
                .filter(other -> other != player && plugin.canBeTrolled(other))
                .forEach(target -> target.openInventory(target.getInventory()));
    }

    private void handleSinglePlayer(Player player, Player target) {
        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return;
        }

        if (plugin.canBeTrolled(target)) {
            target.openInventory(target.getInventory());
            player.sendMessage(StringManager.PREFIX + "§eOpened the inventory of §7" + target.getName() + "§e!");
            this.plugin.addTroll();
            plugin.getStats().addStats("Popup", player);
        } else {
            player.sendMessage(StringManager.BYPASS);
        }
    }

}
