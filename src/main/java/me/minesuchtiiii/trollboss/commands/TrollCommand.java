package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.GuiManager;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrollCommand implements CommandExecutor {
    private static final String PERMISSION_TROLL = "troll.troll";
    private static final String PERMISSION_TROLL_HELP = "troll.trollhelp";
    private static final String PERMISSION_STATISTICS = "troll.statistics";
    private static final String PERMISSION_GUI = "troll.gui";

    private final static String PLUGIN_VERSION = TrollBoss.getInstance().getPluginMeta().getVersion();

    private final TrollBoss plugin;

    public TrollCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission(PERMISSION_TROLL)) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            openTrollInventory(player);
            return true;
        }

        if (args.length == 1) {
            if ("statistics".equalsIgnoreCase(args[0])) {
                openStatistics(player);
                return true;
            } else if ("help".equalsIgnoreCase(args[0])) {
                handleHelpCommand(player, args);
                return true;
            }

            openTargetGui(player, args[0]);
            return true;
        }

        if (args.length == 2) {
            if ("help".equalsIgnoreCase(args[0])) {
                handleHelpCommand(player, args);
                return true;
            }
        }

        player.sendMessage(StringManager.MUCHARGS);
        return true;
    }

    private void openTrollInventory(Player player) {
        GuiManager.openTrollInv(player);
        player.sendMessage(StringManager.PREFIX + "§eFor more commands use §7/troll help §c[page]");
    }

    private void handleHelpCommand(Player player, String[] args) {
        if (!player.hasPermission(PERMISSION_TROLL_HELP)) {
            player.sendMessage(StringManager.NOPERM);
            return;
        }

        int page = 1;
        if (args.length == 2 && plugin.isInt(args[1])) {
            page = Integer.parseInt(args[1]);
        } else if (args.length == 2 && !plugin.isInt(args[1])) {
            player.sendMessage(StringManager.PREFIX + "§cError! §e" + args[1] + " §cis not a number!");
            return;
        }

        sendHelp(player, page);
    }

    private void openStatistics(Player player) {
        if (player.hasPermission(PERMISSION_STATISTICS)) {
            plugin.openStatisticsInv(player);
        } else {
            player.sendMessage(StringManager.NOPERM);
        }
    }

    private void openTargetGui(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            plugin.notOnline(player, targetName);
            return;
        }

        if (plugin.canBeTrolled(target)) {
            if (player.hasPermission(PERMISSION_GUI)) {
                GuiManager.openGui(player);
                plugin.trolling.put(player.getUniqueId(), target.getUniqueId());
            } else {
                player.sendMessage(StringManager.NOPERM);
            }
        } else {
            player.sendMessage(StringManager.BYPASS);
        }
    }

    public void sendHelp(Player p, int i) {

        if ((i > 0) && (i < TrollBoss.HELP_PAGES + 1)) {
            if (i == 1) {

                p.sendMessage("§7§l|§e§l==============§7§l| §r§cHelp page §4" + i +
                        "§c/§4" + TrollBoss.HELP_PAGES + "§7§l |§e§l==============§7§l|");
                p.sendMessage("§a * §7[§cTrollBoss§7] §7§l§oHere's a list of all available commands:");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/troll");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/troll help §c[page]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/troll §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/troll statistics");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/trollop §7[true / false / status]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/trolltutorial <confirm / reject / stop>");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/burn §7[player / all]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/freeze §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/bolt §7[player / all]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/special §c[number] §7[player]");
                p.sendMessage("§a * §cType §4/troll help " + (i + 1) + " §cfor the next page");
                p.sendMessage("§7§l|§e§l===============§7§l| §r§cVersion §4" +
                        PLUGIN_VERSION + "§7§l |§e§l==============§7§l|");

            } else if (i == 2) {

                p.sendMessage("§7§l|§e§l==============§7§l| §r§cHelp page §4" + i +
                        "§c/§4" + TrollBoss.HELP_PAGES + "§7§l |§e§l==============§7§l|");
                p.sendMessage("§a * §7[§cTrollBoss§7] §7§l§oHere's a list of all available commands:");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/launch §7[player / all]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/fakeop §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/fakedeop §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/spam §7[player] §c[amount]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/trollkick §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/badapple §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/boom §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/push §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/gokill §7[player] §c[delay]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/switch §7[player] [player]");
                p.sendMessage("§a * §cType §4/troll help " + (i + 1) + " §cfor the next page");
                p.sendMessage("§7§l|§e§l===============§7§l| §r§cVersion §4" +
                        PLUGIN_VERSION + "§7§l |§e§l==============§7§l|");
            } else if (i == 3) {

                p.sendMessage("§7§l|§e§l==============§7§l| §r§cHelp page §4" + i +
                        "§c/§4" + TrollBoss.HELP_PAGES + "§7§l |§e§l==============§7§l|");
                p.sendMessage("§a * §7[§cTrollBoss§7] §7§l§oHere's a list of all available commands:");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/denymove §7[player / all] §c[delay]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/potatotroll §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/trap §7[player] §c[delay]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/tptroll §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/infect §7[player] §c[time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/herobrine §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/fakerestart §c[time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/turn §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/starve §7[player] §c[count]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/hurt §7[player] §c[count]");
                p.sendMessage("§a * §cType §4/troll help " + (i + 1) + " §cfor the next page");
                p.sendMessage("§7§l|§e§l===============§7§l| §r§cVersion §4" +
                        PLUGIN_VERSION + "§7§l |§e§l==============§7§l|");

            } else if (i == 4) {

                p.sendMessage("§7§l|§e§l==============§7§l| §r§cHelp page §4" + i +
                        "§c/§4" + TrollBoss.HELP_PAGES + "§7§l |§e§l==============§7§l|");
                p.sendMessage("§a * §7[§cTrollBoss§7] §7§l§oHere's a list of all available commands:");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/void §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/pumpkinhead §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/bury §7[player] §c[time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/nomine §7[player] §c[time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/randomtp §7[player] §c[count]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/crash §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/freefall §7[player] §c[high]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/webtrap §7[player] §c[time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/spank §7[player / all]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/trample §7[player] §c[amount]");
                p.sendMessage("§a * §cType §4/troll help " + (i + 1) + " §cfor the next page");
                p.sendMessage("§7§l|§e§l===============§7§l| §r§cVersion §4" +
                        PLUGIN_VERSION + "§7§l |§e§l==============§7§l|");

            } else if (i == 5) {

                p.sendMessage("§7§l|§e§l==============§7§l| §r§cHelp page §4" + i +
                        "§c/§4" + TrollBoss.HELP_PAGES + "§7§l |§e§l==============§7§l|");
                p.sendMessage("§a * §7[§cTrollBoss§7] §7§l§oHere's a list of all available commands:");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/stfu §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/popup §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/sky §7[player] §c[time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/abduct §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/popular §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/creeper §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/sparta §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/trollbows");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/drug §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/squidrain §7[player] §c[number]");
                p.sendMessage("§a * §cType §4/troll help " + (i + 1) + " §cfor the next page");
                p.sendMessage("§7§l|§e§l===============§7§l| §r§cVersion §4" +
                        PLUGIN_VERSION + "§7§l |§e§l==============§7§l|");

            } else {

                p.sendMessage("§7§l|§e§l==============§7§l| §r§cHelp page §4" + i +
                        "§c/§4" + TrollBoss.HELP_PAGES + "§7§l |§e§l==============§7§l|");
                p.sendMessage("§a * §7[§cTrollBoss§7] §7§l§oHere's a list of all available commands:");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/dropinv §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/garbage §7[player] §c[on | off]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/anvil §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/invtext §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/runforrest §7[player] [time]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/border §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/noob §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/randomtroll §7[player]");
                p.sendMessage("§a * §7[§cTrollBoss§7] §e/schlong §7[player]");
                p.sendMessage("§a * §cType §4/troll help " + (i - 1) + " §cto get to the previous page");
                p.sendMessage("§7§l|§e§l===============§7§l| §r§cVersion §4" +
                        PLUGIN_VERSION + "§7§l |§e§l==============§7§l|");

            }

        } else {
            p.sendMessage(StringManager.PREFIX + "§cCan't find help page §4" + i + "§c!");
        }

    }
}
