package me.minesuchtiiii.trollboss;

import me.minesuchtiiii.trollboss.commands.manager.RegisterCommands;
import me.minesuchtiiii.trollboss.listeners.RegisterEvents;
import me.minesuchtiiii.trollboss.manager.StatsManager;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.GarbageManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.GuiItem;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.UpdateChecker;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// This thing is a god class and needs to be refactored heavily :/
public class TrollBoss extends JavaPlugin {

    private StatsManager statsManager;
    private GarbageManager garbageManager;
    public static final int HELP_PAGES = 6;
    private static final int METRICS_ID = 15941;
    private static TrollBoss INSTANCE;
    public final Map<UUID, List<Location>> ufoBlockLocations = new HashMap<>();
    private final ArrayList<Entity> cows = new ArrayList<>();
    private final HashMap<String, Integer> fiveSecondTimerTask = new HashMap<>();
    private final HashMap<String, Integer> sixtySecondTimerTask = new HashMap<>();
    private final HashMap<String, Integer> tasks2 = new HashMap<>();
    private final HashMap<String, Location> rfloc = new HashMap<>();
    private final HashMap<UUID, Inventory> invstores = new HashMap<>();
    public ArrayList<Integer> potatoTroll = new ArrayList<>();
    public HashMap<Integer, Location> altblockloc = new HashMap<>();
    public HashMap<Integer, Location> block = new HashMap<>();
    public HashMap<Integer, Location> blockloc = new HashMap<>();
    public HashMap<Integer, Location> blocks = new HashMap<>();
    public HashMap<Integer, Location> oldBlocksLocation = new HashMap<>();
    public HashMap<Integer, Material> blockmat = new HashMap<>();
    public HashMap<Integer, Material> numbersmat = new HashMap<>();
    public HashMap<Integer, Material> zahlmat = new HashMap<>();
    public HashMap<String, Boolean> rf = new HashMap<>();
    public HashMap<String, Double> yloc = new HashMap<>();
    public HashMap<String, Float> pitch = new HashMap<>();
    public HashMap<String, Float> yaw = new HashMap<>();
    public HashMap<String, Integer> rftime = new HashMap<>();
    public HashMap<String, Integer> warnTime = new HashMap<>();
    public HashMap<String, Location> skymap = new HashMap<>();
    public HashMap<String, String> rfmsg = new HashMap<>();
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
    private String version;
    private boolean update;

    public static TrollBoss getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        version = Bukkit.getBukkitVersion();

        RegisterEvents.register(this);
        RegisterCommands.register(this);
        saveDefaultConfigFile();

        update = getConfig().getBoolean("Auto-Update");

        checkForUpdate();
        this.statsManager = new StatsManager(this);
        this.statsManager.checkFile();
        this.garbageManager = new GarbageManager(this);
        this.garbageManager.init();

        new Metrics(this, METRICS_ID);
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getScheduler().cancelTasks(this);

        saveTrolls();
        unsetHerobrines();
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

    private void unsetHerobrines() {
        Bukkit.getOnlinePlayers().forEach(this::unsetHerobrine);
    }

    private void checkForUpdate() {

        new UpdateChecker(this, 47423).getVersion(version -> {
            if (getPluginMeta().getVersion().equals(version)) {
                getLogger().info("You are using the latest version of " + getPluginMeta().getName() + "!");
            } else {
                getLogger().info("There is a new update available!");
                getLogger().info("Your version: v" + getPluginMeta().getVersion());
                getLogger().info("Latest version: " + version);
                getLogger().info("Download it at: https://www.spigotmc.org/resources/trollboss.47423/");
            }
        });

    }

    public StatsManager getStats() {
        return statsManager;
    }

    public GarbageManager getGarbageManager() {
        return garbageManager;
    }

    public void notOnline(Player p, String name) {
        p.sendMessage(StringManager.PREFIX + "§ePlayer §7" + name + " §eis not online!");
    }

    public void setHerobrine(Player p) {

        TrollManager.activate(p.getUniqueId(), TrollType.HEROBRINE);

        Bukkit.getOnlinePlayers().forEach(all -> all.hidePlayer(this, p));

    }

    public void unsetHerobrine(Player p) {

        TrollManager.deactivate(p.getUniqueId(), TrollType.HEROBRINE);

        Bukkit.getOnlinePlayers().forEach(all -> all.showPlayer(this, p));
    }

    public boolean isInt(String s) {

        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();

            return false;
        }
        return true;
    }

    public void restartMessage(int i) {

        Bukkit.broadcastMessage("§7§l[§c§lServer§7§l] §r§6Server will be restarting in §4" + i + " §6seconds!");

    }

    public void kickSchedu(final Player p) {

        Bukkit.getScheduler().runTaskLater(this, () -> {

            List<Player> playersToKick = Bukkit.getOnlinePlayers().stream()
                    .filter(all -> !all.getUniqueId().equals(p.getUniqueId()))
                    .collect(Collectors.toList());

            for (Player all : playersToKick) {
                TrollManager.activate(all.getUniqueId(), TrollType.FAKERESTART);
                all.kickPlayer("§cServer restarting...");
            }

            TrollManager.clear(TrollType.RANDOMTP);

        }, 30L);

    }

    public boolean canBeTrolled(Player p) {

        if (p.isOp()) {
            return getConfig().getBoolean("Troll-Operators");
        }

        return !p.hasPermission("troll.bypass");
    }

    public int createRandom(int lower, int upper) {

        final Random r = new Random();

        return r.nextInt((upper - lower) + 1) + lower;
    }

    public void closeGui(Player p) {

        p.getOpenInventory().close();
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

    }

    public void openChoseWindow(Player p) {

        final Inventory inv = Bukkit.createInventory(null, 9, "§cChoose the special");

        final ItemStack one = GuiItem.createGuiItem(1, Material.EMERALD, "§7#1 §eAk-47", "§7To get the AK-47.");
        final ItemStack two = GuiItem.createGuiItem(2, Material.EMERALD, "§7#2 §eBlock Shooter", "§7To get the Block Shooter.");
        final ItemStack back = GuiItem.createGuiItem(1, Material.IRON_DOOR, "§bReturn to the Troll-Gui", "§7To return to the Troll-Gui.");

        final ItemStack none = new ItemStack(Material.GLASS_PANE, 1);
        final ItemMeta nonemeta = none.getItemMeta();
        nonemeta.setDisplayName("§5");
        none.setItemMeta(nonemeta);

        inv.setItem(0, one);
        inv.setItem(1, two);
        inv.setItem(8, back);

        inv.setItem(2, none);
        inv.setItem(3, none);
        inv.setItem(4, none);
        inv.setItem(5, none);
        inv.setItem(6, none);
        inv.setItem(7, none);

        p.openInventory(inv);

    }

    public boolean isInventoryEmpty(Player p) {

        for (ItemStack item : p.getInventory().getContents()) {

            if (item != null) {

                return false;

            }

        }

        return true;

    }

    public void spawnCow(Player p) {
        final Location ploc = p.getLocation();

        final Silverfish fish = (Silverfish) p.getWorld().spawnEntity(p.getLocation(), EntityType.SILVERFISH);
        fish.addPotionEffects(List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 3),
                new PotionEffect(PotionEffectType.SPEED, 10000000, 3),
                new PotionEffect(Objects.requireNonNull(Registry.EFFECT.get(NamespacedKey.minecraft("strength"))), 10000000, 3)));
        fish.setAggressive(true);
        fish.setTarget(p);

        final Cow cow = (Cow) p.getWorld().spawnEntity(ploc, EntityType.COW);

        final Silverfish fish2 = (Silverfish) p.getWorld().spawnEntity(p.getLocation(), EntityType.SILVERFISH);
        fish2.addPotionEffects(List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 3),
                new PotionEffect(PotionEffectType.SPEED, 10000000, 3),
                new PotionEffect(Objects.requireNonNull(Registry.EFFECT.get(NamespacedKey.minecraft("strength"))), 10000000, 3)));
        fish2.setAggressive(true);
        fish2.setTarget(p);

        fish2.setCustomName(Util.getRandomColor() + "Mad Cow");
        fish2.setCustomNameVisible(false);

        cow.addPassenger(fish2);
        fish.addPassenger(cow);

        cows.add(cow);
        cows.add(fish);
        cows.add(fish2);

    }

    public void removeCows() {

        cows.forEach(Entity::remove);
        cows.clear();

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

    public void getAllTo(Player trgt, Player ignore) {

        final Location loc = trgt.getLocation();

        Bukkit.getServer().getOnlinePlayers().stream().filter(all -> !all.getName().equals(ignore.getName()))
                .forEach(all -> all.teleport(loc));

    }

    public void spawnCreepers(Player player, Location location, int amount) {
        spawnCreeperWithName(player, location, amount, "Angry Creeper");
    }

    private void spawnCreeperWithName(Player player, Location location, int amount, String creeperName) {
        World world = player.getWorld();
        String customName = Util.getRandomColor() + creeperName;

        for (int i = 0; i < amount; i++) {
            Creeper creeper = (Creeper) world.spawnEntity(location, EntityType.CREEPER);
            creeper.setCustomName(customName);
            creeper.setCustomNameVisible(true);
            creeper.setTarget(player);
            creeper.setPowered(true);
            creeper.damage(1.0D, player);
            creeper.setRemoveWhenFarAway(true);
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, 99999999));
        }
    }

    public void spawnCreeperForBow(Location location, Player player) {
        spawnCreeper(location, player, "Angry Creeper");
    }

    private void spawnCreeper(Location location, Player player, String creeperName) {
        World world = player.getWorld();
        Creeper creeper = (Creeper) world.spawnEntity(location, EntityType.CREEPER);

        creeper.setCustomName(Util.getRandomColor() + creeperName);
        creeper.setCustomNameVisible(true);
        creeper.setPowered(true);
        creeper.setRemoveWhenFarAway(true);
        creeper.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, Integer.MAX_VALUE));
    }


    public int getRandom(int lower, int upper) {

        final Random r = new Random();

        return r.nextInt((upper - lower) + 1) + lower;

    }

    public void dropArmor(Player p) {

        final Location loc = p.getLocation().clone();

        for (ItemStack clothes : p.getEquipment().getArmorContents()) {
            if (clothes != null) {

                loc.getWorld().dropItemNaturally(loc, clothes.clone());

            }

        }

        p.getEquipment().clear();
        p.updateInventory();

    }

    public void dropItems(Player p) {

        final Location loc = p.getLocation().clone();
        final Inventory inv = p.getInventory();

        for (ItemStack stuff : inv.getContents()) {

            if (stuff != null) {

                loc.getWorld().dropItemNaturally(loc, stuff.clone());

            }

        }

        p.getInventory().clear();
        p.updateInventory();

    }


    /**
     * Launches a player upward by modifying their velocity.
     *
     * @param p the player to be launched; their vertical velocity will be increased.
     */
    public void launchPlayer(Player p) {

        p.setVelocity(p.getVelocity().setY(3));

    }

    /**
     * Cancels a scheduled task associated with the specified player and removes it from the task map.
     *
     * @param task a {@code HashMap<String, Integer>} mapping player names to their respective task IDs
     * @param p    the {@code Player} whose task is to be canceled
     */
    private void cancelTask(HashMap<String, Integer> task, Player p) {

        if (!task.containsKey(p.getName())) {
            return;
        }
        final int tid = task.get(p.getName());
        Bukkit.getScheduler().cancelTask(tid);
        task.remove(p.getName());

    }

    private void editRFTime(Player p) {

        if (this.rftime.containsKey(p.getName())) {

            this.rftime.replace(p.getName(), this.rftime.get(p.getName()) - 1);

        }

    }

    private int getRFTime(Player p) {

        return this.rftime.getOrDefault(p.getName(), 0);

    }

    private void editWarnTime(Player p) {

        if (this.warnTime.containsKey(p.getName())) {

            this.warnTime.replace(p.getName(), this.warnTime.get(p.getName()) - 1);

        }

    }

    private int getWarnTime(Player p) {

        return this.warnTime.getOrDefault(p.getName(), 0);
    }

    public void start5SecRunTimer(Player player) {

        this.warnTime.put(player.getName(), 11);
        sendInitialMessages(player);

        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> handleWarningTimer(player), 0L, 20L);
        fiveSecondTimerTask.put(player.getName(), taskId);
    }

    private void sendInitialMessages(Player player) {
        player.sendMessage(String.format(StringManager.ENGLISH_INITIAL_MSG, getRFTime(player)));
        player.sendMessage(StringManager.ENGLISH_DEATH_MSG);

    }

    private void handleWarningTimer(Player player) {
        editWarnTime(player);
        int warnTime = getWarnTime(player);

        if (warnTime > 0) {
            player.sendMessage(String.format(StringManager.ENGLISH_WARNING_MSG, warnTime));
        } else {
            player.sendMessage(StringManager.ENGLISH_WARNING_FINAL);

            this.warnTime.remove(player.getName());
            this.rf.put(player.getName(), true);
            start60SekRunTimer(player);
            cancelTask(fiveSecondTimerTask, player);
        }
    }


    private void check4movement(Player p) {

        if (!rf.containsKey(p.getName())) {
            return;
        }
        final int checki = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

            if (TrollBoss.this.rfloc.containsKey(p.getName())) {
                if (TrollBoss.this.rfloc.get(p.getName()).equals(p.getLocation())) {

                    p.sendMessage("§7[§4INFO§7] §cAll you had to do was to damn move CJ!");

                    TrollBoss.this.rf.replace(p.getName(), false);
                    TrollBoss.this.cancelTask(tasks2, p);

                    final String trollername = TrollBoss.this.rfmsg.get(p.getName());
                    final Player troller = Bukkit.getPlayer(trollername);

                    if (troller != null) {

                        troller.sendMessage(StringManager.PREFIX + "§eSucessfully trolled §7" + p.getName() + "§e!");
                        TrollBoss.this.rfmsg.remove(p.getName());

                    }

                } else {

                    rf.replace(p.getName(), true);

                }

            }
        }, 40L, 20L);
        tasks2.put(p.getName(), checki);

    }

    private void start60SekRunTimer(Player p) {

        rfloc.put(p.getName(), p.getLocation());
        check4movement(p);

        final int m2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {

            if (getRFTime(p) > 0 && rf.get(p.getName())) {

                editRFTime(p);
                rfloc.put(p.getName(), p.getLocation());

                p.setLevel(getRFTime(p));

            } else if (getRFTime(p) > 0 && !rf.get(p.getName())) {

                rftime.remove(p.getName());
                rf.remove(p.getName());
                rfloc.remove(p.getName());
                p.setLevel(0);
                p.setHealth(0D);
                cancelTask(sixtySecondTimerTask, p);

            } else if (getRFTime(p) == 0 && rf.get(p.getName())) {

                rf.remove(p.getName());
                rftime.remove(p.getName());
                rfloc.remove(p.getName());
                p.setLevel(0);
                cancelTask(sixtySecondTimerTask, p);

                final String targetname = rfmsg.get(p.getName());
                final Player troller = Bukkit.getPlayer(targetname);

                p.sendMessage("§7[§4INFO§7] §aYou survived!");

                if (troller != null) {

                    troller.sendMessage(StringManager.PREFIX + "§ePlayer §7" + p.getName() + " §esurvived the troll!");
                    rfmsg.remove(p.getName());

                }

            }

        }, 1L, 20L);

        sixtySecondTimerTask.put(p.getName(), m2);

    }

    public void teleportToBorder(Player p) {

        final Location ploc = p.getLocation();

        final int x = 29999983;
        final int z = 29999983;
        final int y = ploc.getWorld().getHighestBlockYAt(x, z);

        final Location bloc = new Location(p.getWorld(), x, y, z, p.getLocation().getPitch(), p.getLocation().getYaw());
        p.teleport(bloc);

    }

    public void storeInv(Player p) {

        Inventory clone = Bukkit.createInventory(p, InventoryType.PLAYER);
        clone.setContents(p.getInventory().getContents());
        invstores.put(p.getUniqueId(), clone);

    }

    public void restoreInv(Player p, int sec) {

        if (invstores.containsKey(p.getUniqueId())) {

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {

                p.getInventory().clear();
                p.getInventory().setContents(invstores.get(p.getUniqueId()).getContents());
                invstores.remove(p.getUniqueId());
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                p.sendMessage("§eJust a prank, here's your old inventory!");

            }, sec * 20L);

        }
    }

    public String getVersion() {

        return this.version;
    }

}
