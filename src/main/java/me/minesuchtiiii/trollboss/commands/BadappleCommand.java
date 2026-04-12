package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BadappleCommand implements CommandExecutor {

    private final TrollBoss plugin;

    public BadappleCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.badapple")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            handleAppleForPlayer(player);
        } else if (args.length == 1) {
            if ("all".equalsIgnoreCase(args[0])) {
                handleAppleForAll(player);
            } else {
                handleTargetedApple(player, args[0]);
            }
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }
        return true;
    }

    private void handleAppleForPlayer(Player player) {
        addAppleAndNotify(player, player, true);
        this.plugin.addTroll();
        plugin.getStats().addStats("Badapple", player);
    }

    private void handleAppleForAll(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer != player && plugin.canBeTrolled(onlinePlayer)) {
                onlinePlayer.getInventory().addItem(getAppleItem());
            }
        }
        this.plugin.addTroll();
        plugin.getStats().addStats("Badapple", player);
        player.sendMessage(StringManager.PREFIX + "§eThe §7troll apple §ehas been added to everyone's inventory, except the players who can bypass it!");
    }

    private void handleTargetedApple(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(sender, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            sender.sendMessage(StringManager.BYPASS);
            return;
        }

        addAppleAndNotify(sender, target, false);
        this.plugin.addTroll();
        plugin.getStats().addStats("Badapple", sender);

    }

    private void addAppleAndNotify(Player sender, Player target, boolean self) {
        target.getInventory().addItem(getAppleItem());
        if (self) {
            sender.sendMessage(StringManager.PREFIX + "§eThe §7troll apple §ehas been added to your inventory!");
        } else {
            target.sendMessage("§c§lA special apple has been added to your inventory. You shall eat it and see what happens.");
            sender.sendMessage(StringManager.PREFIX + "§eThe §7troll apple §ehas been added to §7" + target.getName() + "§e's inventory!");
        }
    }

    private ItemStack getAppleItem() {
        final ItemStack apple = new ItemStack(Material.APPLE);
        final ItemMeta meta = apple.getItemMeta();
        meta.setDisplayName("§c§lSpecial apple");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("§cIf you eat this apple, you become Chuck Norris");
        meta.setLore(lore);
        apple.setItemMeta(meta);
        return apple;
    }
}
