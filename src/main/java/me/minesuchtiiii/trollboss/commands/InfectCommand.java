package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class InfectCommand implements CommandExecutor {
    private static final int MAX_TIME = 3600;
    private static final long SCHEDULER_DELAY = 40L;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");

    private final TrollBoss plugin;

    public InfectCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.infect")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/infect [player] [delay]");
            return true;
        }
        if (args.length > 2) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return true;
        }

        int duration;
        try {
            duration = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(StringManager.PREFIX + "§cPlease provide a valid number for time!");
            return true;
        }

        if (isValidTarget(player, target, duration)) {
            infectTarget(player, target, duration);
        }

        return true;
    }

    private boolean isValidTarget(Player sender, Player target, int duration) {
        if (!plugin.canBeTrolled(target)) {
            sender.sendMessage(StringManager.BYPASS);
            return false;
        }
        if (target.isDead()) {
            sender.sendMessage(StringManager.PREFIX + "§eCan't infect §7" + target.getName() + " §ebecause they are dead!");
            return false;
        }
        if (duration <= 0) {
            sender.sendMessage(StringManager.PREFIX + "§cNumber has to be bigger than 0!");
            return false;
        }
        if (duration > MAX_TIME) {
            sender.sendMessage(StringManager.PREFIX + "§cCan't use that number, max allowed is " + MAX_TIME + "!");
            return false;
        }

        return true;
    }

    private void infectTarget(Player sender, Player target, int duration) {
        plugin.addTroll();
        plugin.getStats().addStats("Infect", sender);

        double minutes = duration / 60.0;
        String timeFormatted = DECIMAL_FORMAT.format(minutes);

        sender.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §ewill be infected for §7" + duration
                + " §eseconds! §c(~" + timeFormatted + " minutes)");

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> applyInfectionEffects(target, duration), SCHEDULER_DELAY);
    }

    private void applyInfectionEffects(Player target, int duration) {
        int ticks = duration * 20;
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ticks, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, ticks, 1));
    }
}
