package me.minesuchtiiii.trollboss;

import me.minesuchtiiii.trollboss.commands.manager.RegisterCommands;
import me.minesuchtiiii.trollboss.listeners.RegisterEvents;
import me.minesuchtiiii.trollboss.manager.StatsManager;
import me.minesuchtiiii.trollboss.trolls.*;
import me.minesuchtiiii.trollboss.utils.UpdateChecker;
import me.minesuchtiiii.trollboss.utils.Util;
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
    private BuryManager buryManager;
    public static final int HELP_PAGES = 6;
    private static final int METRICS_ID = 15941;
    private static TrollBoss INSTANCE;

    public HashMap<String, Location> skymap = new HashMap<>();
    public HashMap<UUID, Location> abductedCachedLocations = new HashMap<>();
    public HashMap<UUID, UUID> trolling = new HashMap<>();
    public boolean creep;
    public int bowCreepers = 0;
    public int creepers = 0;
    public int trollBuffer = 0;

    public static TrollBoss getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        RegisterEvents.register(this);
        RegisterCommands.register(this);
        Util.saveDefaultConfigFile(this);

        new UpdateChecker(this, 47423).check();

        this.statsManager = new StatsManager(this);
        this.statsManager.checkFile();

        this.garbageManager = new GarbageManager(this);
        this.garbageManager.init();

        this.trampleManager = new TrampleManager();
        this.runforrestManager = new RunforrestManager();
        this.inventoryManager = new InventoryManager();
        this.buryManager = new BuryManager();

        new Metrics(this, METRICS_ID);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getScheduler().cancelTasks(this);

        saveTrolls();
    }

    public boolean canBeTrolled(Player p) {
        if (p.isOp()) {
            return getConfig().getBoolean("Troll-Operators");
        }

        return !p.hasPermission("troll.bypass");
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

    public BuryManager getBuryManager() {
        return buryManager;
    }

}
