package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.DeathManager;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SpartaCommand implements CommandExecutor {
    private final TrollBoss plugin;

    public SpartaCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.sparta")) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/sparta [player]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Util.notOnline(player, args[0]);
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

        startSparta(player, target);
        return true;
    }

    private void startSparta(Player player, Player target) {
        plugin.addTroll();
        plugin.getStats().addStats("Sparta", player);
        player.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §ewill enjoy SPARTA!");
        TrollManager.activate(target.getUniqueId(), TrollType.SPARTA);
        plugin.spartaArrows.put(target.getUniqueId(), 0);

        int arrowAmount = Util.getRandom(5, 10);
        plugin.spartaTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> handleSpartaArrows(target, arrowAmount), 0L, 20L);
    }

    private void handleSpartaArrows(Player target, int arrowAmount) {
        if (DeathManager.hasDied(target.getUniqueId(), TrollType.SPARTA)) {
            resetSparta(target);
            return;
        }

        if (plugin.spartaArrows.get(target.getUniqueId()) < arrowAmount) {
            spawnArrow(target);
            plugin.spartaArrows.put(target.getUniqueId(), plugin.spartaArrows.get(target.getUniqueId()) + 1);
        } else {
            resetSparta(target);
        }
    }

    private void spawnArrow(Player target) {
        Location targetLocation = target.getLocation();
        Location spawnLocation = targetLocation.clone().add(
                Util.getRandom(-10, 10),
                Util.getRandom(5, 10),
                Util.getRandom(-10, 10)
        );
        Arrow arrow = target.getWorld().spawn(spawnLocation, Arrow.class);
        Vector direction = targetLocation.toVector().subtract(spawnLocation.toVector()).normalize().multiply(2.0D);
        arrow.setVelocity(direction);
    }

    private void resetSparta(Player player) {
        Bukkit.getScheduler().cancelTask(plugin.spartaTask);
        UUID playerUuid = player.getUniqueId();

        TrollManager.deactivate(playerUuid, TrollType.SPARTA);
        plugin.spartaArrows.remove(playerUuid);
        DeathManager.setDead(playerUuid, TrollType.SPARTA);
    }
}
