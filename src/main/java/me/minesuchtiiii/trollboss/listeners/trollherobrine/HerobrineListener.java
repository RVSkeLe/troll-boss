package me.minesuchtiiii.trollboss.listeners.trollherobrine;

import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.HerobrineHelper;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.minesuchtiiii.trollboss.TrollBoss;

public class HerobrineListener implements Listener {

    // TODO: This should happen automatically when a player rejoins, since a new Player object is created
    public void onQuit(PlayerQuitEvent e) {
        final Player p = e.getPlayer();

        if (TrollManager.isActive(p.getUniqueId(), TrollType.HEROBRINE)) {
            HerobrineHelper.unsetHerobrine(p);
        }
    }

}
