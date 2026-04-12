package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.items.AK47Item;
import me.minesuchtiiii.trollboss.items.BlockShooterItem;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SpecialCommand implements CommandExecutor {
    private final TrollBoss plugin;

    public SpecialCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.special")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/special [number] [player]");
            return true;
        }

        String specialNumber = args[0];
        if (args.length == 1) {
            handleSelfSpecial(player, specialNumber);
        } else if (args.length == 2) {
            handleTargetSpecial(player, specialNumber, args[1]);
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void handleSelfSpecial(Player player, String specialNumber) {
        switch (specialNumber) {
            case "1" -> giveSpecialItem(player, AK47Item.create(), "§e#1");
            case "2" -> giveSpecialItem(player, BlockShooterItem.create(), "§e#2");
            default -> player.sendMessage(StringManager.PREFIX + "§cInvalid special number!");
        }
    }

    private void handleTargetSpecial(Player player, String specialNumber, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(player, targetName);
            return;
        }

        switch (specialNumber) {
            case "1" -> giveSpecialItemToTarget(player, target, AK47Item.create(), AK47Item.NAME_STRING);
            case "2" -> giveSpecialItemToTarget(player, target, BlockShooterItem.create(), BlockShooterItem.NAME_STRING);
            default -> player.sendMessage(StringManager.PREFIX + "§cInvalid special number!");
        }
    }

    private void giveSpecialItem(Player player, ItemStack item, String specialNumber) {
        player.sendMessage(StringManager.PREFIX + "§7Here's the special " + specialNumber + "§7!");
        this.plugin.addTroll();
        plugin.getStats().addStats("Special", player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            player.getInventory().addItem(item);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }, 40L);
    }

    private void giveSpecialItemToTarget(Player sender, Player target, ItemStack item, String specialName) {
        target.sendMessage(StringManager.PREFIX + "§7You're getting something special: " + specialName + "!");
        this.plugin.addTroll();
        plugin.getStats().addStats("Special", sender);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            target.getInventory().addItem(item);
            sender.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }, 40L);
    }
}
