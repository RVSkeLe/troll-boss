package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HurtCommand implements CommandExecutor {
    private static final String NO_PLAYER_MESSAGE = StringManager.NOPLAYER;
    private static final String PREFIX = StringManager.PREFIX;
    private static final String BYPASS_MESSAGE = StringManager.BYPASS;
    private static final long TASK_INTERVAL = 25L;
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 20;
    private final TrollBoss plugin;
    private int hurtScheduler;

    public HurtCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(NO_PLAYER_MESSAGE);
            return true;
        }

        if (!player.hasPermission("troll.hurt")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(PREFIX + "§eUse §7/hurt [player] §c[count]");
            return true;
        }


        validateTroll(player, args);
        return true;
    }

    private void validateTroll(Player player, String[] args) {

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Util.notOnline(player, args[0]);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(BYPASS_MESSAGE);
            return;
        }

        if (target.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(PREFIX + "§cCan't do this, this player is in creative gamemode.");
            return;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.HURT)) {
            player.sendMessage(PREFIX + "§cCan't do this right now!");
            return;
        }

        if (!plugin.isInt(args[1])) {
            player.sendMessage(PREFIX + "§cError! §e" + args[1] + " §cis not a number!");
            return;
        }

        int count = Integer.parseInt(args[1]);
        if (!validateCount(player, count)) {
            return;
        }

        startHurtTask(player, target, count);

    }

    private boolean validateCount(Player player, int count) {
        if (count < MIN_COUNT) {
            player.sendMessage(PREFIX + "§cCan't use that number! Min must be " + MIN_COUNT + ".");
            return false;
        }

        if (count > MAX_COUNT) {
            player.sendMessage(PREFIX + "§cCan't use that number! Max allowed is " + MAX_COUNT + "!");
            return false;
        }

        return true;
    }

    private void startHurtTask(Player player, Player target, int count) {
        plugin.addTroll();
        plugin.getStats().addStats("Hurt", player);
        TrollManager.activate(target.getUniqueId(), TrollType.HURT);
        player.sendMessage(PREFIX + "§7" + target.getName() + " §ewill be hurt!");

        hurtScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private int hurtCount = 0;

            @Override
            public void run() {
                if (hurtCount >= count || target.getHealth() <= 1) {
                    stopHurtTask(target);
                    return;
                }

                target.setHealth(target.getHealth() - 1);
                target.playHurtAnimation(180);
                target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);
                hurtCount++;
            }
        }, 0L, TASK_INTERVAL);
    }

    private void stopHurtTask(Player target) {
        Bukkit.getScheduler().cancelTask(hurtScheduler);
        TrollManager.deactivate(target.getUniqueId(), TrollType.HURT);
    }
}
