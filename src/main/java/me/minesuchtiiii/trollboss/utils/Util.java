package me.minesuchtiiii.trollboss.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class Util {
    public static List<String> colors = List.of("§a", "§b", "§c", "§d", "§e", "§f", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§o", "§k", "§m", "§n", "§l");

    public static String getRandomColor() {
        return colors.get(getRandomColorIndex());
    }

    public static int getRandomColorIndex() {
        return (int) (Math.random() * colors.size());
    }

    public static ItemStack getHead(Player p) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(Bukkit.getPlayer(p.getName()));
        item.setItemMeta(skull);

        return item;
    }

    /**
     * Teleports the player to the center of the block they are currently standing on.
     * Adjusts the player's X and Z coordinates to be precisely at the middle of their current block.
     *
     * @param p the player to be centered on the block
     */
    public static void centerPlayer(Player p) {
        Location loc = p.getLocation();
        loc.setX(loc.getBlockX() + 0.5);
        loc.setZ(loc.getBlockZ() + 0.5);
        p.teleport(loc);
    }

    public static void notOnline(Player p, String name) {
        p.sendMessage(StringManager.PREFIX + "§ePlayer §7" + name + " §eis not online!");
    }
}
