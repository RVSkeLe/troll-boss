package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.inventory.BowInventoryHolder;
import me.minesuchtiiii.trollboss.items.bows.BowsItem;
import me.minesuchtiiii.trollboss.utils.StringManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class BowsCommand implements CommandExecutor {
    private static final Component BOW_TITLE = MiniMessage.miniMessage().deserialize("<red>Choose a Troll-Bow</red>");
    private final TrollBoss plugin;

    public BowsCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!(player.hasPermission("troll.trollbows"))) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length != 0) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }
        
        openBowWindow(player);
        this.plugin.addTroll();
        plugin.getStats().addStats("Trollbows", player);

        return true;
    }

    public void openBowWindow(Player p) {
        final Inventory inv = Bukkit.createInventory(new BowInventoryHolder(), 9, BOW_TITLE);

        inv.setItem(0, BowsItem.boltBow());
        inv.setItem(1, BowsItem.boomBow());
        inv.setItem(2, BowsItem.creeperBow());
        inv.setItem(3, BowsItem.pullBow());
        inv.setItem(7, BowsItem.allBows());
        inv.setItem(8, BowsItem.back());

        inv.setItem(4, BowsItem.filler());
        inv.setItem(5, BowsItem.filler());
        inv.setItem(6, BowsItem.filler());

        p.openInventory(inv);
    }

}
