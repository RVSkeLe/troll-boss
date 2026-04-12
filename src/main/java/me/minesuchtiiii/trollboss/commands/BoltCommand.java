package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BoltCommand implements CommandExecutor {
    private final TrollBoss plugin;

    public BoltCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.bolt")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            boltPlayer(player, player);
            player.sendMessage(StringManager.PREFIX + "§eYou bolted yourself!");
        } else if (args.length == 1) {
            if ("all".equalsIgnoreCase(args[0])) {
                boltAllPlayers(player);
            } else {
                handleTargetedBolt(player, args[0]);
            }
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }


        return true;
    }

    private void boltPlayer(Player executor, Player target) {
        final Location strikeLocation = target.getLocation();
        strikeLocation.getWorld().strikeLightning(strikeLocation);
        plugin.addTroll();
        plugin.getStats().addStats("Bolt", executor);
    }

    private void boltAllPlayers(Player executor) {
        executor.sendMessage(StringManager.PREFIX + "§eYou bolted everyone, except the players who can bypass it!");
        plugin.addTroll();
        plugin.getStats().addStats("Bolt", executor);
        Bukkit.getOnlinePlayers().stream()
                .filter(target -> target != executor && plugin.canBeTrolled(target))
                .forEach(target -> boltPlayer(executor, target));
    }

    private void handleTargetedBolt(Player executor, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(executor, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
            return;
        }

        boltPlayer(executor, target);
        executor.sendMessage(StringManager.PREFIX + "§eYou bolted §7" + target.getName() + "§e!");
        plugin.getStats().updateLastUsedUser(executor, "Bolt");
    }

}
