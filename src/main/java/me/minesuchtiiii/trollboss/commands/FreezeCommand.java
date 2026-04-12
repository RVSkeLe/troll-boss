package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class FreezeCommand implements CommandExecutor {
    private static final int FREEZE_DURATION = 2400;
    private static final int BLINDNESS_DURATION = 300;
    private static final int HEALTH = 14;
    private static final int EFFECT_LEVEL = 2;

    private final TrollBoss plugin;

    public FreezeCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.freeze")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            applyFreezeEffects(player);
            player.sendMessage(StringManager.PREFIX + "§7You've frozen yourself!");
            plugin.addTroll();
            plugin.getStats().addStats("Freeze", player);
        } else if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                Util.notOnline(player, args[0]);
                return true;
            }

            if (!plugin.canBeTrolled(targetPlayer)) {
                player.sendMessage(StringManager.BYPASS);
                return true;
            }

            applyFreezeEffects(targetPlayer);
            player.sendMessage(StringManager.PREFIX + "§eYou've frozen §7" + targetPlayer.getName() + "§e!");
            plugin.addTroll();
            plugin.getStats().addStats("Freeze", player);
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void applyFreezeEffects(Player target) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, FREEZE_DURATION, EFFECT_LEVEL));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, BLINDNESS_DURATION, EFFECT_LEVEL));
        target.setHealth(HEALTH);
    }
}
