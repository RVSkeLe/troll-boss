package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.SpamManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpamCommand implements CommandExecutor {
    private static final String PERMISSION_SPAM = "troll.spam";
    private static final String ERROR_NOT_NUMBER = StringManager.PREFIX + "§cError! §e%s §cis not a number!";
    private static final String ERROR_INVALID_NUMBER = StringManager.PREFIX + "§cCan't use that number! %s";
    private static final String ERROR_ALREADY_SPAMMED = StringManager.PREFIX + "§cThis player is already being spammed!";

    private final TrollBoss plugin;
    private final int maxSpam;

    public SpamCommand(TrollBoss plugin) {
        this.plugin = plugin;
        this.maxSpam = 100;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERMISSION_SPAM)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/spam [player] [amount]");
            return true;
        }

        if (args.length > 2) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        handleSpamCommand(player, args[0], args[1]);
        return true;
    }

    private void handleSpamCommand(Player player, String targetName, String amountStr) {

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(player, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        if (!Util.isInt(amountStr)) {
            player.sendMessage(String.format(ERROR_NOT_NUMBER, amountStr));
            return;
        }

        int amount = Integer.parseInt(amountStr);
        if (amount < 1) {
            player.sendMessage(String.format(ERROR_INVALID_NUMBER, "Min must be 1."));
            return;
        }

        if (amount > maxSpam) {
            player.sendMessage(String.format(ERROR_INVALID_NUMBER, "Max allowed is " + maxSpam + "!"));
            return;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.SPAM)) {
            player.sendMessage(ERROR_ALREADY_SPAMMED);
            return;
        }

        spamPlayer(player, target, amount);
    }

    private void spamPlayer(Player player, Player target, int amount) {
        plugin.addTroll();
        plugin.getStats().addStats("Spam", player);
        player.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §ewill be spammed for §7" + amount + " §etimes!");
        SpamManager.spamPlayer(target, amount);
    }
}
