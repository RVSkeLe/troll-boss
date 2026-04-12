package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BorderCommand implements CommandExecutor {

    private static final String PERM_TROLL_BORDER = "troll.border";
    private static final String PLAYER_HELP_MESSAGE = StringManager.PREFIX + "§eUse §7/border [player]";

    private final TrollBoss plugin;

    public BorderCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERM_TROLL_BORDER)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PLAYER_HELP_MESSAGE);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        handleTargetTeleport(player, args[0]);
        return true;
    }

    private void handleTargetTeleport(Player player, String targetName) {

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
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

        plugin.addTroll();
        plugin.getStats().addStats("Border", player);
        player.sendMessage(String.format("%s§eTeleporting §7%s §eto the world border!", StringManager.PREFIX, target.getName()));
        plugin.teleportToBorder(target);
    }
}
