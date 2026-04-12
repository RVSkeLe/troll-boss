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

public class DrugCommand implements CommandExecutor {
    private static final int POTION_DURATION = 60 * 20; // 60 seconds
    private static final int POTION_AMPLIFIER = 1;

    private final TrollBoss plugin;

    public DrugCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.drug")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/drug [player]");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        if (targetPlayer.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return true;
        }

        if (!plugin.canBeTrolled(targetPlayer)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        applyPotionEffects(targetPlayer);
        player.sendMessage(StringManager.PREFIX + "§eDrugged §7" + targetPlayer.getName() + "§e!");
        plugin.addTroll();
        plugin.getStats().addStats("Drug", player);

        return true;
    }

    private void applyPotionEffects(Player targetPlayer) {
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, POTION_DURATION, POTION_AMPLIFIER));
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, POTION_DURATION, POTION_AMPLIFIER));
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, POTION_DURATION, POTION_AMPLIFIER));
    }
}
