package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BorderCommand implements CommandExecutor {

    private static final String PERM_TROLL_BORDER = "troll.border";
    private static final String PLAYER_HELP_MESSAGE = StringManager.PREFIX + "§eUse §7/border [player]";

    private static final int MARGIN = 5;

    private final TrollBoss plugin;

    public BorderCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERM_TROLL_BORDER)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PLAYER_HELP_MESSAGE);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        handleTargetTeleport(player, args[0]);
        return true;
    }

    private void handleTargetTeleport(Player player, String targetName) {

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(player, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return;
        }

        plugin.addTroll();
        plugin.getStats().addStats("Border", player);
        player.sendMessage(String.format("%s§eTeleporting §7%s §eto the world border!", StringManager.PREFIX, target.getName()));
        teleportToBorder(target);
    }

    public void teleportToBorder(Player p) {
        World world = p.getWorld();
        WorldBorder border = world.getWorldBorder();

        Location center = border.getCenter();
        double size = border.getSize() / 2.0;

        double max = size - MARGIN;
        if (max <= 0) {
            TrollBoss.getInstance().getLogger().warning(StringManager.PREFIX + "§cWorld border is too small.");
            return;
        }

        int side = ThreadLocalRandom.current().nextInt(4);

        double x, z;

        switch (side) {
            case 0 -> {
                x = center.getX() + randomOffset(max);
                z = center.getZ() - max;
            }
            case 1 -> {
                x = center.getX() + randomOffset(max);
                z = center.getZ() + max;
            }
            case 2 -> {
                x = center.getX() - max;
                z = center.getZ() + randomOffset(max);
            }
            default -> {
                x = center.getX() + max;
                z = center.getZ() + randomOffset(max);
            }
        }

        int blockX = (int) x;
        int blockZ = (int) z;

        int chunkX = blockX >> 4;
        int chunkZ = blockZ >> 4;

        world.getChunkAtAsync(chunkX, chunkZ).thenAccept(chunk -> {
            if (!p.isOnline() || p.getWorld() != world) return;

            int y = world.getHighestBlockYAt(blockX, blockZ);

            Location target = new Location(
                    world,
                    blockX + 0.5, y + 1, blockZ + 0.5,
                    p.getYaw(), p.getPitch()
            );

            p.teleport(target);
        });
    }

    private double randomOffset(double max) {
        return ThreadLocalRandom.current().nextDouble(-max, max);
    }
}
