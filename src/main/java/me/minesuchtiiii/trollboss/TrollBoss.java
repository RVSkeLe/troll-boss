package me.minesuchtiiii.trollboss;

import me.minesuchtiiii.trollboss.commands.manager.RegisterCommands;
import me.minesuchtiiii.trollboss.listeners.RegisterEvents;
import me.minesuchtiiii.trollboss.manager.TrollManager;
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

    public static final int HELP_PAGES = 6;
    private static final int METRICS_ID = 15941;
    private static TrollBoss INSTANCE;
    public final Map<UUID, List<Location>> ufoBlockLocations = new HashMap<>();
    private final ArrayList<Entity> cows = new ArrayList<>();
    private final File gbmsgs = new File(getDataFolder() + "/GarbageMessages.yml");
    private final File stats = new File(getDataFolder() + "/stats.yml");
    private final FileConfiguration gbmsgscfg = YamlConfiguration.loadConfiguration(gbmsgs);
    private final FileConfiguration statscfg = YamlConfiguration.loadConfiguration(stats);
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
        check4File();
        check4otherFile();

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

    public void openBowWindow(Player p) {

        final Inventory inv = Bukkit.createInventory(null, 9, "§cChoose a Troll-Bow");

        final ItemStack b1 = new ItemStack(Material.BOW);
        final ItemMeta b1meta = b1.getItemMeta();
        b1meta.setDisplayName("§eBolt Bow");
        b1meta.addEnchant(Enchantment.INFINITY, 1, true);
        b1meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        final ArrayList<String> b1metalore = new ArrayList<>();
        b1metalore.add("§7Strikes a lightning at the arrows location.");
        b1meta.setLore(b1metalore);
        b1.setItemMeta(b1meta);

        final ItemStack b2 = new ItemStack(Material.BOW);
        final ItemMeta b2meta = b2.getItemMeta();
        b2meta.setDisplayName("§eBoom Bow");
        b2meta.addEnchant(Enchantment.INFINITY, 1, true);
        b2meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        final ArrayList<String> b2metalore = new ArrayList<>();
        b2metalore.add("§7Creates an explosion at the arrows location.");
        b2meta.setLore(b2metalore);
        b2.setItemMeta(b2meta);

        final ItemStack b3 = new ItemStack(Material.BOW);
        final ItemMeta b3meta = b3.getItemMeta();
        b3meta.setDisplayName("§eCreeper Bow");
        b3meta.addEnchant(Enchantment.INFINITY, 1, true);
        b3meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        final ArrayList<String> b3metalore = new ArrayList<>();
        b3metalore.add("§7Spawns a creeper at the arrows location.");
        b3meta.setLore(b3metalore);
        b3.setItemMeta(b3meta);

        final ItemStack b4 = new ItemStack(Material.BOW);
        final ItemMeta b4meta = b4.getItemMeta();
        b4meta.setDisplayName("§ePull Bow");
        b4meta.addEnchant(Enchantment.INFINITY, 1, true);
        b4meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        final ArrayList<String> b4metalore = new ArrayList<>();
        b4metalore.add("§7Pulls the hit entity into your direction.");
        b4meta.setLore(b4metalore);
        b4.setItemMeta(b4meta);

        final ItemStack b5 = new ItemStack(Material.BOW);
        final ItemMeta b5meta = b5.getItemMeta();
        b5meta.setDisplayName("§eGet all bows at once");
        b5meta.addEnchant(Enchantment.INFINITY, 1, true);
        b5meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        final ArrayList<String> b5metalore = new ArrayList<>();
        b5metalore.add("§7Adds every Troll-Bow to your inventory.");
        b5meta.setLore(b5metalore);
        b5.setItemMeta(b5meta);

        final ItemStack back = new ItemStack(Material.IRON_DOOR);
        final ItemMeta backmeta = back.getItemMeta();
        backmeta.setDisplayName("§bReturn to the Troll-Gui");
        final ArrayList<String> backmetalore = new ArrayList<>();
        backmetalore.add("§7To return to the Troll-Gui.");
        backmeta.setLore(backmetalore);
        back.setItemMeta(backmeta);

        final ItemStack none = new ItemStack(Material.GLASS_PANE, 1);
        final ItemMeta nonemeta = none.getItemMeta();
        nonemeta.setDisplayName("§5");
        none.setItemMeta(nonemeta);

        inv.setItem(0, b1);
        inv.setItem(1, b2);
        inv.setItem(2, b3);
        inv.setItem(3, b4);
        inv.setItem(7, b5);
        inv.setItem(8, back);

        inv.setItem(4, none);
        inv.setItem(5, none);
        inv.setItem(6, none);

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

    @SuppressWarnings("deprecation")
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

    private void saveStats() {

        try {

            statscfg.save(stats);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void savegbmsgs() {

        try {

            gbmsgscfg.save(gbmsgs);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void check4File() {

        if (!(stats.exists())) {

            try {

                stats.createNewFile();
                addStatsDefaults();
                getLogger().info("Created stats file.");
                saveStats();
                getLogger().info("Saved stats file.");

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

            saveStats();

        }

    }

    private void check4otherFile() {

        if (!(gbmsgs.exists())) {

            try {

                gbmsgs.createNewFile();
                addGarbageDefaults();
                getLogger().info("Created GarbageMessages file.");
                savegbmsgs();
                getLogger().info("Saved GarbageMessages file.");

            } catch (IOException e) {

                e.printStackTrace();

            }

        } else {

            savegbmsgs();

        }

    }

    @SuppressWarnings("deprecation")
    private void addGarbageDefaults() {

        gbmsgscfg.options().header("You can add up to 100 messages if you want");

        gbmsgscfg.addDefault("Messages.1", "I am such an idiot, unbelievable!");
        gbmsgscfg.addDefault("Messages.2", "I just hate humans");
        gbmsgscfg.addDefault("Messages.3", "Can you all please be quiet? I have to meditate!");
        gbmsgscfg.addDefault("Messages.4", "My mother should have aborted me..");
        gbmsgscfg.addDefault("Messages.5", "Hey girls, want to meet? ;)");
        gbmsgscfg.addDefault("Messages.6", "A doctor tells a woman she can no longer touch anything alcoholic. So she gets a divorce.");
        gbmsgscfg.addDefault("Messages.7", "What's the difference between men and pigs? Pigs don't turn into men when they drink.");
        gbmsgscfg.addDefault("Messages.8", "I have noticed that everyone who is for abortion, has already been born.");
        gbmsgscfg.addDefault("Messages.9", "The best mathematical equation I have ever seen: 1 cross + 3 nails = 4 given.");
        gbmsgscfg.addDefault("Messages.10", "We can't help everyone, but everyone can help someone.");
        gbmsgscfg.addDefault("Messages.11", "Ok, I admit that I have a foot fetish..");
        gbmsgscfg.addDefault("Messages.12", "I still play with barbies");
        gbmsgscfg.addDefault("Messages.13", "Sometimes I just cry without a reason :(");
        gbmsgscfg.addDefault("Messages.14", "Don't tell anyone.. but my aunt is very sexy *_*");
        gbmsgscfg.addDefault("Messages.15", "My favorite color is toast");
        gbmsgscfg.addDefault("Messages.16", "Don't tell anyone.. but my girlfriend ain't a girl.....");
        gbmsgscfg.addDefault("Messages.17", "Who wants to fight me? Come on, don't be shy!!");
        gbmsgscfg.addDefault("Messages.18", "I am 40 years old and from somalia");
        gbmsgscfg.addDefault("Messages.19", "I am naked at the moment");
        gbmsgscfg.addDefault("Messages.20", "Can someone kill me please?");
        gbmsgscfg.addDefault("Messages.21", "I love pet wussies");
        gbmsgscfg.addDefault("Messages.22", "Call me please I'm desperate");
        gbmsgscfg.addDefault("Messages.23", "The future, the present and the past walked into a bar. Things got a little tense..");
        gbmsgscfg.addDefault("Messages.24", "My doctors office has two doctors on call at all times. Is that considered a pair a docs?");
        gbmsgscfg.addDefault("Messages.25", "Q: What do you call the security outside of a Samsung Store? A: Guardians of the Galaxy.");
        gbmsgscfg.addDefault("Messages.26", "This message shouldn't exist...");

        gbmsgscfg.options().copyDefaults(true);

    }

    private int getAmountOfGarbageMessages() {

        int amount = 0;

        for (int i = 0; i < 100; i++) {

            if (this.gbmsgscfg.get("Messages." + i) != null) {

                amount++;

            } else {

                amount += 0;

            }

        }

        return amount;

    }

    private void addStatsDefaults() {

        statscfg.addDefault("Troll.Burn", 0);
        statscfg.addDefault("Troll.Freeze", 0);
        statscfg.addDefault("Troll.Bolt", 0);
        statscfg.addDefault("Troll.Special", 0);
        statscfg.addDefault("Troll.Boom", 0);
        statscfg.addDefault("Troll.Push", 0);
        statscfg.addDefault("Troll.Fakeop", 0);
        statscfg.addDefault("Troll.Fakedeop", 0);
        statscfg.addDefault("Troll.Launch", 0);
        statscfg.addDefault("Troll.Spam", 0);
        statscfg.addDefault("Troll.Gokill", 0);
        statscfg.addDefault("Troll.Switch", 0);
        statscfg.addDefault("Troll.Trollkick", 0);
        statscfg.addDefault("Troll.Badapple", 0);
        statscfg.addDefault("Troll.Potatotroll", 0);
        statscfg.addDefault("Troll.Trap", 0);
        statscfg.addDefault("Troll.Teleporttroll", 0);
        statscfg.addDefault("Troll.Infect", 0);
        statscfg.addDefault("Troll.Herobrine", 0);
        statscfg.addDefault("Troll.Fakerestart", 0);
        statscfg.addDefault("Troll.Turn", 0);
        statscfg.addDefault("Troll.Starve", 0);
        statscfg.addDefault("Troll.Hurt", 0);
        statscfg.addDefault("Troll.Void", 0);
        statscfg.addDefault("Troll.Pumpkinhead", 0);
        statscfg.addDefault("Troll.Bury", 0);
        statscfg.addDefault("Troll.Nomine", 0);
        statscfg.addDefault("Troll.Randomteleport", 0);
        statscfg.addDefault("Troll.Crash", 0);
        statscfg.addDefault("Troll.Freefall", 0);
        statscfg.addDefault("Troll.Webtrap", 0);
        statscfg.addDefault("Troll.Spank", 0);
        statscfg.addDefault("Troll.Trample", 0);
        statscfg.addDefault("Troll.Stfu", 0);
        statscfg.addDefault("Troll.Popup", 0);
        statscfg.addDefault("Troll.Sky", 0);
        statscfg.addDefault("Troll.Abduct", 0);
        statscfg.addDefault("Troll.Popular", 0);
        statscfg.addDefault("Troll.Creeper", 0);
        statscfg.addDefault("Troll.Sparta", 0);
        statscfg.addDefault("Troll.Trollbows", 0);
        statscfg.addDefault("Troll.Drug", 0);
        statscfg.addDefault("Troll.Squidrain", 0);
        statscfg.addDefault("Troll.Dropinv", 0);
        statscfg.addDefault("Troll.Garbage", 0);
        statscfg.addDefault("Troll.Anvil", 0);
        statscfg.addDefault("Troll.Invtext", 0);
        statscfg.addDefault("Troll.Runforrest", 0);
        statscfg.addDefault("Troll.Border", 0);
        statscfg.addDefault("Troll.Noob", 0);
        statscfg.addDefault("Troll.Randomtroll", 0);
        statscfg.addDefault("Troll.Schlong", 0);
        statscfg.addDefault("Troll.Denymove", 0);

        statscfg.addDefault("Troll.Bows.Bolt", 0);
        statscfg.addDefault("Troll.Bows.Boom", 0);
        statscfg.addDefault("Troll.Bows.Creeper", 0);
        statscfg.addDefault("Troll.Bows.Pull", 0);


        statscfg.addDefault("LastUsed.Burn", "Nobody");
        statscfg.addDefault("LastUsed.Freeze", "Nobody");
        statscfg.addDefault("LastUsed.Bolt", "Nobody");
        statscfg.addDefault("LastUsed.Special", "Nobody");
        statscfg.addDefault("LastUsed.Boom", "Nobody");
        statscfg.addDefault("LastUsed.Push", "Nobody");
        statscfg.addDefault("LastUsed.Fakeop", "Nobody");
        statscfg.addDefault("LastUsed.Fakedeop", "Nobody");
        statscfg.addDefault("LastUsed.Launch", "Nobody");
        statscfg.addDefault("LastUsed.Spam", "Nobody");
        statscfg.addDefault("LastUsed.Gokill", "Nobody");
        statscfg.addDefault("LastUsed.Switch", "Nobody");
        statscfg.addDefault("LastUsed.Trollkick", "Nobody");
        statscfg.addDefault("LastUsed.Badapple", "Nobody");
        statscfg.addDefault("LastUsed.Potatotroll", "Nobody");
        statscfg.addDefault("LastUsed.Trap", "Nobody");
        statscfg.addDefault("LastUsed.Tptroll", "Nobody");
        statscfg.addDefault("LastUsed.Infect", "Nobody");
        statscfg.addDefault("LastUsed.Herobrine", "Nobody");
        statscfg.addDefault("LastUsed.Fakerestart", "Nobody");
        statscfg.addDefault("LastUsed.Turn", "Nobody");
        statscfg.addDefault("LastUsed.Starve", "Nobody");
        statscfg.addDefault("LastUsed.Teleporttroll", "Nobody");
        statscfg.addDefault("LastUsed.Hurt", "Nobody");
        statscfg.addDefault("LastUsed.Void", "Nobody");
        statscfg.addDefault("LastUsed.Pumpkinhead", "Nobody");
        statscfg.addDefault("LastUsed.Bury", "Nobody");
        statscfg.addDefault("LastUsed.Nomine", "Nobody");
        statscfg.addDefault("LastUsed.Randomteleport", "Nobody");
        statscfg.addDefault("LastUsed.Crash", "Nobody");
        statscfg.addDefault("LastUsed.Freefall", "Nobody");
        statscfg.addDefault("LastUsed.Webtrap", "Nobody");
        statscfg.addDefault("LastUsed.Spank", "Nobody");
        statscfg.addDefault("LastUsed.Trample", "Nobody");
        statscfg.addDefault("LastUsed.Stfu", "Nobody");
        statscfg.addDefault("LastUsed.Popup", "Nobody");
        statscfg.addDefault("LastUsed.Sky", "Nobody");
        statscfg.addDefault("LastUsed.Abduct", "Nobody");
        statscfg.addDefault("LastUsed.Popular", "Nobody");
        statscfg.addDefault("LastUsed.Creeper", "Nobody");
        statscfg.addDefault("LastUsed.Sparta", "Nobody");
        statscfg.addDefault("LastUsed.Trollbows", "Nobody");
        statscfg.addDefault("LastUsed.Drug", "Nobody");
        statscfg.addDefault("LastUsed.Squidrain", "Nobody");
        statscfg.addDefault("LastUsed.Dropinv", "Nobody");
        statscfg.addDefault("LastUsed.Garbage", "Nobody");
        statscfg.addDefault("LastUsed.Anvil", "Nobody");
        statscfg.addDefault("LastUsed.Invtext", "Nobody");
        statscfg.addDefault("LastUsed.Runforrest", "Nobody");
        statscfg.addDefault("LastUsed.Border", "Nobody");
        statscfg.addDefault("LastUsed.Noob", "Nobody");
        statscfg.addDefault("LastUsed.Randomtroll", "Nobody");
        statscfg.addDefault("LastUsed.Schlong", "Nobody");
        statscfg.addDefault("LastUsed.Denymove", "Nobody");

        statscfg.options().copyDefaults(true);

    }

    public void updateLastUsedUser(Player p, String troll) {

        statscfg.set("LastUsed." + troll, p.getName());
        saveStats();

    }

    private String getLastUsedUser(String troll) {
        return statscfg.getString("LastUsed." + troll);
    }

    private int getStats(String cmd) {

        return statscfg.getInt("Troll." + cmd);

    }

    public void addStats(String cmd, Player lastused) {

        statscfg.set("Troll." + cmd, getStats(cmd) + 1);
        saveStats();
        updateLastUsedUser(lastused, cmd);

    }

    private int getBowStats(String bow) {

        return statscfg.getInt("Troll.Bows." + bow);

    }

    public int getAllBowStats() {

        final int bow1 = statscfg.getInt("Troll.Bows.Bolt");
        final int bow2 = statscfg.getInt("Troll.Bows.Boom");
        final int bow3 = statscfg.getInt("Troll.Bows.Creeper");
        final int bow4 = statscfg.getInt("Troll.Bows.Pull");

        return bow1 + bow2 + bow3 + bow4;

    }

    public void addBowStats(String bow) {

        statscfg.set("Troll.Bows." + bow, getBowStats(bow) + 1);
        saveStats();

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
     * Generates a random garbage message by selecting an index from the available garbage messages
     * configuration. If no message is found for the selected index, a default error message is returned.
     *
     * @return a random garbage message as a {@code String}, or a default error message if none is found
     */
    public String randomGarbageMessage() {
        Random randomGenerator = new Random();
        int randomMessageIndex = randomGenerator.nextInt(getAmountOfGarbageMessages()) + 1;

        return getGarbageMessage(randomMessageIndex)
                .orElse("There's an error in the GarbageMessages file. Please tell the author of the plugin!");
    }


    /**
     * Retrieves a garbage message from a configuration file based on the provided index.
     *
     * @param index the index of the garbage message to retrieve from the configuration
     * @return an {@code Optional<String>} containing the garbage message if it exists,
     * or an empty {@code Optional} if no message is found for the specified index
     */
    private Optional<String> getGarbageMessage(int index) {
        String garbageMessage = this.gbmsgscfg.getString("Messages." + index);
        return Optional.ofNullable(garbageMessage);
    }


    /**
     * Launches a player upward by modifying their velocity.
     *
     * @param p the player to be launched; their vertical velocity will be increased.
     */
    public void launchPlayer(Player p) {

        p.setVelocity(p.getVelocity().setY(3));

    }

    private void addInventoryTextItem(Player p, int inventorySlot, String text, int amount) {
        final ItemStack dirt1 = new ItemStack(Material.DIRT, amount);
        final ItemMeta dirt1meta = dirt1.getItemMeta();
        dirt1meta.setDisplayName(Util.getRandomColor() + text);
        dirt1.setItemMeta(dirt1meta);

        p.getInventory().setItem(inventorySlot, dirt1);

    }

    public void addAllInventoryTextItems(Player p) {

        p.getInventory().clear();

        // Line 1
        addInventoryTextItem(p, 9, "Hello,", 1);
        addInventoryTextItem(p, 10, "I", 2);
        addInventoryTextItem(p, 11, "hope", 3);
        addInventoryTextItem(p, 12, "you", 4);
        addInventoryTextItem(p, 13, "can", 5);
        addInventoryTextItem(p, 14, "need", 6);
        addInventoryTextItem(p, 15, "some", 7);
        addInventoryTextItem(p, 16, "fresh", 8);
        addInventoryTextItem(p, 17, "dirt..", 9);
        // Line 2
        addInventoryTextItem(p, 18, "if", 10);
        addInventoryTextItem(p, 19, "not...", 11);
        addInventoryTextItem(p, 20, "well", 12);
        addInventoryTextItem(p, 21, "it's", 13);
        addInventoryTextItem(p, 22, "too", 14);
        addInventoryTextItem(p, 23, "late", 15);
        addInventoryTextItem(p, 24, "anyway..", 16);
        addInventoryTextItem(p, 25, "old", 17);
        addInventoryTextItem(p, 26, "items", 18);
        // Line 3
        addInventoryTextItem(p, 27, "are", 19);
        addInventoryTextItem(p, 28, "gone", 20);
        addInventoryTextItem(p, 29, "for", 21);
        addInventoryTextItem(p, 30, "ever..", 22);
        addInventoryTextItem(p, 31, "hehe", 23);
        addInventoryTextItem(p, 32, "see", 24);
        addInventoryTextItem(p, 33, "you..", 25);
        addInventoryTextItem(p, 34, "have", 26);
        addInventoryTextItem(p, 35, "fun!", 27);

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

    public void openBowStatisticsInv(Player p) {

        final Inventory inv = Bukkit.createInventory(null, 9, "§cTrollbow Statistics");

        createItemForGui(1, Material.EMERALD, "§bClose the gui", 8, inv, "§7Closes the Statistics-Gui");
        createItemForGui(1, Material.BOW, "§eBolt Bow", 0, inv, "§7Times used: §a" + getStats("Bows.Bolt"));
        createItemForGui(1, Material.BOW, "§eBoom Bow", 1, inv, "§7Times used: §a" + getStats("Bows.Boom"));
        createItemForGui(1, Material.BOW, "§eCreeper Bow", 2, inv, "§7Times used: §a" + getStats("Bows.Creeper"));
        createItemForGui(1, Material.BOW, "§ePull Bow", 3, inv, "§7Times used: §a" + getStats("Bows.Pull"));

        p.openInventory(inv);

    }

    public void openStatisticsInv(Player p) {

        final Inventory inv = Bukkit.createInventory(null, 54, "§cTroll Statistics");

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

    public String getVersion() {

        return this.version;
    }

}
