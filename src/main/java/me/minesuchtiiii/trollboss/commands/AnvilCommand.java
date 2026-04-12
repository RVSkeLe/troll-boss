package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.items.keys.AnvilKey;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class AnvilCommand implements CommandExecutor {
    private static final String CANNOT_EXECUTE_NOW = StringManager.PREFIX + "§cCan't do this right now!";
    private static final String USE_ANVIL_COMMAND = StringManager.PREFIX + "§eUse §7/anvil [player]";
    private final TrollBoss plugin;

    public AnvilCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.anvil")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(USE_ANVIL_COMMAND);
            return true;
        }

        checkConditions(player, args[0]);
        return true;
    }

    private void checkConditions(Player player, String targetName) {

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            Util.notOnline(player, targetName);
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return;
        }
        if (target.isSprinting()) {
            player.sendMessage(CANNOT_EXECUTE_NOW);
            return;
        }
        if (TrollManager.isActive(target.getUniqueId(), TrollType.ANVIL)) {
            player.sendMessage(CANNOT_EXECUTE_NOW);
            return;
        }

        dropAnvilOnTarget(player, target);

    }

    private void dropAnvilOnTarget(Player player, Player target) {
        TrollManager.activate(target.getUniqueId(), TrollType.ANVIL);

        player.sendMessage(StringManager.PREFIX + "§eDropping an anvil on §7" + target.getName() + "§e!");

        Location targetLocation = target.getLocation();
        Location spawnLocation = targetLocation.clone().add(0, 20, 0);
        FallingBlock fallingBlock = target.getWorld().spawnFallingBlock(spawnLocation, Material.DAMAGED_ANVIL.createBlockData());
        AnvilKey.setAnvilTroll(fallingBlock);
        fallingBlock.setDamagePerBlock(20f);
        fallingBlock.setMaxDamage(100);

        this.plugin.addTroll();
        plugin.getStats().addStats("Anvil", player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(TrollBoss.getInstance(), () -> {
            TrollManager.deactivate(target.getUniqueId(), TrollType.ANVIL);
        }, 80L);
    }

}
