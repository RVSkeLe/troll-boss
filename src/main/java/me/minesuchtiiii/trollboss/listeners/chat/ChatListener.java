package me.minesuchtiiii.trollboss.listeners.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final TrollBoss plugin;

    public ChatListener(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onGarbageChat(AsyncChatEvent e) {
        if (!TrollManager.isActive(e.getPlayer().getUniqueId(), TrollType.GARBAGE)) return;

        String msg = plugin.getGarbageManager().randomGarbageMessage();
        e.message(MiniMessage.miniMessage().deserialize(msg));
    }
}
