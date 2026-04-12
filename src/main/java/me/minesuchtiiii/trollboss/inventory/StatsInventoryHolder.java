package me.minesuchtiiii.trollboss.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class StatsInventoryHolder implements InventoryHolder {
    @Override
    public @NotNull Inventory getInventory() {
        throw new UnsupportedOperationException("Not used directly");
    }
}
