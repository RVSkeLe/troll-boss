package me.minesuchtiiii.trollboss.listeners.gui;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.trolltutorial.TutorialManager;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TrollInvListener implements Listener {
    public static boolean c;

    private final TrollBoss plugin;

    public TrollInvListener(TrollBoss plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof final Player p)) return;
        if (!("§cTroll a player".equals(e.getView().getTitle()))) return;
        if (!(TutorialManager.isUsable(p))) {
            p.updateInventory();

            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                final ItemStack item = e.getCurrentItem();

                e.setCancelled(true);

                final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                final Player target = Bukkit.getPlayer(name);

                if (target != null) {
                    if (!(target.isDead())) {
                        p.performCommand("troll " + name);
                    } else {
                        p.sendMessage(StringManager.FAILDEAD);
                    }
                } else {
                    Util.notOnline(p, name);
                }
            }
        } else {
            if (!c) {
                p.sendMessage(StringManager.PREFIX + "§cNot available in the tutorial!");
                e.setCancelled(true);
                c = true;
            } else {
                e.setCancelled(true);
            }

        }
    }

}
