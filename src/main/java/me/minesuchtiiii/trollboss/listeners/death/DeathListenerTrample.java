package me.minesuchtiiii.trollboss.listeners.death;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListenerTrample implements Listener {

    private final TrollBoss plugin;

    public DeathListenerTrample(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeadFromTrample(PlayerDeathEvent e) {
        if (!(TrollManager.deactivate(e.getPlayer().getUniqueId(), TrollType.TRAMPLE))) return;

        e.deathMessage(null);
        plugin.getTrampleManager().removeAllCows();
    }

}
