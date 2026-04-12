package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.items.TeleportTrollItem;
import me.minesuchtiiii.trollboss.utils.StringManager;
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
import java.util.List;

public class TptrollCommand implements CommandExecutor {

    private static final long ITEM_DELAY_TICKS = 40L;
    private final TrollBoss plugin;

    public TptrollCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.tptroll")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            giveTrollItemWithDelay(player, player);
            player.sendMessage(StringManager.PREFIX + "§7Here's the item for this troll!");
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                giveTrollItemWithDelay(player, target);
                player.sendMessage(StringManager.PREFIX + "§eYou gave the teleport troll item to §7" + target.getName() + "§e!");
                target.sendMessage(StringManager.PREFIX + "§7You're getting something special!");
            } else {
                plugin.notOnline(player, args[0]);
            }
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void giveTrollItemWithDelay(Player giver, Player receiver) {
        ItemStack item = TeleportTrollItem.create();
        plugin.addTroll();
        plugin.getStats().addStats("Teleporttroll", giver);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> receiver.getInventory().addItem(item), ITEM_DELAY_TICKS);
    }
}
