package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class NomineCommand implements CommandExecutor {
    private static final String USAGE_MESSAGE = "§eUse §7/nomine [player] [time]";
    private static final String TIME_ERROR = "§cNumber has to be bigger than 0!";
    private static final String MAX_TIME_ERROR = "§cCan't use that number, max allowed is ";
    private static final String CANNOT_DO_NOW = "§cCan't do this right now!";
    private static final String NOT_A_NUMBER = "§cError! §e";
    private static final String MINING_BLOCKED_MESSAGE = " §ewon't be able to mine for §7";
    private static final int MAX_TIME = 3600;
    private final TrollBoss plugin;

    public NomineCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.nomine")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(StringManager.PREFIX + USAGE_MESSAGE);
            return true;
        }

        if (args.length > 2) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        String timeArgument = args[1];

        if (!plugin.isInt(timeArgument)) {
            player.sendMessage(StringManager.PREFIX + NOT_A_NUMBER + timeArgument + " §cis not a number!");
            return true;
        }

        int time = Integer.parseInt(timeArgument);

        if (target == null) {
            plugin.notOnline(player, args[0]);
            return true;
        }

        handleTrollAction(player, target, time);
        return true;
    }

    private void handleTrollAction(Player player, Player target, int time) {
        if (!canBeTrolled(target, player)) return;

        if (time > MAX_TIME) {
            player.sendMessage(StringManager.PREFIX + MAX_TIME_ERROR + MAX_TIME + "!");
            return;
        }

        if (time <= 0) {
            player.sendMessage(StringManager.PREFIX + TIME_ERROR);
            return;
        }

        startMiningBlock(player, target, time);
    }

    private boolean canBeTrolled(Player target, Player player) {
        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return false;
        }
        if (TrollManager.isActive(target.getUniqueId(), TrollType.NOMINE)) {
            player.sendMessage(StringManager.PREFIX + CANNOT_DO_NOW);
            return false;
        }
        return true;
    }

    private void startMiningBlock(Player player, Player target, int time) {
        double timeMinutes = time / 60.0;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        String formattedTime = decimalFormat.format(timeMinutes);

        plugin.addTroll();
        plugin.getStats().addStats("Nomine", player);
        TrollManager.activate(target.getUniqueId(), TrollType.NOMINE);

        player.sendMessage(StringManager.PREFIX + "§7" + target.getName() + MINING_BLOCKED_MESSAGE + time
                + " §eseconds! §c(~" + formattedTime + " minutes)");

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                TrollManager.deactivate(target.getUniqueId(), TrollType.NOMINE), time * 20L);
    }
}
