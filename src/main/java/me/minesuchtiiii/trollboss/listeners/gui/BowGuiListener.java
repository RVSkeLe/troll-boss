package me.minesuchtiiii.trollboss.listeners.gui;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.inventory.BowInventoryHolder;
import me.minesuchtiiii.trollboss.items.bows.BowsItem;
import me.minesuchtiiii.trollboss.manager.GuiManager;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BowGuiListener implements Listener {

    private final TrollBoss plugin;

    public BowGuiListener(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractInBowGui(InventoryClickEvent e) {

        if (e.getClickedInventory() == null) return;
        if (!(e.getInventory().getHolder(false) instanceof BowInventoryHolder)) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;

        int raw = e.getRawSlot();
        if (raw >= e.getView().getTopInventory().getSize()) return;


        e.setCancelled(true);

        switch (raw) {
            case 0 -> giveBow(p, BowsItem.boltBow(), "Bolt");
            case 1 -> giveBow(p, BowsItem.boomBow(), "Boom");
            case 2 -> giveBow(p, BowsItem.creeperBow(), "Creeper");
            case 3 -> giveBow(p, BowsItem.pullBow(), "Pull");
            case 7 -> giveAllBows(p);
            case 8 -> {
                p.closeInventory();
                GuiManager.openGui(p);
            }
            // filler slots; do nothing
            case 4, 5, 6 -> {}
        }
    }

    private void giveBow(Player p, ItemStack item, String type) {
        p.getInventory().addItem(item);
        Util.closeGui(p);

        p.sendMessage(StringManager.PREFIX + "§eHave fun with the §7" + type + " Bow§e!");

        plugin.addTroll();
        plugin.getStats().addBowStats(type);
    }

    private void giveAllBows(Player p) {
        p.getInventory().addItem(BowsItem.boltBow());
        p.getInventory().addItem(BowsItem.boomBow());
        p.getInventory().addItem(BowsItem.creeperBow());
        p.getInventory().addItem(BowsItem.pullBow());

        Util.closeGui(p);

        p.sendMessage(StringManager.PREFIX + "§eHave fun with those bows!");

        plugin.addTroll();
        plugin.addTroll();
        plugin.addTroll();
        plugin.addTroll();

        plugin.getStats().addBowStats("Bolt");
        plugin.getStats().addBowStats("Boom");
        plugin.getStats().addBowStats("Creeper");
        plugin.getStats().addBowStats("Pull");
    }
}