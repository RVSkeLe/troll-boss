package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StfuCommand implements CommandExecutor {

    private static final String PERMISSION_TROLL_STFU = "troll.stfu";
    private final TrollBoss plugin;

    public StfuCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERMISSION_TROLL_STFU)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/stfu [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
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

        handlePlayer(player, target);
        return true;
    }

    private void handlePlayer(Player executor, Player target) {

        if (TrollManager.isActive(target.getUniqueId(), TrollType.STFU)) {
            unmutePlayer(executor, target);
        } else {
            mutePlayer(executor, target);
        }
        plugin.addTroll();
        plugin.getStats().addStats("Stfu", executor);
    }

    private void mutePlayer(Player executor, Player target) {
        executor.sendMessage(StringManager.PREFIX + "§eYou muted §7" + target.getName() + "§e!");
        TrollManager.activate(target.getUniqueId(), TrollType.STFU);
    }

    private void unmutePlayer(Player executor, Player target) {
        executor.sendMessage(StringManager.PREFIX + "§eYou unmuted §7" + target.getName() + "§e!");
        TrollManager.deactivate(target.getUniqueId(), TrollType.STFU);
    }
}
