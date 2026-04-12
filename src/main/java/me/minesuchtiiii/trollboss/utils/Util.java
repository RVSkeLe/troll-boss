package me.minesuchtiiii.trollboss.utils;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getRandom(int lower, int upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("lower > upper");
        }
        return ThreadLocalRandom.current().nextInt(lower, upper + 1);
    }

    public static boolean isInventoryEmpty(Player p) {
        return p.getInventory().isEmpty();
    }

    public static void saveDefaultConfigFile(TrollBoss plugin) {
        FileConfiguration config = plugin.getConfig();
        config.addDefault("Auto-Update", true);
        config.addDefault("Troll-Operators", true);
        config.addDefault("Trolls", 0);

        //TODO: This needs to be addressed since MC is about to switch to Calendar Versioning (e.g. 26.1.1)
        int version = Integer.parseInt(plugin.getServer().getMinecraftVersion().split("\\.")[1]);
        int subVersion = Integer.parseInt(plugin.getServer().getMinecraftVersion().split("\\.")[2]);

        if (version >= 19 || version == 18 && subVersion >= 1) {
            config.options().setHeader(List.of("Some options you can edit"));

            config.options().parseComments(true);

            config.setComments("Trolls", List.of("Used for the statistics, should not be touched"));
            config.setComments("Troll-Operators", List.of("Define if operators can be trolled or not", "default: true"));
            config.setComments("Auto-Update", List.of("Define if the plugin should automatically update when a new version is available", "default: true"));
        } else if (plugin.getServer().getMinecraftVersion().contains("1.18-") || version <= 17 && version >= 14) {
            config.options().setHeader(List.of("Some options you can edit"));
        }

        config.options().copyDefaults(true);
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        plugin.saveConfig();
    }

    public static void closeGui(Player p) {
        p.getOpenInventory().close();
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }
}
