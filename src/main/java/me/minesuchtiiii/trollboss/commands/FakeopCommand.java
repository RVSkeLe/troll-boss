package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FakeopCommand implements CommandExecutor {

    private static final String PERMISSION_FAKEOP = "troll.fakeop";
    private static final String PERMISSION_BYPASS = "troll.bypass";
    private final TrollBoss plugin;

    public FakeopCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERMISSION_FAKEOP)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/fakeop [player]");
            return true;
        }

        if ("all".equalsIgnoreCase(args[0])) {
            fakeOpAllPlayers(player);
        } else {
            fakeOpPlayer(player, args[0]);
        }

        return true;
    }

    private void fakeOpPlayer(Player sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            plugin.notOnline(sender, targetName);
            return;
        }

        if (target.isOp() && !plugin.getConfig().getBoolean("Troll-Operators")) {
            sender.sendMessage(StringManager.BYPASS);
            return;
        }

        if (target.hasPermission(PERMISSION_BYPASS)) {
            sender.sendMessage(StringManager.BYPASS);
            return;
        }

        performFakeOp(sender, target);
    }

    private void fakeOpAllPlayers(Player sender) {

        Bukkit.getOnlinePlayers().stream()
                .filter(onlinePlayer -> onlinePlayer != sender && plugin.canBeTrolled(onlinePlayer))
                .forEach(onlinePlayer -> onlinePlayer.sendMessage("§7§o[" + sender.getDisplayName() + ": Opped " + onlinePlayer.getDisplayName() + "§7]"));

        plugin.addTroll();
        plugin.getStats().addStats("Fakeop", sender);
        sender.sendMessage(StringManager.PREFIX + "§eYou fake oped everyone, except players who can bypass it!");
    }

    private void performFakeOp(Player sender, Player target) {
        target.sendMessage("§7§o[" + sender.getDisplayName() + ": Opped " + target.getDisplayName() + "§7]");
        sender.sendMessage(StringManager.PREFIX + "§eYou have fake oped §7" + target.getName() + "§e!");
        plugin.addTroll();
        plugin.getStats().addStats("Fakeop", sender);
    }
}
