package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InvtextCommand implements CommandExecutor {

    private static final String COMMAND_NAME = "invtext";
    private final TrollBoss plugin;

    public InvtextCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] commandArgs) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.invtext")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (commandArgs.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/invtext [player]");
            return true;
        }

        if (commandArgs.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        final Player target = Bukkit.getPlayer(commandArgs[0]);

        if (target == null) {
            plugin.notOnline(player, commandArgs[0]);
            return true;
        }

        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return true;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        applyInventoryTextEffect(player, target);
        return true;
    }

    private void applyInventoryTextEffect(Player player, Player target) {
        plugin.addTroll();
        plugin.getStats().addStats("Invtext", player);
        plugin.storeInv(target);
        plugin.addAllInventoryTextItems(target);
        plugin.restoreInv(target, 20);
        player.sendMessage(StringManager.PREFIX + "§eAdded text to §7" + target.getName() + "§e's inventory!");
    }
}
