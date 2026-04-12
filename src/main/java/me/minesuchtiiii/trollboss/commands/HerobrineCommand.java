package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.HerobrineHelper;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HerobrineCommand implements CommandExecutor {
    private static final String PERMISSION = "troll.herobrine";
    private static final String SUCCESS_HEROBRINE = "§eYou are Herobrine now!";
    private static final String REMOVE_HEROBRINE = "§cYou are no longer Herobrine!";
    private static final String PREFIX_SET = "§eSet §7";
    private static final String PREFIX_UNSET = "§ePlayer §7";

    private final TrollBoss plugin;

    public HerobrineCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            toggleHerobrine(player, player, SUCCESS_HEROBRINE, REMOVE_HEROBRINE);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                toggleHerobrine(player, target, PREFIX_SET + target.getName() + " §eHerobrine!", PREFIX_UNSET + target.getName() + " §eis no longer Herobrine!");
            } else {
                Util.notOnline(player, args[0]);
            }
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }
        return true;
    }

    private void toggleHerobrine(Player executor, Player target, String successMessage, String removeMessage) {
        if (!TrollManager.isActive(target.getUniqueId(), TrollType.HEROBRINE)) {
            HerobrineHelper.setHerobrine(target);
            executor.sendMessage(StringManager.PREFIX + successMessage);
            target.sendMessage(SUCCESS_HEROBRINE);
        } else {
            HerobrineHelper.unsetHerobrine(target);
            executor.sendMessage(StringManager.PREFIX + removeMessage);
            target.sendMessage(REMOVE_HEROBRINE);
        }
        plugin.addTroll();
        plugin.getStats().addStats("Herobrine", executor);
    }

}
