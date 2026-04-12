package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class TrapCommand implements CommandExecutor {
    private final TrollBoss plugin;
    int max = 3600;

    public TrapCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.trap")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/trap [player] [time]");
            return true;
        }

        if (!plugin.isInt(args[1])) {
            player.sendMessage(StringManager.PREFIX + "§cInvalid time format. Please enter a number.");
            return true;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        final int trapTime = Integer.parseInt(args[1]);

        if (trapTime <= 0 || trapTime > max) {
            player.sendMessage(StringManager.PREFIX + "§cTime must be between 1 and " + max + " seconds.");
            return true;
        }

        if (target == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.TRAP)) {
            player.sendMessage(StringManager.PREFIX + "§cThis player is already trapped.");
            return true;
        }

        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return true;
        }

        if (target.isSprinting()) {
            player.sendMessage(StringManager.PREFIX + "§cCan't do that right now as the target is sprinting!");
            return true;
        }

        TrollManager.activate(target.getUniqueId(), TrollType.TRAP);
        plugin.addTroll();
        plugin.getStats().addStats("Trap", player);

        final Location targetLocation = target.getLocation();
        Util.centerPlayer(target);
        createTrapAround(targetLocation);

        double timeInMinutes = trapTime / 60.0;
        player.sendMessage(StringManager.PREFIX + "§eTrapped §7" + target.getName() + " §efor §7" + trapTime + " seconds! §c(~" + new DecimalFormat("##.##").format(timeInMinutes) + " minutes)");

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> releaseTrap(target), trapTime * 20L);

        return true;
    }

    private void createTrapAround(Location location) {
        Location[] trapBlockPositions = {
                location.getBlock().getRelative(1, 0, 0).getLocation(),
                location.getBlock().getRelative(1, 1, 0).getLocation(),
                location.getBlock().getRelative(-1, 0, 0).getLocation(),
                location.getBlock().getRelative(-1, 1, 0).getLocation(),
                location.getBlock().getRelative(0, 0, 1).getLocation(),
                location.getBlock().getRelative(0, 1, 1).getLocation(),
                location.getBlock().getRelative(0, 0, -1).getLocation(),
                location.getBlock().getRelative(0, 1, -1).getLocation(),
                location.getBlock().getRelative(0, 2, 0).getLocation(),
                location.getBlock().getRelative(0, -1, 0).getLocation()
        };

        for (int i = 0; i < trapBlockPositions.length; i++) {
            Location blockLocation = trapBlockPositions[i];
            Material originalMaterial = blockLocation.getWorld().getBlockAt(blockLocation).getType();

            plugin.oldBlocksLocation.put(i, blockLocation);
            plugin.numbersmat.put(i, originalMaterial);

            blockLocation.getWorld().getBlockAt(blockLocation).setType(Material.GLASS);
            plugin.blocks.put(i + 1, blockLocation);
        }
    }

    private void releaseTrap(Player player) {
        for (int i = 1; i <= 10; i++) {
            Location blockLocation = plugin.blocks.get(i);
            blockLocation.getWorld().getBlockAt(blockLocation).setType(Material.AIR);
        }

        for (int i = 0; i < 10; i++) {
            Location oldBlockLocation = plugin.oldBlocksLocation.get(i);
            Material originalMaterial = plugin.numbersmat.get(i);
            oldBlockLocation.getWorld().getBlockAt(oldBlockLocation).setType(originalMaterial);
        }

        plugin.blocks.clear();
        plugin.oldBlocksLocation.clear();
        plugin.numbersmat.clear();
        TrollManager.deactivate(player.getUniqueId(), TrollType.TRAP);
    }
}
