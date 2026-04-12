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

public class DenymoveCommand implements CommandExecutor {
    private static final String INSUFFICIENT_ARGS_MSG = StringManager.PREFIX + "§eUse §7/denymove [player] [delay]";
    private static final String INVALID_NUMBER_MSG_TEMPLATE = StringManager.PREFIX + "§cError! §e%s §cis not a number!";
    private static final int MAX_DURATION = 3600;
    private final TrollBoss plugin;

    public DenymoveCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.denymove")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(INSUFFICIENT_ARGS_MSG);
            return true;
        }

        String targetName = args[0];
        String delayArg = args[1];

        if (!Util.isInt(delayArg)) {
            player.sendMessage(String.format(INVALID_NUMBER_MSG_TEMPLATE, delayArg));
            return true;
        }

        int delayInSeconds = Integer.parseInt(delayArg);
        if (delayInSeconds <= 0) {
            player.sendMessage(StringManager.PREFIX + "§cNumber has to be bigger than 0!");
            return true;
        }

        if (delayInSeconds > MAX_DURATION) {
            player.sendMessage(StringManager.PREFIX + "§cCan't use that number, max allowed is " + MAX_DURATION + "!");
            return true;
        }

        handleSinglePlayer(player, targetName, delayInSeconds);
        return true;
    }

    private void handleSinglePlayer(Player sender, String targetName, int delayInSeconds) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(sender, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            sender.sendMessage(StringManager.BYPASS);
            return;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.DENYMOVE)) {
            sender.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return;
        }

        addDenymove(target, delayInSeconds);
        sender.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §ewon't be able to move for §7" + delayInSeconds
                + " §eseconds! §c(~" + formatMinutes(delayInSeconds) + " minutes)");
    }

    private void addDenymove(Player target, int delayInSeconds) {
        TrollManager.activate(target.getUniqueId(), TrollType.DENYMOVE);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            TrollManager.deactivate(target.getUniqueId(), TrollType.DENYMOVE);
        }, delayInSeconds * 20L);
    }

    private String formatMinutes(int seconds) {
        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(seconds / 60.0D);
    }
}
