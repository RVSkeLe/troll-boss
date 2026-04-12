package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
            Util.notOnline(player, commandArgs[0]);
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
        addAllInventoryTextItems(target);
        plugin.restoreInv(target, 20);
        player.sendMessage(StringManager.PREFIX + "§eAdded text to §7" + target.getName() + "§e's inventory!");
    }

    private void addInventoryTextItem(Player p, int inventorySlot, String text, int amount) {
        final ItemStack dirt1 = new ItemStack(Material.DIRT, amount);
        final ItemMeta dirt1meta = dirt1.getItemMeta();
        dirt1meta.setDisplayName(Util.getRandomColor() + text);
        dirt1.setItemMeta(dirt1meta);

        p.getInventory().setItem(inventorySlot, dirt1);

    }

    public void addAllInventoryTextItems(Player p) {

        p.getInventory().clear();

        // Line 1
        addInventoryTextItem(p, 9, "Hello,", 1);
        addInventoryTextItem(p, 10, "I", 2);
        addInventoryTextItem(p, 11, "hope", 3);
        addInventoryTextItem(p, 12, "you", 4);
        addInventoryTextItem(p, 13, "can", 5);
        addInventoryTextItem(p, 14, "need", 6);
        addInventoryTextItem(p, 15, "some", 7);
        addInventoryTextItem(p, 16, "fresh", 8);
        addInventoryTextItem(p, 17, "dirt..", 9);
        // Line 2
        addInventoryTextItem(p, 18, "if", 10);
        addInventoryTextItem(p, 19, "not...", 11);
        addInventoryTextItem(p, 20, "well", 12);
        addInventoryTextItem(p, 21, "it's", 13);
        addInventoryTextItem(p, 22, "too", 14);
        addInventoryTextItem(p, 23, "late", 15);
        addInventoryTextItem(p, 24, "anyway..", 16);
        addInventoryTextItem(p, 25, "old", 17);
        addInventoryTextItem(p, 26, "items", 18);
        // Line 3
        addInventoryTextItem(p, 27, "are", 19);
        addInventoryTextItem(p, 28, "gone", 20);
        addInventoryTextItem(p, 29, "for", 21);
        addInventoryTextItem(p, 30, "ever..", 22);
        addInventoryTextItem(p, 31, "hehe", 23);
        addInventoryTextItem(p, 32, "see", 24);
        addInventoryTextItem(p, 33, "you..", 25);
        addInventoryTextItem(p, 34, "have", 26);
        addInventoryTextItem(p, 35, "fun!", 27);

    }
}
