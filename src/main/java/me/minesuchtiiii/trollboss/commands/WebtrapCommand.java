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
import java.util.HashMap;

public class WebtrapCommand implements CommandExecutor {

    private static final int MAX_TIME = 3600;
    private static final String USAGE_MESSAGE = StringManager.PREFIX + "§eUse §7/webtrap [player] [time]";
    private static final String INVALID_NUMBER = StringManager.PREFIX + "§cNumber has to be bigger than 0!";
    private static final String MAX_EXCEEDED = StringManager.PREFIX + "§cCan't use that number, max allowed is " + MAX_TIME + "!";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");

    public HashMap<Integer, Location> altblockloc = new HashMap<>();
    public HashMap<Integer, Location> block = new HashMap<>();
    public HashMap<Integer, Material> zahlmat = new HashMap<>();

    private final TrollBoss plugin;

    public WebtrapCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.webtrap")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(USAGE_MESSAGE);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        if (!(Util.isInt(args[1]))) {
            player.sendMessage(StringManager.PREFIX + "§c" + args[1] + " §4is not a valid input time!");
            return true;
        }

        int time = Integer.parseInt(args[1]);

        if (!(plugin.canBeTrolled(target))) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.WEBTRAP)) {
            player.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return true;
        }

        if (!isValidTime(time)) {
            player.sendMessage(time <= 0 ? INVALID_NUMBER : MAX_EXCEEDED);
            return true;
        }

        double timeInMinutes = time / 60.0D;
        String formattedTime = DECIMAL_FORMAT.format(timeInMinutes);

        trapPlayer(target, time, player);
        player.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §ewill be trapped in web for §7" + time + " §eseconds! §c(~" + formattedTime + " minutes)");
        return true;
    }

    private boolean isValidTime(int time) {
        return time > 0 && time <= MAX_TIME;
    }

    private void trapPlayer(Player target, int time, Player executor) {
        plugin.addTroll();
        plugin.getStats().addStats("Webtrap", executor);
        TrollManager.activate(target.getUniqueId(), TrollType.WEBTRAP);

        // Web-Positionen zuweisen
        Location[] webLocations = calculateWebLocations(target.getLocation());
        saveAndSetBlocks(webLocations);

        // Zeitgesteuerte Entfernung der Blöcke
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> removeWebtrap(target), time * 20L);
    }

    private Location[] calculateWebLocations(Location tloc) {
        return new Location[]{
                tloc.clone().add(0, 0, 1), // front
                tloc.clone().add(0, 0, -1), // behind
                tloc.clone().add(1, 0, 0), // left
                tloc.clone().add(-1, 0, 0), // right
                tloc.clone().add(1, 0, 1), // frontleft
                tloc.clone().add(-1, 0, 1), // frontright
                tloc.clone().add(1, 0, -1), // behindleft
                tloc.clone().add(-1, 0, -1), // behindright
                tloc // center
        };
    }

    private void saveAndSetBlocks(Location[] locations) {
        for (int i = 0; i < locations.length; i++) {
            Location loc = locations[i];
            this.altblockloc.put(i, loc);
            this.zahlmat.put(i, loc.getBlock().getType());
            loc.getBlock().setType(Material.COBWEB);
            this.block.put(i + 1, loc);
        }
    }

    private void removeWebtrap(Player target) {
        TrollManager.deactivate(target.getUniqueId(), TrollType.WEBTRAP);
        for (int i = 1; i <= this.block.size(); i++) {
            Location block = this.block.get(i);
            block.getBlock().setType(Material.AIR);
        }
        for (int i = 0; i < this.altblockloc.size(); i++) {
            Location oldBlock = this.altblockloc.get(i);
            oldBlock.getBlock().setType(this.zahlmat.get(i));
        }
        this.altblockloc.clear();
        this.block.clear();
        this.zahlmat.clear();
    }
}
