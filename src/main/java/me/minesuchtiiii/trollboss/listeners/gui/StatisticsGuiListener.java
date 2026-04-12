package me.minesuchtiiii.trollboss.listeners.gui;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class StatisticsGuiListener implements Listener {

    private final TrollBoss plugin;

    public StatisticsGuiListener(TrollBoss plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractInStatisticsGui(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if ("§cTroll Statistics".equals(e.getView().getTitle())) {

            e.setCancelled(true);

            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.EMERALD) {

                this.plugin.closeGui(p);

            } else if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BOW) {

                plugin.getStats().openBowStatisticsInv(p);

            }


        } else if ("§cTrollbow Statistics".equals(e.getView().getTitle())) {

            e.setCancelled(true);

            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.EMERALD) {

                plugin.getStats().openStatisticsInv(p);

            }

        }

    }

}
