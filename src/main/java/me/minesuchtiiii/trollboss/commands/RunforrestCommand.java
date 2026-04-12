package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RunforrestCommand implements CommandExecutor {
    private static final int MIN_TIME = 3;
    private static final int MAX_TIME = 60;
    private final TrollBoss plugin;

    public RunforrestCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.runforrest")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/runforrest [player] [time]");
            return true;
        }

        String timeArg = args[1];

        if (!plugin.isInt(timeArg)) {
            player.sendMessage(StringManager.PREFIX + "§cError! §e" + timeArg + " §cis not a number!");
            return true;
        }

        int time = Integer.parseInt(timeArg);

        handleTarget(player, args[0], time);
        return true;
    }

    private void handleTarget(Player sender, String targetName, int time) {

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            plugin.notOnline(sender, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            sender.sendMessage(StringManager.BYPASS);
            return;
        }

        if (target.isDead()) {
            sender.sendMessage(StringManager.FAILDEAD);
            return;
        }

        if (time <= MIN_TIME) {
            sender.sendMessage(StringManager.PREFIX + "§cNumber has to be bigger than 3!");
            return;
        }

        if (time > MAX_TIME) {
            sender.sendMessage(StringManager.PREFIX + "§cCan't use that number, max allowed is " + MAX_TIME + "!");
            return;
        }

        if (plugin.rf.containsKey(target.getName()) || plugin.warnTime.containsKey(sender.getName())) {
            sender.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return;
        }

        startTroll(sender, target, time);
    }

    private void startTroll(Player sender, Player target, int time) {
        sender.sendMessage(StringManager.PREFIX + "§eStarted troll successfully!");
        plugin.getStats().addStats("Runforrest", sender);
        plugin.addTroll();
        plugin.rftime.put(target.getName(), time);
        plugin.start5SecRunTimer(target);
        plugin.rfmsg.put(target.getName(), sender.getName());
    }
}
