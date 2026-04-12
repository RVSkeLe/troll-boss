package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LaunchCommand implements CommandExecutor {
    private static final String NOT_ENOUGH_PLAYERS_MSG = StringManager.PREFIX + "§cNot enough players online!";
    private final TrollBoss plugin;

    public LaunchCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.launch")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        switch (args.length) {
            case 0 -> launchPlayer(player, player); // Launch self
            case 1 -> {
                if ("all".equalsIgnoreCase(args[0])) {
                    launchAllPlayers(player);
                } else {
                    Player targetPlayer = Bukkit.getPlayer(args[0]);
                    if (targetPlayer != null) {
                        handleTargetLaunch(player, targetPlayer);
                    } else {
                        plugin.notOnline(player, args[0]);
                    }
                }
            }
            default -> player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void launchPlayer(Player launcher, Player target) {
        if (!plugin.canBeTrolled(target)) {
            launcher.sendMessage(StringManager.BYPASS);
            return;
        }

        target.setVelocity(target.getVelocity().setY(3));
        sendLaunchMessage(launcher, target);
        plugin.addTroll();
        plugin.getStats().addStats("Launch", launcher);
    }

    private void launchAllPlayers(Player launcher) {
        if (Bukkit.getOnlinePlayers().size() <= 1) {
            launcher.sendMessage(NOT_ENOUGH_PLAYERS_MSG);
            return;
        }

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !player.equals(launcher))
                .filter(plugin::canBeTrolled)
                .forEach(plugin::launchPlayer);

        plugin.addTroll();
        plugin.getStats().addStats("Launch", launcher);
        launcher.sendMessage(StringManager.PREFIX + "§eYou launched everyone, except the players who can bypass it!");
    }

    private void handleTargetLaunch(Player launcher, Player target) {
        if (plugin.canBeTrolled(target)) {
            launchPlayer(launcher, target);
        } else {
            launcher.sendMessage(StringManager.BYPASS);
        }
    }

    private void sendLaunchMessage(Player launcher, Player target) {
        if (launcher.equals(target)) {
            launcher.sendMessage(StringManager.PREFIX + "§7You launched yourself!");
        } else {
            launcher.sendMessage(StringManager.PREFIX + "§eYou launched §7" + target.getName() + "§e!");
        }
    }
}
