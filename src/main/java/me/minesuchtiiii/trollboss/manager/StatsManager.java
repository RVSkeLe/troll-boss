package me.minesuchtiiii.trollboss.manager;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.inventory.BowStatsInventoryHolder;
import me.minesuchtiiii.trollboss.inventory.StatsInventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public final class StatsManager {
    private final File statsFile;
    private FileConfiguration statsConfig; // removed final
    private final TrollBoss plugin;

    public StatsManager(TrollBoss plugin) {
        this.plugin = plugin;
        this.statsFile = new File(plugin.getDataFolder(), "stats.yml");
    }

    public void checkFile() {
        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
                plugin.getLogger().info("Created stats file.");
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create stats file", e);
            }
        }

        // load AFTER file exists
        this.statsConfig = YamlConfiguration.loadConfiguration(statsFile);

        addStatsDefaults();
        saveStats();
        plugin.getLogger().info("Saved stats file.");
    }

    private void saveStats() {
        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save stats file", e);
        }
    }

    public void updateLastUsedUser(Player p, String troll) {
        statsConfig.set("LastUsed." + troll, p.getName());
        saveStats();
    }

    public String getLastUsedUser(String troll) {
        return statsConfig.getString("LastUsed." + troll);
    }

    public int getStats(String cmd) {
        return statsConfig.getInt("Troll." + cmd);
    }

    public void addStats(String cmd, Player lastused) {
        String path = "Troll." + cmd;
        statsConfig.set(path, statsConfig.getInt(path) + 1);
        saveStats();
        updateLastUsedUser(lastused, cmd);
    }

    private void addStatsDefaults() {
        statsConfig.addDefault("Troll.Burn", 0);
        statsConfig.addDefault("Troll.Freeze", 0);
        statsConfig.addDefault("Troll.Bolt", 0);
        statsConfig.addDefault("Troll.Special", 0);
        statsConfig.addDefault("Troll.Boom", 0);
        statsConfig.addDefault("Troll.Push", 0);
        statsConfig.addDefault("Troll.Fakeop", 0);
        statsConfig.addDefault("Troll.Fakedeop", 0);
        statsConfig.addDefault("Troll.Launch", 0);
        statsConfig.addDefault("Troll.Spam", 0);
        statsConfig.addDefault("Troll.Gokill", 0);
        statsConfig.addDefault("Troll.Switch", 0);
        statsConfig.addDefault("Troll.Trollkick", 0);
        statsConfig.addDefault("Troll.Badapple", 0);
        statsConfig.addDefault("Troll.Potatotroll", 0);
        statsConfig.addDefault("Troll.Trap", 0);
        statsConfig.addDefault("Troll.Teleporttroll", 0);
        statsConfig.addDefault("Troll.Infect", 0);
        statsConfig.addDefault("Troll.Herobrine", 0);
        statsConfig.addDefault("Troll.Fakerestart", 0);
        statsConfig.addDefault("Troll.Turn", 0);
        statsConfig.addDefault("Troll.Starve", 0);
        statsConfig.addDefault("Troll.Hurt", 0);
        statsConfig.addDefault("Troll.Void", 0);
        statsConfig.addDefault("Troll.Pumpkinhead", 0);
        statsConfig.addDefault("Troll.Bury", 0);
        statsConfig.addDefault("Troll.Nomine", 0);
        statsConfig.addDefault("Troll.Randomteleport", 0);
        statsConfig.addDefault("Troll.Crash", 0);
        statsConfig.addDefault("Troll.Freefall", 0);
        statsConfig.addDefault("Troll.Webtrap", 0);
        statsConfig.addDefault("Troll.Spank", 0);
        statsConfig.addDefault("Troll.Trample", 0);
        statsConfig.addDefault("Troll.Stfu", 0);
        statsConfig.addDefault("Troll.Popup", 0);
        statsConfig.addDefault("Troll.Sky", 0);
        statsConfig.addDefault("Troll.Abduct", 0);
        statsConfig.addDefault("Troll.Popular", 0);
        statsConfig.addDefault("Troll.Creeper", 0);
        statsConfig.addDefault("Troll.Sparta", 0);
        statsConfig.addDefault("Troll.Trollbows", 0);
        statsConfig.addDefault("Troll.Drug", 0);
        statsConfig.addDefault("Troll.Squidrain", 0);
        statsConfig.addDefault("Troll.Dropinv", 0);
        statsConfig.addDefault("Troll.Garbage", 0);
        statsConfig.addDefault("Troll.Anvil", 0);
        statsConfig.addDefault("Troll.Invtext", 0);
        statsConfig.addDefault("Troll.Runforrest", 0);
        statsConfig.addDefault("Troll.Border", 0);
        statsConfig.addDefault("Troll.Noob", 0);
        statsConfig.addDefault("Troll.Randomtroll", 0);
        statsConfig.addDefault("Troll.Schlong", 0);
        statsConfig.addDefault("Troll.Denymove", 0);

        statsConfig.addDefault("Troll.Bows.Bolt", 0);
        statsConfig.addDefault("Troll.Bows.Boom", 0);
        statsConfig.addDefault("Troll.Bows.Creeper", 0);
        statsConfig.addDefault("Troll.Bows.Pull", 0);


        statsConfig.addDefault("LastUsed.Burn", "Nobody");
        statsConfig.addDefault("LastUsed.Freeze", "Nobody");
        statsConfig.addDefault("LastUsed.Bolt", "Nobody");
        statsConfig.addDefault("LastUsed.Special", "Nobody");
        statsConfig.addDefault("LastUsed.Boom", "Nobody");
        statsConfig.addDefault("LastUsed.Push", "Nobody");
        statsConfig.addDefault("LastUsed.Fakeop", "Nobody");
        statsConfig.addDefault("LastUsed.Fakedeop", "Nobody");
        statsConfig.addDefault("LastUsed.Launch", "Nobody");
        statsConfig.addDefault("LastUsed.Spam", "Nobody");
        statsConfig.addDefault("LastUsed.Gokill", "Nobody");
        statsConfig.addDefault("LastUsed.Switch", "Nobody");
        statsConfig.addDefault("LastUsed.Trollkick", "Nobody");
        statsConfig.addDefault("LastUsed.Badapple", "Nobody");
        statsConfig.addDefault("LastUsed.Potatotroll", "Nobody");
        statsConfig.addDefault("LastUsed.Trap", "Nobody");
        statsConfig.addDefault("LastUsed.Tptroll", "Nobody");
        statsConfig.addDefault("LastUsed.Infect", "Nobody");
        statsConfig.addDefault("LastUsed.Herobrine", "Nobody");
        statsConfig.addDefault("LastUsed.Fakerestart", "Nobody");
        statsConfig.addDefault("LastUsed.Turn", "Nobody");
        statsConfig.addDefault("LastUsed.Starve", "Nobody");
        statsConfig.addDefault("LastUsed.Teleporttroll", "Nobody");
        statsConfig.addDefault("LastUsed.Hurt", "Nobody");
        statsConfig.addDefault("LastUsed.Void", "Nobody");
        statsConfig.addDefault("LastUsed.Pumpkinhead", "Nobody");
        statsConfig.addDefault("LastUsed.Bury", "Nobody");
        statsConfig.addDefault("LastUsed.Nomine", "Nobody");
        statsConfig.addDefault("LastUsed.Randomteleport", "Nobody");
        statsConfig.addDefault("LastUsed.Crash", "Nobody");
        statsConfig.addDefault("LastUsed.Freefall", "Nobody");
        statsConfig.addDefault("LastUsed.Webtrap", "Nobody");
        statsConfig.addDefault("LastUsed.Spank", "Nobody");
        statsConfig.addDefault("LastUsed.Trample", "Nobody");
        statsConfig.addDefault("LastUsed.Stfu", "Nobody");
        statsConfig.addDefault("LastUsed.Popup", "Nobody");
        statsConfig.addDefault("LastUsed.Sky", "Nobody");
        statsConfig.addDefault("LastUsed.Abduct", "Nobody");
        statsConfig.addDefault("LastUsed.Popular", "Nobody");
        statsConfig.addDefault("LastUsed.Creeper", "Nobody");
        statsConfig.addDefault("LastUsed.Sparta", "Nobody");
        statsConfig.addDefault("LastUsed.Trollbows", "Nobody");
        statsConfig.addDefault("LastUsed.Drug", "Nobody");
        statsConfig.addDefault("LastUsed.Squidrain", "Nobody");
        statsConfig.addDefault("LastUsed.Dropinv", "Nobody");
        statsConfig.addDefault("LastUsed.Garbage", "Nobody");
        statsConfig.addDefault("LastUsed.Anvil", "Nobody");
        statsConfig.addDefault("LastUsed.Invtext", "Nobody");
        statsConfig.addDefault("LastUsed.Runforrest", "Nobody");
        statsConfig.addDefault("LastUsed.Border", "Nobody");
        statsConfig.addDefault("LastUsed.Noob", "Nobody");
        statsConfig.addDefault("LastUsed.Randomtroll", "Nobody");
        statsConfig.addDefault("LastUsed.Schlong", "Nobody");
        statsConfig.addDefault("LastUsed.Denymove", "Nobody");

        statsConfig.options().copyDefaults(true);
    }

    private int getBowStats(String bow) {
        return statsConfig.getInt("Troll.Bows." + bow);
    }

    public int getAllBowStats() {
        final int bow1 = statsConfig.getInt("Troll.Bows.Bolt");
        final int bow2 = statsConfig.getInt("Troll.Bows.Boom");
        final int bow3 = statsConfig.getInt("Troll.Bows.Creeper");
        final int bow4 = statsConfig.getInt("Troll.Bows.Pull");

        return bow1 + bow2 + bow3 + bow4;
    }

    public void addBowStats(String bow) {
        statsConfig.set("Troll.Bows." + bow, getBowStats(bow) + 1);
        saveStats();
    }

    public void openBowStatisticsInv(Player p) {

        final Inventory inv = Bukkit.createInventory(new BowStatsInventoryHolder(), 9, "§cTrollbow Statistics");

        createItemForGui(1, Material.EMERALD, "§bClose the gui", 8, inv, "§7Closes the Statistics-Gui");
        createItemForGui(1, Material.BOW, "§eBolt Bow", 0, inv, "§7Times used: §a" + getStats("Bows.Bolt"));
        createItemForGui(1, Material.BOW, "§eBoom Bow", 1, inv, "§7Times used: §a" + getStats("Bows.Boom"));
        createItemForGui(1, Material.BOW, "§eCreeper Bow", 2, inv, "§7Times used: §a" + getStats("Bows.Creeper"));
        createItemForGui(1, Material.BOW, "§ePull Bow", 3, inv, "§7Times used: §a" + getStats("Bows.Pull"));

        p.openInventory(inv);

    }

    public void openStatisticsInv(Player p) {

        final Inventory inv = Bukkit.createInventory(new StatsInventoryHolder(), 54, "§cTroll Statistics");

        createItemForGui(1, Material.EMERALD, "§bClose the gui", 53, inv, "§7Closes the Statistics-Gui");
        createItemForGui(1, Material.APPLE, "§eBadapple", 0, inv, "§7Times used: §a" + getStats("Badapple"), "§7Last used by: §a" + getLastUsedUser("Badapple"));
        createItemForGui(1, Material.FIRE_CHARGE, "§eBolt", 1, inv, "§7Times used: §a" + getStats("Bolt"), "§7Last used by: §a" + getLastUsedUser("Bolt"));
        createItemForGui(1, Material.TNT, "§eBoom", 2, inv, "§7Times used: §a" + getStats("Boom"), "§7Last used by: §a" + getLastUsedUser("Boom"));
        createItemForGui(1, Material.LAVA_BUCKET, "§eBurn", 3, inv, "§7Times used: §a" + getStats("Burn"), "§7Last used by: §a" + getLastUsedUser("Burn"));
        createItemForGui(1, Material.DIRT, "§eBury", 4, inv, "§7Times used: §a" + getStats("Bury"), "§7Last used by: §a" + getLastUsedUser("Bury"));
        createItemForGui(1, Material.STRING, "§eCrash", 5, inv, "§7Times used: §a" + getStats("Crash"), "§7Last used by: §a" + getLastUsedUser("Crash"));
        createItemForGui(1, Material.LEATHER_BOOTS, "§eDenymove", 6, inv, "§7Times used: §a" + getStats("Denymove"), "§7Last used by: §a" + getLastUsedUser("Denymove"));
        createItemForGui(1, Material.DIAMOND, "§eFakeop", 7, inv, "§7Times used: §a" + getStats("Fakeop"), "§7Last used by: §a" + getLastUsedUser("Fakeop"));
        createItemForGui(1, Material.GOLD_INGOT, "§eFakedeop", 8, inv, "§7Times used: §a" + getStats("Fakedeop"), "§7Last used by: §a" + getLastUsedUser("Fakedeop"));
        createItemForGui(1, Material.BLAZE_POWDER, "§eFakerestart", 9, inv, "§7Times used: §a" + getStats("Fakerestart"), "§7Last used by: §a" + getLastUsedUser("Fakerestart"));
        createItemForGui(1, Material.WHITE_WOOL, "§eFreefall", 10, inv, "§7Times used: §a" + getStats("Freefall"), "§7Last used by: §a" + getLastUsedUser("Freefall"));
        createItemForGui(1, Material.ICE, "§eFreeze", 11, inv, "§7Times used: §a" + getStats("Freeze"), "§7Last used by: §a" + getLastUsedUser("Freeze"));
        createItemForGui(1, Material.SOUL_SAND, "§eGokill", 12, inv, "§7Times used: §a" + getStats("Gokill"), "§7Last used by: §a" + getLastUsedUser("Gokill"));
        createItemForGui(1, Material.GLOWSTONE_DUST, "§eHerobrine", 13, inv, "§7Times used: §a" + getStats("Herobrine"), "§7Last used by: §a" + getLastUsedUser("Herobrine"));
        createItemForGui(1, Material.REDSTONE, "§eBoom", 14, inv, "§7Times used: §a" + getStats("Hurt"), "§7Last used by: §a" + getLastUsedUser("Hurt"));
        createItemForGui(1, Material.POTION, "§eInfect", 15, inv, "§7Times used: §a" + getStats("Infect"), "§7Last used by: §a" + getLastUsedUser("Infect"));
        createItemForGui(1, Material.FIREWORK_ROCKET, "§eLaunch", 16, inv, "§7Times used: §a" + getStats("Launch"), "§7Last used by: §a" + getLastUsedUser("Launch"));
        createItemForGui(1, Material.GRASS_BLOCK, "§eNomine", 17, inv, "§7Times used: §a" + getStats("Nomine"), "§7Last used by: §a" + getLastUsedUser("Nomine"));
        createItemForGui(1, Material.BAKED_POTATO, "§ePotatotroll", 18, inv, "§7Times used: §a" + getStats("Potatotroll"), "§7Last used by: §a" + getLastUsedUser("Potatotroll"));
        createItemForGui(1, Material.JACK_O_LANTERN, "§ePumpkinhead", 19, inv, "§7Times used: §a" + getStats("Pumpkinhead"), "§7Last used by: §a" + getLastUsedUser("Pumpkinhead"));
        createItemForGui(1, Material.FEATHER, "§ePush", 20, inv, "§7Times used: §a" + getStats("Push"), "§7Last used by: §a" + getLastUsedUser("Push"));
        createItemForGui(1, Material.ENDER_PEARL, "§eRandomteleport", 21, inv, "§7Times used: §a" + getStats("Randomteleport"), "§7Last used by: §a" + getLastUsedUser("Randomteleport"));
        createItemForGui(1, Material.OAK_SIGN, "§eSpam", 22, inv, "§7Times used: §a" + getStats("Spam"), "§7Last used by: §a" + getLastUsedUser("Spam"));
        createItemForGui(1, Material.CHEST, "§eSpecial", 23, inv, "§7Times used: §a" + getStats("Special"), "§7Last used by: §a" + getLastUsedUser("Special"));
        createItemForGui(1, Material.COOKED_CHICKEN, "§eStarve", 24, inv, "§7Times used: §a" + getStats("Starve"), "§7Last used by: §a" + getLastUsedUser("Starve"));
        createItemForGui(1, Material.FISHING_ROD, "§eTeleporttroll", 25, inv, "§7Times used: §a" + getStats("Teleporttroll"), "§7Last used by: §a" + getLastUsedUser("Teleporttroll"));
        createItemForGui(1, Material.BEDROCK, "§eTrap", 26, inv, "§7Times used: §a" + getStats("Trap"), "§7Last used by: §a" + getLastUsedUser("Trap"));
        createItemForGui(1, Material.IRON_DOOR, "§eTrollkick", 27, inv, "§7Times used: §a" + getStats("Trollkick"), "§7Last used by: §a" + getLastUsedUser("Trollkick"));
        createItemForGui(1, Material.PAPER, "§eTurn", 28, inv, "§7Times used: §a" + getStats("Turn"), "§7Last used by: §a" + getLastUsedUser("Turn"));
        createItemForGui(1, Material.OBSIDIAN, "§eVoid", 29, inv, "§7Times used: §a" + getStats("Void"), "§7Last used by: §a" + getLastUsedUser("Void"));
        createItemForGui(1, Material.COBWEB, "§eWebtrap", 30, inv, "§7Times used: §a" + getStats("Webtrap"), "§7Last used by: §a" + getLastUsedUser("Webtrap"));
        createItemForGui(1, Material.BONE, "§eSpank", 31, inv, "§7Times used: §a" + getStats("Spank"), "§7Last used by: §a" + getLastUsedUser("Spank"));
        createItemForGui(1, Material.COW_SPAWN_EGG, "§eTrample", 32, inv, "§7Times used: §a" + getStats("Trample"), "§7Last used by: §a" + getLastUsedUser("Trample"));
        createItemForGui(1, Material.LEVER, "§eStfu", 33, inv, "§7Times used: §a" + getStats("Stfu"), "§7Last used by: §a" + getLastUsedUser("Stfu"));
        createItemForGui(1, Material.BOOK, "§ePopup", 34, inv, "§7Times used: §a" + getStats("Popup"), "§7Last used by: §a" + getLastUsedUser("Popup"));
        createItemForGui(1, Material.GLASS, "§eSky", 35, inv, "§7Times used: §a" + getStats("Sky"), "§7Last used by: §a" + getLastUsedUser("Sky"));
        createItemForGui(1, Material.CLOCK, "§eAbduct", 36, inv, "§7Times used: §a" + getStats("Abduct"), "§7Last used by: §a" + getLastUsedUser("Abduct"));
        createItemForGui(1, Material.EXPERIENCE_BOTTLE, "§ePopular", 37, inv, "§7Times used: §a" + getStats("Popular"), "§7Last used by: §a" + getLastUsedUser("Popular"));
        createItemForGui(1, Material.CREEPER_SPAWN_EGG, "§eCreeper", 38, inv, "§7Times used: §a" + getStats("Creeper"), "§7Last used by: §a" + getLastUsedUser("Creeper"));
        createItemForGui(1, Material.ARROW, "§eSparta", 39, inv, "§7Times used: §a" + getStats("Sparta"), "§7Last used by: §a" + getLastUsedUser("Sparta"));
        createItemForGui(1, Material.WHEAT, "§eDrug", 40, inv, "§7Times used: §a" + getStats("Drug"), "§7Last used by: §a" + getLastUsedUser("Drug"));
        createItemForGui(1, Material.INK_SAC, "§eSquidrain", 41, inv, "§7Times used: §a" + getStats("Squidrain"), "§7Last used by: §a" + getLastUsedUser("Squidrain"));
        createItemForGui(1, Material.DROPPER, "§eDropinv", 42, inv, "§7Times used: §a" + getStats("Dropinv"), "§7Last used by: §a" + getLastUsedUser("Dropinv"));
        createItemForGui(1, Material.WRITABLE_BOOK, "§eGarbage", 43, inv, "§7Times used: §a" + getStats("Garbage"), "§7Last used by: §a" + getLastUsedUser("Garbage"));
        createItemForGui(1, Material.ANVIL, "§eAnvil", 44, inv, "§7Times used: §a" + getStats("Anvil"), "§7Last used by: §a" + getLastUsedUser("Anvil"));
        createItemForGui(2, Material.PAPER, "§eInvtext", 45, inv, "§7Times used: §a" + getStats("Invtext"), "§7Last used by: §a" + getLastUsedUser("Invtext"));
        createItemForGui(1, Material.IRON_BOOTS, "§eRunforrest", 46, inv, "§7Times used: §a" + getStats("Runforrest"), "§7Last used by: §a" + getLastUsedUser("Runforrest"));
        createItemForGui(1, Material.BOW, "§eTrollbows", 47, inv, "§7Times used: §a" + getAllBowStats(), "§7Click for more information");
        createItemForGui(1, Material.DEAD_BUSH, "§eBorder", 48, inv, "§7Times used: §a" + getStats("Border"), "§7Last used by: §a" + getLastUsedUser("Border"));
        createItemForGui(1, Material.VINE, "§eNoob", 49, inv, "§7Times used: §a" + getStats("Noob"), "§7Last used by: §a" + getLastUsedUser("Noob"));
        createItemForGui(1, Material.PINK_TULIP, "§eSchlong", 50, inv, "§7Times used: §a" + getStats("Schlong"), "§7Last used by: §a" + getLastUsedUser("Schlong"));
        p.openInventory(inv);

    }

    private void createItemForGui(int amount, Material mat, String DisplayName, int slot,
                                  Inventory inventory, String... lore) {

        final ItemStack istack = new ItemStack(mat, amount);
        final ItemMeta istackmeta = istack.getItemMeta();
        istackmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        istackmeta.setDisplayName(DisplayName);
        final ArrayList<String> metalore = new ArrayList<>(Arrays.asList(lore));

        istackmeta.setLore(metalore);
        istack.setItemMeta(istackmeta);
        inventory.setItem(slot, istack);
    }

}
