package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PushCommand implements CommandExecutor {
    private static final double PUSH_MULTIPLIER = -3.1D;
    private static final double PUSH_Y_VELOCITY = 1.12D;

    private final TrollBoss plugin;

    public PushCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.push")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        switch (args.length) {
            case 0:
                applyPushEffect(player);
                player.sendMessage(StringManager.PREFIX + "§7You pushed yourself!");
                plugin.addTroll();
                plugin.getStats().addStats("Push", player);
                break;

            case 1:
                handlePushTarget(player, args[0]);
                break;

            default:
                player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void applyPushEffect(Player target) {
        target.setVelocity(target.getVelocity().setY(0));
        target.setVelocity(target.getLocation().getDirection().multiply(PUSH_MULTIPLIER));
        target.setVelocity(target.getVelocity().setY(PUSH_Y_VELOCITY));
    }

    private void handlePushTarget(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            plugin.notOnline(player, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        applyPushEffect(target);
        player.sendMessage(StringManager.PREFIX + "§eYou pushed §7" + target.getName() + "§e!");
        plugin.addTroll();
        plugin.getStats().addStats("Push", player);
    }
}
