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

public class SkyCommand implements CommandExecutor {

    private static final int SKY_HEIGHT = 176;
    private static final int MAX_TIME = 3600;
    private static final int TICKS_PER_SECOND = 20;
    private static final String WRONG_USAGE = StringManager.PREFIX + "§eUse §7/sky [player] [time]";
    private final TrollBoss plugin;

    public SkyCommand(TrollBoss plugin) {

        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }
        if (!player.hasPermission("troll.sky")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(WRONG_USAGE);
            return true;
        }

        String targetName = args[0];
        String timeArg = args[1];

        if (!plugin.isInt(timeArg)) {
            player.sendMessage(StringManager.PREFIX + "§cInvalid time provided!");
            return true;
        }

        int time = Integer.parseInt(timeArg);
        if (time <= 0 || time > MAX_TIME) {
            player.sendMessage(String.format("%s§cTime must be between 1 and %d seconds!", StringManager.PREFIX, MAX_TIME));
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return true;
        }
        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }
        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return true;
        }
        if (TrollManager.isActive(target.getUniqueId(), TrollType.SKY)) {
            player.sendMessage(StringManager.PREFIX + "§cCannot troll this player right now!");
            return true;
        }

        // Save player's current location
        Location targetLocation = target.getLocation();
        TrollManager.activate(target.getUniqueId(), TrollType.SKY);
        plugin.skymap.put(targetName, targetLocation);

        // Create glass platform
        Location[] glassLocations = generateGlassPlatformLocations(targetLocation);
        for (Location loc : glassLocations) {
            loc.getWorld().getBlockAt(loc).setType(Material.GLASS);
        }

        // Teleport player to the sky
        Location skyLocation = new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY() + SKY_HEIGHT, target.getLocation().getZ());
        target.teleport(skyLocation);

        // Notify players
        plugin.addTroll();
        plugin.getStats().addStats("Sky", player);
        double minutes = time / 60.0;
        player.sendMessage(String.format("%s§7%s §ewill enjoy the sky for §7%d §eseconds! §c(~%.2f minutes)", StringManager.PREFIX, targetName, time, minutes));

        // Schedule removal of the troll
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            TrollManager.deactivate(target.getUniqueId(), TrollType.SKY);
            target.teleport(plugin.skymap.remove(targetName));
            for (Location loc : glassLocations) {
                loc.getWorld().getBlockAt(loc).setType(Material.AIR);
            }
        }, time * TICKS_PER_SECOND);

        return true;
    }

    private Location[] generateGlassPlatformLocations(Location baseLocation) {
        double x = baseLocation.getX();
        double y = baseLocation.getY() + SKY_HEIGHT - 2; // Slight offset for platform height
        double z = baseLocation.getZ();

        return new Location[]{
                new Location(baseLocation.getWorld(), x, y, z),
                new Location(baseLocation.getWorld(), x, y, z + 1),
                new Location(baseLocation.getWorld(), x, y, z - 1),
                new Location(baseLocation.getWorld(), x + 1, y, z),
                new Location(baseLocation.getWorld(), x - 1, y, z),
                new Location(baseLocation.getWorld(), x + 1, y, z + 1),
                new Location(baseLocation.getWorld(), x - 1, y + 1, z + 1),
                new Location(baseLocation.getWorld(), x + 1, y, z - 1),
                new Location(baseLocation.getWorld(), x - 1, y, z - 1)
        };
    }

}
