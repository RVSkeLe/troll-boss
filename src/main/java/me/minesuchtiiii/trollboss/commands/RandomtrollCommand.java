package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RandomtrollCommand implements CommandExecutor {
    private static final String PREFIX = StringManager.PREFIX;
    private static final String INCORRECT_USAGE = PREFIX + "§eUse §7/randomtroll [player]";
    private final TrollBoss plugin;
    private final Map<Integer, String> trollCommands = new HashMap<>();

    public RandomtrollCommand(TrollBoss plugin) {
        this.plugin = plugin;
        initializeTrollCommands();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.randomtroll")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(args.length == 0 ? INCORRECT_USAGE : StringManager.MUCHARGS);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.notOnline(player, args[0]);
            return true;
        }

        executeRandomTroll(player, target);
        return true;
    }

    private void executeRandomTroll(Player player, Player target) {
        player.sendMessage(PREFIX + "§ePicking a random troll for player §7" + target.getName() + "§e...");
        plugin.addTroll();
        plugin.getStats().addStats("Randomtroll", player);

        int randomTroll = plugin.createRandom(0, trollCommands.size());
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            String trollName = TrollType.values()[randomTroll - 1].name().toLowerCase(); // TODO: cleanup
            player.sendMessage(PREFIX + "§eRandomly picked troll: §7" + trollName + "§e!");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> executeTrollCommand(player, target, randomTroll), 10L);
        }, 30L);
    }

    private void executeTrollCommand(Player player, Player target, int trollId) {
        String command = trollCommands.get(trollId);
        if (command != null) {
            player.performCommand(command.replace("{target}", target.getName()));
        }
    }

    private void initializeTrollCommands() {
        trollCommands.put(1, "badapple {target}");
        trollCommands.put(2, "bolt {target}");
        trollCommands.put(3, "boom {target}");
        trollCommands.put(4, "burn {target}");
        trollCommands.put(5, "bury {target} 10");
        trollCommands.put(6, "crash {target}");
        trollCommands.put(7, "denymove {target} 20");
        trollCommands.put(8, "fakeop {target}");
        trollCommands.put(9, "fakedeop {target}");
        trollCommands.put(10, "fakerestart 60");
        trollCommands.put(11, "freefall {target} 50");
        trollCommands.put(12, "freeze {target}");
        trollCommands.put(13, "gokill {target} 30");
        trollCommands.put(14, "herobrine {target}");
        trollCommands.put(15, "hurt {target} 10");
        trollCommands.put(16, "infect {target} 25");
        trollCommands.put(17, "launch {target}");
        trollCommands.put(18, "nomine {target} 30");
        trollCommands.put(19, "potatotroll {target}");
        trollCommands.put(20, "pumpkinhead {target}");
        trollCommands.put(21, "push {target}");
        trollCommands.put(22, "randomtp {target} 10");
        trollCommands.put(23, "spam {target} 20");
        trollCommands.put(24, "starve {target} 16");
        trollCommands.put(25, "trap {target} 30");
        trollCommands.put(26, "trollkick {target}");
        trollCommands.put(27, "turn {target}");
        trollCommands.put(28, "void {target}");
        trollCommands.put(29, "webtrap {target} 15");
        trollCommands.put(30, "spank {target}");
        trollCommands.put(31, "trample {target} 9");
        trollCommands.put(32, "stfu {target}");
        trollCommands.put(33, "popup {target}");
        trollCommands.put(34, "sky {target} 20");
        trollCommands.put(35, "abduct {target}");
        trollCommands.put(36, "popular {target}");
        trollCommands.put(37, "creeper {target}");
        trollCommands.put(38, "sparta {target}");
        trollCommands.put(39, "drug {target}");
        trollCommands.put(40, "squidrain {target} 50");
        trollCommands.put(41, "dropinv {target}");
        trollCommands.put(42, "anvil {target}");
        trollCommands.put(43, "invtext {target}");
        trollCommands.put(44, "runforrest {target} 60");
        trollCommands.put(45, "border {target}");
        trollCommands.put(46, "noob {target}");
    }
}
