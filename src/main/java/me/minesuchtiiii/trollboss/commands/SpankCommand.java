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

public class SpankCommand implements CommandExecutor {

    private static final String SPANK_MESSAGE = "§c§lYou have been spanked you naughty miner!";
    private static final double SPANK_DAMAGE = 4.0;
    private final TrollBoss plugin;

    public SpankCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.spank")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/spank [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        if ("all".equalsIgnoreCase(args[0])) {
            spankAllPlayers(player);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        spankPlayer(player, target);
        return true;
    }

    private void spankPlayer(Player sender, Player target) {
        if (!plugin.canBeTrolled(target)) {
            sender.sendMessage(StringManager.BYPASS);
            return;
        }

        plugin.addTroll();
        plugin.getStats().addStats("Spank", sender);

        target.sendMessage(SPANK_MESSAGE);
        target.setHealth(Math.max(0, target.getHealth() - SPANK_DAMAGE));
        sender.sendMessage(StringManager.PREFIX + "§eYou spanked §7" + target.getName() + "§e!");

    }

    private void spankAllPlayers(Player sender) {

        plugin.addTroll();
        plugin.getStats().addStats("Spank", sender);
        sender.sendMessage(StringManager.PREFIX + "§eYou spanked everyone, except the players who can bypass it!");

        Bukkit.getOnlinePlayers().stream().filter(player -> !player.equals(sender) && plugin.canBeTrolled(player)).forEach(player -> {
            player.sendMessage(SPANK_MESSAGE);
            player.setHealth(Math.max(0, player.getHealth() - SPANK_DAMAGE));
        });
    }
}
