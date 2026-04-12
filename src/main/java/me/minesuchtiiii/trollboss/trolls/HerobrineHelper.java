package me.minesuchtiiii.trollboss.trolls;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class HerobrineHelper {

    public static void setHerobrine(Player p) {
        TrollManager.activate(p.getUniqueId(), TrollType.HEROBRINE);

        Bukkit.getOnlinePlayers().forEach(all ->
                all.hidePlayer(TrollBoss.getInstance(), p)
        );
    }

    public static void unsetHerobrine(Player p) {
        TrollManager.deactivate(p.getUniqueId(), TrollType.HEROBRINE);

        Bukkit.getOnlinePlayers().forEach(all ->
                all.showPlayer(TrollBoss.getInstance(), p)
        );
    }
}
