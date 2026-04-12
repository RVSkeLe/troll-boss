package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VoidCommand implements CommandExecutor {
    private static final long BLOCK_BREAK_DELAY = 4L; // Delay for breaking blocks
    private static final long BLOCK_REBUILD_DELAY = 2L; // Delay for rebuilding blocks
    private final TrollBoss plugin;
    private int voidScheduler;
    private int voidRebuildScheduler;
    private int blockBreakCounter = 0;
    private int blockRebuildCounter = 0;

    public VoidCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.void")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/void [player]");
            return true;
        }

        voidPlayer(player, args[0]);
        return true;
    }

    private void voidPlayer(Player player, String targetName) {

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        if (target.isSprinting()) {
            player.sendMessage(StringManager.PREFIX + "§eCan't execute this command for §7" + target.getName() + " §ebecause they are sprinting!");
            return;
        }

        if (target.isDead()) {
            player.sendMessage(StringManager.PREFIX + "§eCan't execute this command for §7" + target.getName() + " §ebecause they are dead!");
            return;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.VOID)) {
            player.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return;
        }

        startVoidSequence(player, target);

    }

    private void startVoidSequence(Player player, Player target) {
        plugin.addTroll();
        plugin.getStats().addStats("Void", player);
        player.sendMessage(StringManager.PREFIX + "§eKilling §7" + target.getName() + " §ein void!");

        TrollManager.activate(target.getUniqueId(), TrollType.VOID);
        Location targetLocation = target.getLocation();
        int totalBlocks = calculateBlocksToVoid(targetLocation);

        saveOriginalBlocks(targetLocation, totalBlocks);
        scheduleBlockBreaking(targetLocation, totalBlocks);
        scheduleBlockRebuilding(target, totalBlocks);
    }

    private int calculateBlocksToVoid(Location location) {
        int version = Integer.parseInt(plugin.getVersion().split("\\.")[1]);
        return version >= 18
                ? (int) location.getY() + 65
                : (int) location.getY() + 1;
    }

    private void saveOriginalBlocks(Location location, int totalBlocks) {
        for (int index = 0; index < totalBlocks; index++) {
            Location blockLocation = location.getBlock().getRelative(0, -index, 0).getLocation();
            this.plugin.blockloc.put(index, blockLocation);
            this.plugin.blockmat.put(index, blockLocation.getWorld().getBlockAt(blockLocation).getType());
        }
    }

    private void scheduleBlockBreaking(Location location, int totalBlocks) {
        this.voidScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (blockBreakCounter < totalBlocks) {
                Location currentBlock = location.getBlock().getRelative(0, -blockBreakCounter, 0).getLocation();
                currentBlock.getBlock().setType(Material.AIR);
                blockBreakCounter++;
            } else {
                Bukkit.getScheduler().cancelTask(voidScheduler);
                blockBreakCounter = 0;
            }
        }, 0L, BLOCK_BREAK_DELAY);
    }

    private void scheduleBlockRebuilding(Player target, int totalBlocks) {
        this.voidRebuildScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (blockRebuildCounter < totalBlocks) {
                Location blockLocation = this.plugin.blockloc.get(blockRebuildCounter);
                blockLocation.getWorld().getBlockAt(blockLocation).setType(this.plugin.blockmat.get(blockRebuildCounter));
                blockRebuildCounter++;
            } else {
                cleanupAfterRebuild(target);
            }
        }, totalBlocks * BLOCK_BREAK_DELAY, BLOCK_REBUILD_DELAY);
    }

    private void cleanupAfterRebuild(Player target) {
        this.plugin.blockloc.clear();
        this.plugin.blockmat.clear();
        TrollManager.deactivate(target.getUniqueId(), TrollType.VOID);
        Bukkit.getScheduler().cancelTask(voidRebuildScheduler);
        blockRebuildCounter = 0;
    }
}
