package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class TrampleCommand implements CommandExecutor {

    private static final int MAX_COWS = 16;
    private static final String NUMBER_ERROR = StringManager.PREFIX + "§cNumber has to be bigger than 0!";
    private static final String NOT_A_NUMBER_ERROR = StringManager.PREFIX + "§cError! §e%s §cis not a number!";
    private static final String MAX_COWS_ERROR = StringManager.PREFIX + "§cCan't use that number, max allowed is %d!";
    private static final String USAGE_MESSAGE = StringManager.PREFIX + "§eUse §7/trample [player] [amount of cows]";
    private final TrollBoss plugin;

    public TrampleCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.trample")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(USAGE_MESSAGE);
            return true;
        }

        String targetName = args[0];
        String amountArg = args[1];

        if (!plugin.isInt(amountArg)) {
            player.sendMessage(String.format(NOT_A_NUMBER_ERROR, amountArg));
            return true;
        }
        
        validateTargetPlayer(player, targetName, Integer.parseInt(amountArg));
        return true;
    }


    private void validateTargetPlayer(Player executor, String targetName, int amount) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(executor, executor.getName());
            return;
        }
        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
            return;
        }
        if (target.isDead()) {
            executor.sendMessage(StringManager.FAILDEAD);
            return;
        }
        if (amount <= 0) {
            executor.sendMessage(NUMBER_ERROR);
            return;
        }
        if (amount > MAX_COWS) {
            executor.sendMessage(String.format(MAX_COWS_ERROR, MAX_COWS));
            return;
        }

        executeTrample(executor, target, amount);

    }


    private void executeTrample(Player player, Player target, int amount) {
        plugin.addTroll();
        plugin.getStats().addStats("Trample", player);
        TrollManager.activate(target.getUniqueId(), TrollType.TRAMPLE);
        player.sendMessage(StringManager.PREFIX + String.format("§7%d §ecows will trample on §7%s§e!", amount, target.getName()));
        for (int i = 0; i < amount; i++) {
            spawnCow(target);
        }
    }

    public void spawnCow(Player p) {
        final Location ploc = p.getLocation();

        final Silverfish fish = (Silverfish) p.getWorld().spawnEntity(p.getLocation(), EntityType.SILVERFISH);
        fish.addPotionEffects(List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 3),
                new PotionEffect(PotionEffectType.SPEED, 10000000, 3),
                new PotionEffect(Objects.requireNonNull(Registry.EFFECT.get(NamespacedKey.minecraft("strength"))), 10000000, 3)));
        fish.setAggressive(true);
        fish.setTarget(p);

        final Cow cow = (Cow) p.getWorld().spawnEntity(ploc, EntityType.COW);

        final Silverfish fish2 = (Silverfish) p.getWorld().spawnEntity(p.getLocation(), EntityType.SILVERFISH);
        fish2.addPotionEffects(List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 3),
                new PotionEffect(PotionEffectType.SPEED, 10000000, 3),
                new PotionEffect(Objects.requireNonNull(Registry.EFFECT.get(NamespacedKey.minecraft("strength"))), 10000000, 3)));
        fish2.setAggressive(true);
        fish2.setTarget(p);

        fish2.setCustomName(Util.getRandomColor() + "Mad Cow");
        fish2.setCustomNameVisible(false);

        cow.addPassenger(fish2);
        fish.addPassenger(cow);

        plugin.getTrampleManager().addCow(cow);
        plugin.getTrampleManager().addCow(fish);
        plugin.getTrampleManager().addCow(fish2);
    }
}
