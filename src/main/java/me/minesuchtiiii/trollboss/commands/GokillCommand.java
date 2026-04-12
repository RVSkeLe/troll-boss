package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class GokillCommand implements CommandExecutor {
    private static final String USAGE_MESSAGE = StringManager.PREFIX + "§eUse §7/gokill [player] [delay]";
    private static final String DELAY_ERROR = StringManager.PREFIX + "§cNumber has to be bigger than 0!";
    private static final String INVALID_NUMBER = StringManager.PREFIX + "§cError! §e%s §cis not a number!";
    private static final String ALREADY_ACTIVE = StringManager.PREFIX + "§cCan't do this right now!";
    private static final String MAX_VALUE_ERROR = StringManager.PREFIX + "§cCan't use that number, max allowed is %d!";

    private final TrollBoss plugin;
    private final int maxDelay = 3600;

    public GokillCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.gokill")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(USAGE_MESSAGE);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        if (!Util.isInt(args[1])) {
            player.sendMessage(String.format(INVALID_NUMBER, args[1]));
            return true;
        }

        int delay = Integer.parseInt(args[1]);
        if (delay <= 0) {
            player.sendMessage(DELAY_ERROR);
            return true;
        }
        if (delay > maxDelay) {
            player.sendMessage(String.format(MAX_VALUE_ERROR, maxDelay));
            return true;
        }

        if (!canTargetBeKilled(player, target)) {
            return true;
        }

        scheduleKill(player, target, delay);
        return true;
    }

    private boolean canTargetBeKilled(Player player, Player target) {
        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return false;
        }
        if (TrollManager.isActive(target.getUniqueId(), TrollType.GOKILL)) {
            player.sendMessage(ALREADY_ACTIVE);
            return false;
        }
        return true;
    }

    private void scheduleKill(Player player, Player target, int delay) {
        plugin.addTroll();
        plugin.getStats().addStats("Gokill", player);

        double delayInMinutes = delay / 60.0;
        DecimalFormat formatter = new DecimalFormat("##.##");
        String formattedMinutes = formatter.format(delayInMinutes);

        player.sendMessage(String.format(
                "%s§7%s §ewill get killed in §7%d §eseconds! §c(~%s minutes)",
                StringManager.PREFIX, target.getName(), delay, formattedMinutes
        ));

        TrollManager.activate(target.getUniqueId(), TrollType.GOKILL);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    target.setHealth(0.0D);
                    TrollManager.deactivate(target.getUniqueId(), TrollType.GOKILL);
                }, delay * 20L);
    }
}
