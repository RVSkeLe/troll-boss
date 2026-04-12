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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RandomTpCommand implements CommandExecutor {

    private final TrollBoss plugin;
    int schedu;
    int max = 50;

    public RandomTpCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.randomtp")) {
            player.sendMessage(StringManager.PREFIX + "§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/randomtp [player] [count]");
            return true;
        }

        if (!plugin.isInt(args[1])) {
            player.sendMessage(StringManager.PREFIX + "§cThe count must be a valid number.");
            return true;
        }

        final int count = Integer.parseInt(args[1]);
        final Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            plugin.notOnline(player, args[0]);
            return true;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.RANDOMTP)) {
            player.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return true;
        }

        if (count <= 0 || count > max) {
            player.sendMessage(StringManager.PREFIX + "§cCount must be between 1 and " + max + ".");
            return true;
        }

        plugin.addTroll();
        plugin.getStats().addStats("Randomteleport", player);
        TrollManager.activate(target.getUniqueId(), TrollType.RANDOMTP);

        player.sendMessage(StringManager.PREFIX + "§7" + target.getName()
                + " §eis being teleported randomly §7" + count + " §etimes!");

        int[] stageBoundaries = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
        schedu = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int teleportCount = 0;

            @Override
            public void run() {
                if (teleportCount >= count) {
                    Bukkit.getScheduler().cancelTask(schedu);
                    TrollManager.deactivate(target.getUniqueId(), TrollType.RANDOMTP);
                    return;
                }

                int stage = getStage(teleportCount, stageBoundaries);
                teleportPlayerInDirection(target, stage);
                teleportCount++;
            }
        }, 0L, 20L); // Run every second (20 ticks)

        return true;
    }

    private int getStage(int teleportCount, int[] boundaries) {
        for (int i = 0; i < boundaries.length; i++) {
            if (teleportCount < boundaries[i]) {
                return i;
            }
        }
        return boundaries.length - 1;
    }

    private void teleportPlayerInDirection(Player target, int stage) {
        Random random = new Random();

        double randomOffsetX = random.nextInt(15);
        double randomOffsetY = random.nextInt(4);
        double randomOffsetZ = random.nextInt(15);

        double offsetX = (stage % 2 == 0) ? randomOffsetX : -randomOffsetX;
        double offsetY = (stage < 3) ? randomOffsetY : -randomOffsetY;
        double offsetZ = (stage % 3 == 0) ? -randomOffsetZ : randomOffsetZ;

        Location randomLocation = new Location(
                target.getWorld(),
                target.getLocation().getX() + offsetX,
                target.getLocation().getY() + offsetY,
                target.getLocation().getZ() + offsetZ
        );

        target.teleport(randomLocation);
    }

}
