package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BuryCommand implements CommandExecutor {

    private static final int MAX_BURY_TIME = 3600; // Max bury time in seconds (1hr)
    private static final int DEFAULT_BURY_DEPTH = 4; // Default bury depth in blocks
    private final TrollBoss plugin;

    public BuryCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.bury")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(StringManager.PREFIX + "§eUsage: §7/bury [player] [time]");
            return true;
        }

        String targetName = args[0];
        String timeInput = args[1];

        // Validate time input
        if (!plugin.isInt(timeInput)) {
            player.sendMessage(StringManager.PREFIX + "§cInvalid time. Please use a valid number.");
            return true;
        }

        int buryTime = Integer.parseInt(timeInput);
        if (buryTime <= 0 || buryTime > MAX_BURY_TIME) {
            player.sendMessage(StringManager.PREFIX + "§cTime must be between 1 and " + MAX_BURY_TIME + " seconds.");
            return true;
        }

        // Validate target player
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(player, targetName);
            return true;
        }

        if (!canBeBuried(target, player)) {
            return true;
        }

        buryPlayer(player, target, buryTime);
        return true;
    }

    private boolean canBeBuried(Player target, Player executor) {
        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
            return false;
        }
        if (TrollManager.isActive(target.getUniqueId(), TrollType.BURY)) {
            executor.sendMessage(StringManager.PREFIX + "§cThis player is already buried.");
            return false;
        }
        return true;
    }

    private void buryPlayer(Player executor, Player target, int time) {
        savePlayerState(target);

        double newY = target.getLocation().getY() - DEFAULT_BURY_DEPTH;
        scheduleUnbury(target, target.getLocation(), time);
        teleportPlayerTo(target, newY);

        executor.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §eis buried for §7" + time + " §eseconds!");

        plugin.addTroll();
        plugin.getStats().addStats("Bury", executor);
    }

    private void savePlayerState(Player target) {
        Location location = target.getLocation();
        plugin.yloc.put(target.getName(), location.getY());
        plugin.pitch.put(target.getName(), location.getPitch());
        plugin.yaw.put(target.getName(), location.getYaw());
        TrollManager.activate(target.getUniqueId(), TrollType.BURY);
    }

    private void teleportPlayerTo(Player target, double newY) {
        Location location = target.getLocation();
        target.teleport(new Location(location.getWorld(), location.getX(), newY, location.getZ()));
    }

    private void scheduleUnbury(Player target, Location oldLocation, int time) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            String targetName = target.getName();

            TrollManager.deactivate(target.getUniqueId(), TrollType.BURY);
            plugin.yloc.remove(targetName);

            target.teleport(oldLocation);

            plugin.pitch.remove(targetName);
            plugin.yaw.remove(targetName);
        }, time * 20L);
    }
}
