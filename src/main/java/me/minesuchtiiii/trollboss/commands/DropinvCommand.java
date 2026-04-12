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

public class DropinvCommand implements CommandExecutor {

    private final TrollBoss plugin;

    public DropinvCommand(TrollBoss plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.dropinv")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/dropinv [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        handleDropInventory(player, args[0]);
        return true;
    }

    private void handleDropInventory(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            this.plugin.notOnline(player, targetName);
            return;
        }

        if (target.isDead()) {
            player.sendMessage(StringManager.FAILDEAD);
            return;
        }

        if (this.plugin.isInventoryEmpty(target)) {
            player.sendMessage(StringManager.PREFIX + "§cCan't use that command on that player, his inventory is empty!");
            return;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return;
        }

        executeDropInventory(player, target);
    }

    private void executeDropInventory(Player player, Player target) {
        final long DELAY = 300L; // Introduced constant for readability

        this.plugin.addTroll();
        plugin.getStats().addStats("Dropinv", player);
        this.plugin.dropItems(target);
        this.plugin.dropArmor(target);

        player.sendMessage(StringManager.PREFIX + "§7" + target.getName() + " §edropped all of his items!");
        TrollManager.activate(target.getUniqueId(), TrollType.DROPINV);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () ->
                TrollManager.deactivate(target.getUniqueId(), TrollType.DROPINV), DELAY);
    }
}
