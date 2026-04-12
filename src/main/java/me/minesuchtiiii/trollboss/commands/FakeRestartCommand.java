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

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class FakeRestartCommand implements CommandExecutor {
    private static final int MAX_TIME = 120;
    private static boolean isRestarting = false;
    private final TrollBoss plugin;
    private int counter;
    private int remainingTime;

    public FakeRestartCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.fakerestart")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/fakerestart [time]");
            return true;
        }

        if (args.length != 1 || !Util.isInt(args[0])) {
            player.sendMessage(StringManager.PREFIX + "§cError! §e" + args[0] + " §cis not a valid number!");
            return true;
        }

        if (isRestarting) {
            player.sendMessage(StringManager.PREFIX + "§cYou can't do this right now!");
            return true;
        }

        int inputTime = Integer.parseInt(args[0]);
        if (inputTime <= 0) {
            player.sendMessage(StringManager.PREFIX + "§cNumber has to be bigger than 0!");
            return true;
        }

        if (inputTime > MAX_TIME) {
            player.sendMessage(StringManager.PREFIX + "§cMaximum allowed time is " + MAX_TIME + " seconds!");
            return true;
        }

        initiateFakeRestart(player, inputTime);
        return true;
    }

    private void initiateFakeRestart(Player player, int time) {
        this.remainingTime = time + 1;  // Adjust for time alignment
        this.plugin.addTroll();
        plugin.getStats().addStats("Fakerestart", player);

        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        double timeInMinutes = remainingTime / 60.0D;

        player.sendMessage(StringManager.PREFIX + "§eServer will fakerestart in §7" + (remainingTime - 1) + " §eseconds! §c(~" + decimalFormat.format(timeInMinutes) + " minutes)");
        isRestarting = true;

        this.counter = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> performCountdown(player), 20L, 20L);
    }

    private void performCountdown(Player executor) {
        remainingTime--;

        if (remainingTime > 0) {
            announceRestart(remainingTime);
        } else {
            Bukkit.broadcastMessage("§7§l[§c§lServer§7§l] §r§6Server is restarting...");
            Bukkit.getScheduler().cancelTask(counter);
            kickSchedule(executor);
            isRestarting = false;
        }
    }

    private void announceRestart(int time) {
        if (time % 30 == 0 || time <= 10) {
            restartMessage(time);
        }
    }

    public void restartMessage(int i) {
        Bukkit.broadcastMessage("§7§l[§c§lServer§7§l] §r§6Server will be restarting in §4" + i + " §6seconds!");
    }

    public void kickSchedule(final Player p) {

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            List<Player> playersToKick = Bukkit.getOnlinePlayers().stream()
                    .filter(all -> !all.getUniqueId().equals(p.getUniqueId()))
                    .collect(Collectors.toList());

            for (Player all : playersToKick) {
                TrollManager.activate(all.getUniqueId(), TrollType.FAKERESTART);
                all.kickPlayer("§cServer restarting...");
            }

            TrollManager.clear(TrollType.RANDOMTP);

        }, 30L);

    }
}
