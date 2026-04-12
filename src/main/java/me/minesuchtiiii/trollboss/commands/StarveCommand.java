package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StarveCommand implements CommandExecutor {
    private static final int MAX_COUNT = 20;
    private final TrollBoss plugin;
    private int foodScheduler;

    public StarveCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.starve")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/starve [player] §c[count]");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        if (!validateTargetPlayer(player, targetPlayer, args[1])) {
            return true;
        }

        int count = Integer.parseInt(args[1]);
        startStarveScheduler(player, targetPlayer, count);
        return true;
    }

    private boolean validateTargetPlayer(Player player, Player targetPlayer, String countArg) {
        if (!plugin.canBeTrolled(targetPlayer)) {
            player.sendMessage(StringManager.BYPASS);
            return false;
        }

        if (TrollManager.isActive(targetPlayer.getUniqueId(), TrollType.STARVE)) {
            player.sendMessage(String.format("%s§cCan't do this right now!", StringManager.PREFIX));
            return false;
        }

        if (targetPlayer.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(String.format("%s§cCan't do this, this player is in creative gamemode.", StringManager.PREFIX));
            return false;
        }

        if (!plugin.isInt(countArg)) {
            player.sendMessage(String.format("%s§cError! §e%s §cis not a number!", StringManager.PREFIX, countArg));
            return false;
        }

        int count = Integer.parseInt(countArg);
        if (count <= 0) {
            player.sendMessage(String.format("%s§cCan't use that number! Min must be 1.", StringManager.PREFIX));
            return false;
        }

        if (count > MAX_COUNT) {
            player.sendMessage(String.format("%s§cCan't use that number! Max allowed is %d!", StringManager.PREFIX, MAX_COUNT));
            return false;
        }

        return true;
    }

    private void startStarveScheduler(Player player, Player targetPlayer, int count) {
        plugin.addTroll();
        plugin.getStats().addStats("Starve", player);
        TrollManager.activate(targetPlayer.getUniqueId(), TrollType.STARVE);

        player.sendMessage(String.format("%s§7%s §ewill starve!", StringManager.PREFIX, targetPlayer.getName()));

        foodScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int currentFoodLevel = targetPlayer.getFoodLevel();

            if (plugin.lvl < count) {
                if (currentFoodLevel > 0) {
                    targetPlayer.setFoodLevel(currentFoodLevel - 1);
                    plugin.lvl++;
                } else {
                    cancelStarvation(targetPlayer);
                }
            } else {
                cancelStarvation(targetPlayer);
            }
        }, 0L, 25L);
    }

    private void cancelStarvation(Player targetPlayer) {
        Bukkit.getScheduler().cancelTask(foodScheduler);
        TrollManager.deactivate(targetPlayer.getUniqueId(), TrollType.STARVE);
        plugin.lvl = 0;
    }
}
