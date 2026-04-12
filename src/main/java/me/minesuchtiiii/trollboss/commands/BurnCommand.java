package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BurnCommand implements CommandExecutor {

    private static final int FIRE_TICK_DURATION = 100000000; // Introduced constant to improve clarity
    private final TrollBoss plugin;

    public BurnCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.burn")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            burnPlayer(player, player); // Burning the sender itself
            player.sendMessage(StringManager.PREFIX + "§7You're on fire now!");
        } else if (args.length == 1) {
            if ("all".equalsIgnoreCase(args[0])) {
                burnAllPlayers(player);
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    burnSpecificPlayer(player, target);
                } else {
                    plugin.notOnline(player, args[0]);
                }
            }
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void burnPlayer(Player executor, Player target) {
        target.setFireTicks(FIRE_TICK_DURATION);
        plugin.addTroll();
        plugin.getStats().addStats("Burn", executor);
    }

    private void burnAllPlayers(Player executor) {
        plugin.addTroll();
        plugin.getStats().addStats("Burn", executor);

        executor.sendMessage(StringManager.PREFIX + "§eYou burned everyone, except the players who can bypass it!");
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player != executor && plugin.canBeTrolled(player)) // Exclude the executor themselves
                .forEach(player -> player.setFireTicks(FIRE_TICK_DURATION));
    }

    private void burnSpecificPlayer(Player executor, Player target) {
        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
        } else {
            burnPlayer(executor, target);
            executor.sendMessage(StringManager.PREFIX + "§eYou burned §7" + target.getName() + "§e!");
        }
    }
}
