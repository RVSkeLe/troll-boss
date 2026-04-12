package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BoomCommand implements CommandExecutor {
    private static final float EXPLOSION_POWER = 3.0F;
    private static final long EXPLOSION_DELAY = 40L;

    private final TrollBoss plugin;

    public BoomCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.boom")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            handleSelfExplosion(player);
        } else if (args.length == 1) {
            if ("all".equalsIgnoreCase(args[0])) {
                handleAllExplosion(player);
            } else {
                handleTargetedExplosion(player, args[0]);
            }
        } else {
            player.sendMessage(StringManager.MUCHARGS);
        }

        return true;
    }

    private void handleSelfExplosion(Player player) {
        if (TrollManager.isActive(player.getUniqueId(), TrollType.BOOM)) {
            player.sendMessage(StringManager.PREFIX + "§cYou can't do this right now!");
            return;
        }
        triggerExplosion(player, player);
    }

    private void handleTargetedExplosion(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return;
        }
        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }
        if (TrollManager.isActive(player.getUniqueId(), TrollType.BOOM)) {
            player.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return;
        }
        triggerExplosion(player, target);
    }

    private void handleAllExplosion(Player player) {
        player.sendMessage(StringManager.PREFIX + "§eEveryone is going to explode, except the players who can bypass it!");
        plugin.addTroll();
        plugin.getStats().addStats("Boom", player);

        Bukkit.getOnlinePlayers().stream()
                .filter(target -> target != player && canTargetBeExploded(target))
                .forEach(target -> triggerExplosion(player, target));
    }

    private boolean canTargetBeExploded(Player target) {
        if (TrollManager.isActive(target.getUniqueId(), TrollType.BOOM)) return false;
        return plugin.canBeTrolled(target);
    }

    private void triggerExplosion(Player initiator, Player target) {
        String message = (initiator == target)
                ? StringManager.PREFIX + "§7You're going to explode!"
                : StringManager.PREFIX + "§7" + target.getName() + " §eis going to explode!";
        initiator.sendMessage(message);

        TrollManager.activate(target.getUniqueId(), TrollType.BOOM);
        plugin.addTroll();
        plugin.getStats().addStats("Boom", initiator); // Will be refactored later, since it's in capital case and not UPPER

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            target.getLocation().getWorld().createExplosion(
                    target.getLocation().getX(),
                    target.getLocation().getY(),
                    target.getLocation().getZ(),
                    EXPLOSION_POWER,
                    false,
                    false
            );
            TrollManager.deactivate(target.getUniqueId(), TrollType.BOOM);
        }, EXPLOSION_DELAY);
    }
}
