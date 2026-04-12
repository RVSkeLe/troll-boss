package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FakedeopCommand implements CommandExecutor {

    private static final String TROLL_OPERATORS_KEY = "Troll-Operators";
    private static final String FAKEDEOP_PERMISSION = "troll.fakedeop";

    private final TrollBoss plugin;

    public FakedeopCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(FAKEDEOP_PERMISSION)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/fakedeop [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        if ("all".equalsIgnoreCase(args[0])) {
            fakeDeopAllPlayers(player);
        } else {
            fakeDeopSinglePlayer(player, args[0]);
        }

        return true;
    }

    private void fakeDeopSinglePlayer(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return;
        }

        if (target.isOp() && !plugin.getConfig().getBoolean(TROLL_OPERATORS_KEY)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        notifyFakeDeop(player, target);
    }

    private void fakeDeopAllPlayers(Player player) {
        Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer != player && plugin.canBeTrolled(onlinePlayer))
                .forEach(onlinePlayer -> onlinePlayer.sendMessage("§eYou are no longer op!"));

        plugin.addTroll();
        plugin.getStats().addStats("Fakedeop", player);
        player.sendMessage(StringManager.PREFIX + "§You fake deoped everyone, except players who can bypass it!");
    }

    private void notifyFakeDeop(Player executor, Player target) {
        String message = "§eYou are no longer op!";
        target.sendMessage(message);
        executor.sendMessage(StringManager.PREFIX + "§eYou have fake deoped §7" + target.getName() + "§e!");
        plugin.addTroll();
        plugin.getStats().addStats("Fakedeop", executor);
    }
}
