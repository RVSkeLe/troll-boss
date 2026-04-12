package me.minesuchtiiii.trollboss.utils;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public final class UpdateChecker {

    private static final String API_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    private final TrollBoss plugin;
    private final int resourceId;

    public UpdateChecker(TrollBoss plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + resourceId).openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String version = reader.readLine();
                    if (version != null && !version.isEmpty()) {
                        consumer.accept(version.trim());
                    }
                }

            } catch (IOException e) {
                plugin.getLogger().warning("Update check failed: " + e.getMessage());
            }
        });
    }

    public void check() {
        getVersion(latest -> {
            String current = plugin.getPluginMeta().getVersion();

            if (isSameVersion(current, latest)) {
                plugin.getLogger().info("Plugin is up to date (" + current + ")");
                return;
            }

            plugin.getLogger().warning("New version available!");
            plugin.getLogger().warning("Current: " + current);
            plugin.getLogger().warning("Latest: " + latest);
            plugin.getLogger().warning("Download: https://www.spigotmc.org/resources/" + resourceId + "/");
        });
    }

    private boolean isSameVersion(String a, String b) {
        return normalize(a).equals(normalize(b));
    }

    private String normalize(String ver) {
        return ver.replace("v", "");
    }
}
