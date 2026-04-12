package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SwitchCommand implements CommandExecutor {
    private final TrollBoss plugin;

    public SwitchCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.switch")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/switch [player] [player]");
            return true;
        }

        Player playerOne = Bukkit.getPlayer(args[0]);
        Player playerTwo = Bukkit.getPlayer(args[1]);

        if (playerOne == null && playerTwo == null) {
            player.sendMessage(StringManager.PREFIX + "§eThese players aren't online!");
            return true;
        } else if (playerOne == null) {
            Util.notOnline(player, args[0]);
            return true;
        } else if (playerTwo == null) {
            Util.notOnline(player, args[1]);
            return true;
        }

        if (plugin.canBeTrolled(playerOne) && plugin.canBeTrolled(playerTwo)) {
            switchPlayerLocations(playerOne, playerTwo, player);
        } else {
            player.sendMessage(StringManager.PREFIX + "§cOne of those players cannot be trolled!");
        }
        return true;

    }

    private void switchPlayerLocations(Player playerOne, Player playerTwo, Player executingPlayer) {
        Location locOfPlayerOne = playerOne.getLocation();
        Location locOfPlayerTwo = playerTwo.getLocation();

        playerOne.teleport(locOfPlayerTwo);
        playerTwo.teleport(locOfPlayerOne);

        executingPlayer.sendMessage(StringManager.PREFIX + "§eSwitched §7" + playerOne.getName() + "§e's Location with §7" + playerTwo.getName() + "§e's Location!");
        plugin.addTroll();
        plugin.getStats().addStats("Switch", executingPlayer);
    }
}
