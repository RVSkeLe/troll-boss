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
import org.jetbrains.annotations.NotNull;

public class PumpkinheadCommand implements CommandExecutor {

    private static final ItemStack PUMPKIN_HEAD = new ItemStack(Material.CARVED_PUMPKIN);
    private final TrollBoss plugin;

    public PumpkinheadCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.pumpkinhead")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            givePumpkinHead(player, player);
            return true;
        } else if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);

            if (targetPlayer == null) {
                Util.notOnline(player, args[0]);
                return true;
            }

            if (!plugin.canBeTrolled(targetPlayer)) {
                player.sendMessage(StringManager.BYPASS);
                return true;
            }

            givePumpkinHead(player, targetPlayer);
            return true;
        }

        player.sendMessage(StringManager.MUCHARGS);
        return true;
    }

    private void givePumpkinHead(Player executor, Player target) {
        target.getInventory().setHelmet(PUMPKIN_HEAD);
        executor.sendMessage(String.format("%s§eGave the §7pumpkinhead §eto §7%s§e!", StringManager.PREFIX, target.getName()));
        plugin.addTroll();
        plugin.getStats().addStats("Pumpkinhead", executor);
    }
}
