package me.minesuchtiiii.trollboss.trolls;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {
    private final HashMap<UUID, Inventory> invstores = new HashMap<>();

    public void storeInv(Player p) {
        Inventory clone = Bukkit.createInventory(p, InventoryType.PLAYER);
        clone.setContents(p.getInventory().getContents());
        invstores.put(p.getUniqueId(), clone);
    }

    public void restoreInv(Player p, int sec) {

        if (invstores.containsKey(p.getUniqueId())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(TrollBoss.getInstance(), () -> {

                p.getInventory().clear();
                p.getInventory().setContents(invstores.get(p.getUniqueId()).getContents());
                invstores.remove(p.getUniqueId());
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                p.sendMessage("§eJust a prank, here's your old inventory!");

            }, sec * 20L);

        }
    }
}
