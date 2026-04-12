package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreeperCommand implements CommandExecutor {

    private static final int DEFAULT_CREEPER_AMOUNT = 1;

    private final TrollBoss plugin;

    public CreeperCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.creeper")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/creeper [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            this.plugin.notOnline(player, args[0]);
            return true;
        }

        executeCreeperCommand(player, target);
        return true;
    }

    private void executeCreeperCommand(Player executor, Player target) {
        boolean isCreepersDisabled = this.plugin.creep;

        // Check bypass conditions
        if (!plugin.canBeTrolled(target)) {
            executor.sendMessage(StringManager.BYPASS);
            return;
        }

        if (isCreepersDisabled) {
            executor.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return;
        }

        // Execute creeper spawn logic
        this.plugin.spawnCreepers(target, target.getLocation(), DEFAULT_CREEPER_AMOUNT);
        this.plugin.addTroll();
        plugin.getStats().addStats("Creeper", executor);
        executor.sendMessage(StringManager.PREFIX + "§eSpawned §7" + DEFAULT_CREEPER_AMOUNT + " §ecreeper at §7" + target.getName() + "§e's location!");
        this.plugin.creep = true;
        this.plugin.creepers = DEFAULT_CREEPER_AMOUNT;

        Bukkit.getScheduler().runTaskLater(plugin, () -> this.plugin.creep = false, 20 * 60);
    }
}
