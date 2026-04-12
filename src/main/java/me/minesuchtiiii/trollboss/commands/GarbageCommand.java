package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GarbageCommand implements CommandExecutor {
    private static final String USAGE_MESSAGE = StringManager.PREFIX + "§eUse §7/garbage [player] [on | off]";
    private final TrollBoss plugin;

    public GarbageCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.garbage")) {
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

        if (!canToggleForPlayer(player, target)) return true;

        toggleGarbageMode(player, target, args[1]);
        return true;
    }

    private boolean canToggleForPlayer(Player executor, Player target) {
        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
            return false;
        }
        return true;
    }

    private void toggleGarbageMode(Player executor, Player target, String action) {
        if ("on".equalsIgnoreCase(action)) {
            if (!TrollManager.activate(target.getUniqueId(), TrollType.GARBAGE)) {
                sendToggleMessage(executor, target, true);
            } else {
                executor.sendMessage(StringManager.PREFIX + "§cGarbage mode already activated for this player!");
                executor.sendMessage(USAGE_MESSAGE.replace("[player] [on | off]", target.getName() + " off"));
            }
        } else if ("off".equalsIgnoreCase(action)) {
            if (TrollManager.deactivate(target.getUniqueId(), TrollType.GARBAGE)) {
                sendToggleMessage(executor, target, false);
            } else {
                executor.sendMessage(StringManager.PREFIX + "§cGarbage mode is not activated for this player!");
                executor.sendMessage(USAGE_MESSAGE.replace("[player] [on | off]", target.getName() + " on"));
            }
        } else {
            executor.sendMessage(USAGE_MESSAGE);
        }
    }

    private void sendToggleMessage(Player executor, Player target, boolean activated) {
        String status = activated ? "Activated" : "Deactivated";
        plugin.addTroll();
        plugin.getStats().addStats("Garbage", executor);
        executor.sendMessage(StringManager.PREFIX + "§e" + status + " garbage mode for §7" + target.getName() + "§e!");
    }
}
