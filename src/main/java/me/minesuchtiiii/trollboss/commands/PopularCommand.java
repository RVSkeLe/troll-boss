package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PopularCommand implements CommandExecutor {

    private static final String PERMISSION_POPULAR = "troll.popular";
    private static final String TROLL_ACTION = "Popular";

    private final TrollBoss plugin;

    public PopularCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERMISSION_POPULAR)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            sendCommandUsage(player);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        isValidTarget(player, args[0]);

        return true;
    }

    private void sendCommandUsage(Player player) {
        player.sendMessage(StringManager.PREFIX + "§eUse §7/popular [player]");
    }

    private void isValidTarget(Player player, String targetName) {
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

        if (Bukkit.getOnlinePlayers().size() <= 1) {
            player.sendMessage(StringManager.PREFIX + "§cNot enough players online!");
            return;
        }

        performTrollAction(player, target);
    }

    private void performTrollAction(Player player, Player target) {
        plugin.getAllTo(target, player);
        plugin.addTroll();
        plugin.getStats().addStats(TROLL_ACTION, player);

        player.sendMessage(String.format("%s§7%s §eis popular now!", StringManager.PREFIX, target.getName()));
    }
}
