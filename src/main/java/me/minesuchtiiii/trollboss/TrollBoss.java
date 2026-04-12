package me.minesuchtiiii.trollboss;

import me.minesuchtiiii.trollboss.commands.manager.RegisterCommands;
import me.minesuchtiiii.trollboss.listeners.RegisterEvents;
import me.minesuchtiiii.trollboss.manager.StatsManager;
import me.minesuchtiiii.trollboss.trolls.GarbageManager;
import me.minesuchtiiii.trollboss.trolls.InventoryManager;
import me.minesuchtiiii.trollboss.trolls.RunforrestManager;
import me.minesuchtiiii.trollboss.trolls.TrampleManager;
import me.minesuchtiiii.trollboss.utils.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

// This thing is a god class and needs to be refactored heavily :/
public class TrollBoss extends JavaPlugin {

    private StatsManager statsManager;
    private GarbageManager garbageManager;
    private TrampleManager trampleManager;
    private RunforrestManager runforrestManager;
    private InventoryManager inventoryManager;
    public static final int HELP_PAGES = 6;
    private static final int METRICS_ID = 15941;
    private static TrollBoss INSTANCE;
    public final Map<UUID, List<Location>> ufoBlockLocations = new HashMap<>();
    public ArrayList<Integer> potatoTroll = new ArrayList<>();
    public HashMap<Integer, Location> altblockloc = new HashMap<>();
    public HashMap<Integer, Location> block = new HashMap<>();
    public HashMap<Integer, Location> blockloc = new HashMap<>();
    public HashMap<Integer, Location> blocks = new HashMap<>();
    public HashMap<Integer, Location> oldBlocksLocation = new HashMap<>();
    public HashMap<Integer, Material> blockmat = new HashMap<>();
    public HashMap<Integer, Material> numbersmat = new HashMap<>();
    public HashMap<Integer, Material> zahlmat = new HashMap<>();
    public HashMap<String, Double> yloc = new HashMap<>();
    public HashMap<String, Float> pitch = new HashMap<>();
    public HashMap<String, Float> yaw = new HashMap<>();
    public HashMap<String, Location> skymap = new HashMap<>();
    public HashMap<UUID, Integer> spartaArrows = new HashMap<>();
    public HashMap<UUID, Location> abductedCachedLocations = new HashMap<>();
    public HashMap<UUID, UUID> trolling = new HashMap<>();
    public boolean c;
    public boolean creep;
    public boolean isRestarting = false;
    public boolean worked = false;
    public int bowCreepers = 0;
    public int creepers = 0;
    public int lvl = 0;
    public int spartaTask;
    public int time = 14;
    public int trollBuffer = 0;

    public static TrollBoss getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        RegisterEvents.register(this);
        RegisterCommands.register(this);
        saveDefaultConfigFile();

        new UpdateChecker(this, 47423).check();

        this.statsManager = new StatsManager(this);
        this.statsManager.checkFile();

        this.garbageManager = new GarbageManager(this);
        this.garbageManager.init();

        this.trampleManager = new TrampleManager();
        this.runforrestManager = new RunforrestManager();
        this.inventoryManager = new InventoryManager();

        new Metrics(this, METRICS_ID);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getScheduler().cancelTasks(this);

        saveTrolls();
    }

    private void saveDefaultConfigFile() {

        getConfig().addDefault("Auto-Update", true);
        getConfig().addDefault("Troll-Operators", true);
        getConfig().addDefault("Trolls", 0);

        //TODO: This needs to be addressed since MC is about to switch to Calendar Versioning (e.g. 26.1.1)
        int version = Integer.parseInt(getServer().getMinecraftVersion().split("\\.")[1]);
        int subVersion = Integer.parseInt(getServer().getMinecraftVersion().split("\\.")[2]);

        if (version >= 19 || version == 18 && subVersion >= 1) {

            getConfig().options().setHeader(List.of("Some options you can edit"));

            getConfig().options().parseComments(true);

            getConfig().setComments("Trolls", List.of("Used for the statistics, should not be touched"));
            getConfig().setComments("Troll-Operators", List.of("Define if operators can be trolled or not", "default: true"));
            getConfig().setComments("Auto-Update", List.of("Define if the plugin should automatically update when a new version is available", "default: true"));

        } else if (getServer().getMinecraftVersion().contains("1.18-") || version <= 17 && version >= 14) {

            getConfig().options().setHeader(List.of("Some options you can edit"));

        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
        saveConfig();

    }

    public StatsManager getStats() {
        return statsManager;
    }

    public GarbageManager getGarbageManager() {
        return garbageManager;
    }

    public TrampleManager getTrampleManager() {
        return trampleManager;
    }

    public RunforrestManager getRunforrestManager() {
        return runforrestManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public boolean canBeTrolled(Player p) {

        if (p.isOp()) {
            return getConfig().getBoolean("Troll-Operators");
        }

        return !p.hasPermission("troll.bypass");
    }

    public void closeGui(Player p) {
        p.getOpenInventory().close();
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    public int getTrolls() {

        if (getConfig().getString("Trolls") != null) {

            return getConfig().getInt("Trolls");

        } else {
            return 0;
        }
    }

    public void addTroll() {
        trollBuffer++;
    }

    private int total() {
        return trollBuffer + getTrolls();
    }

    private void saveTrolls() {

        if (getConfig().getString("Trolls") != null) {

            getConfig().set("Trolls", total());
            this.saveConfig();
            this.reloadConfig();
            trollBuffer = 0;

        }

    }

}
