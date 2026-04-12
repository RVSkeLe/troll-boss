package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FreefallCommand implements CommandExecutor {

    private static final int MAX_HEIGHT = 10000;
    private final TrollBoss plugin;

    public FreefallCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.freefall")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/freefall [player] [high]");
            return true;
        }

        if (!Util.isInt(args[1])) {
            player.sendMessage(StringManager.PREFIX + "§cError! §e" + args[1] + " §cis not a number!");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        int heightIncrease = Integer.parseInt(args[1]);
        if (!validateHeight(player, heightIncrease)) return true;

        if (!canFreefall(player, targetPlayer)) return true;

        executeFreefall(player, targetPlayer, heightIncrease);
        return true;
    }

    private boolean validateHeight(Player player, int heightIncrease) {
        if (heightIncrease <= 0) {
            player.sendMessage(StringManager.PREFIX + "§cNumber has to be bigger than 0!");
            return false;
        }
        if (heightIncrease > MAX_HEIGHT) {
            player.sendMessage(StringManager.PREFIX + "§cCan't use that number, max allowed is " + MAX_HEIGHT + "!");
            return false;
        }
        return true;
    }

    private boolean canFreefall(Player sender, Player target) {
        if (target.isDead()) {
            sender.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return false;
        }

        if (TrollManager.isActive(target.getUniqueId(), TrollType.FREEFALL)) {
            sender.sendMessage(StringManager.PREFIX + "§cCan't do this right now!");
            return false;
        }

        if (!plugin.canBeTrolled(target)) {
            sender.sendMessage(StringManager.BYPASS);
            return false;
        }

        return true;
    }

    private void executeFreefall(Player sender, Player target, int amount) {
        double newY = target.getLocation().getY() + amount;

        this.plugin.addTroll();
        plugin.getStats().addStats("Freefall", sender);
        TrollManager.activate(target.getUniqueId(), TrollType.FREEFALL);

        target.setFlying(false);
        target.setGameMode(GameMode.SURVIVAL);
        target.teleport(new Location(target.getWorld(), target.getLocation().getX(), newY, target.getLocation().getZ()));

        TrollManager.deactivate(target.getUniqueId(), TrollType.FREEFALL);
        sender.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §eis freefalling from §7" + newY + " §emeters!");
    }
}
