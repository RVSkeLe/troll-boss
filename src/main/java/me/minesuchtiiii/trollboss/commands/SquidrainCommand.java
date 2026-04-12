package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SquidrainCommand implements CommandExecutor {

    private static final long SCHEDULER_DELAY = 10L;
    private static final long SCHEDULER_PERIOD = 3L;
    private static final int SQUIDS_SPAWN_HEIGHT = 25;

    private final TrollBoss plugin;
    private final int maxSquidsAllowed = 100;
    private int spawnRounds = 0;
    private int scheduledTaskId;

    public SquidrainCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.squidrain")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/squidrain [player] [number]");
            return true;
        }

        if (args.length > 2) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        String targetName = args[0];
        String amountString = args[1];

        // Validate the target player
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return true;
        }

        // Validate if the input amount is a number
        if (!plugin.isInt(amountString)) {
            player.sendMessage(StringManager.PREFIX + "§cError! §e" + amountString + " §cis not a number!");
            return true;
        }

        int amount = Integer.parseInt(amountString);

        // Validate trollability conditions
        if (!canBeTrolled(target, player, amount)) {
            return true;
        }

        // Execute squid rain functionality
        startSquidRain(player, target, amount);

        return true;
    }

    private boolean canBeTrolled(Player target, Player executor, int amount) {

        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
            return false;
        }

        if (amount <= 0) {
            executor.sendMessage(StringManager.PREFIX + "§cNumber has to be bigger than 0!");
            return false;
        }

        if (amount > maxSquidsAllowed) {
            executor.sendMessage(StringManager.PREFIX + "§cCan't use that number, max allowed is " + maxSquidsAllowed + "!");
            return false;
        }

        if (target.isDead()) {
            executor.sendMessage(StringManager.FAILDEAD);
            return false;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.SQUIDRAIN)) {
            executor.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return false;
        }

        return true;
    }

    private void startSquidRain(Player executor, Player target, int amount) {
        TrollManager.activate(target.getUniqueId(), TrollType.SQUIDRAIN);
        plugin.addTroll();
        plugin.getStats().addStats("Squidrain", executor);

        executor.sendMessage(StringManager.PREFIX + "§7" + amount + " §eSquids will rain on §7" + target.getName() + "§e!");

        scheduledTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (spawnRounds < amount) {
                spawnRounds++;
                spawnSquidAbovePlayer(target);
            }
            if (spawnRounds == amount) {
                TrollManager.deactivate(target.getUniqueId(), TrollType.SQUIDRAIN);
                Bukkit.getScheduler().cancelTask(scheduledTaskId);
                spawnRounds = 0;
            }
        }, SCHEDULER_DELAY, SCHEDULER_PERIOD);
    }

    private void spawnSquidAbovePlayer(Player player) {
        Location squidSpawnLocation = player.getLocation().clone().add(0, SQUIDS_SPAWN_HEIGHT, 0);
        Bukkit.getWorld(player.getWorld().getName()).spawnEntity(squidSpawnLocation, EntityType.SQUID);
    }
}
