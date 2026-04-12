package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TurnCommand implements CommandExecutor {
    private final TrollBoss plugin;

    public TurnCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.turn")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/turn [player]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.notOnline(player, args[0]);
            return true;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        rotatePlayer(target, player);
        return true;
    }

    private void rotatePlayer(Player target, Player sender) {
        Location targetLocation = target.getLocation();
        Location newLocation = new Location(
                targetLocation.getWorld(),
                targetLocation.getX(),
                targetLocation.getY(),
                targetLocation.getZ(),
                getRotatedYaw(targetLocation.getYaw()),
                targetLocation.getPitch()
        );
        target.teleport(newLocation);

        sender.sendMessage(StringManager.PREFIX + "§eTurned §7" + target.getName() + "§e!");
        plugin.addTroll();
        plugin.getStats().addStats("Turn", sender);
    }

    private float getRotatedYaw(float currentYaw) {
        if (currentYaw > 0) return currentYaw - 180;
        if (currentYaw < 0) return 180 + currentYaw;
        return (currentYaw == 180 || currentYaw == -180) ? 0 : 180;
    }
}
