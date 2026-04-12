package me.minesuchtiiii.trollboss.trolls;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public final class GarbageManager {

    private final TrollBoss plugin;
    private final File file;

    private FileConfiguration config;

    // Async-safe snapshot (immutable)
    private volatile List<String> messages = List.of();

    public GarbageManager(TrollBoss plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "GarbageMessages.yml");
    }

    public void init() {
        if (!file.exists()) {
            createFile();
        }

        load(); // always load into memory
    }

    public void reload() {
        load();
    }

    public String randomGarbageMessage() {
        List<String> list = this.messages;

        if (list.isEmpty()) {
            return "There's an error in the GarbageMessages file. Please tell the author of the plugin!";
        }

        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private void load() {
        this.config = YamlConfiguration.loadConfiguration(file);

        List<String> loaded = new ArrayList<>();

        ConfigurationSection section = config.getConfigurationSection("Messages");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String msg = section.getString(key);
                if (msg != null) {
                    loaded.add(msg);
                }
            }
        }

        // Atomic replace
        this.messages = List.copyOf(loaded);
    }

    private void createFile() {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();

            this.config = YamlConfiguration.loadConfiguration(file);
            addDefaults();
            save();

            plugin.getLogger().info("Created GarbageMessages file.");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create GarbageMessages file", e);
        }
    }

    private void addDefaults() {
        config.options().header("You can add up to 100 messages if you want");

        config.addDefault("Messages.1", "I am such an idiot, unbelievable!");
        config.addDefault("Messages.2", "I just hate humans");
        config.addDefault("Messages.3", "Can you all please be quiet? I have to meditate!");
        config.addDefault("Messages.4", "My mother should have aborted me..");
        config.addDefault("Messages.5", "Hey girls, want to meet? ;)");
        config.addDefault("Messages.6", "A doctor tells a woman she can no longer touch anything alcoholic. So she gets a divorce.");
        config.addDefault("Messages.7", "What's the difference between men and pigs? Pigs don't turn into men when they drink.");
        config.addDefault("Messages.8", "I have noticed that everyone who is for abortion, has already been born.");
        config.addDefault("Messages.9", "The best mathematical equation I have ever seen: 1 cross + 3 nails = 4 given.");
        config.addDefault("Messages.10", "We can't help everyone, but everyone can help someone.");
        config.addDefault("Messages.11", "Ok, I admit that I have a foot fetish..");
        config.addDefault("Messages.12", "I still play with barbies");
        config.addDefault("Messages.13", "Sometimes I just cry without a reason :(");
        config.addDefault("Messages.14", "Don't tell anyone.. but my aunt is very sexy *_*");
        config.addDefault("Messages.15", "My favorite color is toast");
        config.addDefault("Messages.16", "Don't tell anyone.. but my girlfriend ain't a girl.....");
        config.addDefault("Messages.17", "Who wants to fight me? Come on, don't be shy!!");
        config.addDefault("Messages.18", "I am 40 years old and from somalia");
        config.addDefault("Messages.19", "I am naked at the moment");
        config.addDefault("Messages.20", "Can someone kill me please?");
        config.addDefault("Messages.21", "I love pet wussies");
        config.addDefault("Messages.22", "Call me please I'm desperate");
        config.addDefault("Messages.23", "The future, the present and the past walked into a bar. Things got a little tense..");
        config.addDefault("Messages.24", "My doctors office has two doctors on call at all times. Is that considered a pair a docs?");
        config.addDefault("Messages.25", "Q: What do you call the security outside of a Samsung Store? A: Guardians of the Galaxy.");
        config.addDefault("Messages.26", "This message shouldn't exist...");

        config.options().copyDefaults(true);
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save GarbageMessages file", e);
        }
    }
}
