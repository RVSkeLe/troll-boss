package me.minesuchtiiii.trollboss.listeners.projectiles;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileBowsHitListener implements Listener {

    private final TrollBoss plugin;

    public ProjectileBowsHitListener(TrollBoss plugin) {

        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onTrollBowHit(ProjectileHitEvent e) {

        final Projectile pro = e.getEntity();

        if (pro instanceof Arrow a && pro.getShooter() instanceof Player p) {

            final Location aloc = a.getLocation();

            if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()
                    && p.getItemInHand().getItemMeta().hasDisplayName()) {

                if ("§eBolt Bow".equalsIgnoreCase(p.getItemInHand().getItemMeta().getDisplayName())) {

                    aloc.getWorld().strikeLightning(aloc);
                    a.remove();
                    plugin.getStats().addBowStats("Bolt");

                }
                if ("§eBoom Bow".equalsIgnoreCase(p.getItemInHand().getItemMeta().getDisplayName())) {

                    aloc.getWorld().createExplosion(aloc.getX(), aloc.getY(), aloc.getZ(), 2.0f, false, false);
                    a.remove();
                    plugin.getStats().addBowStats("Boom");

                }
                if ("§eCreeper Bow".equalsIgnoreCase(p.getItemInHand().getItemMeta().getDisplayName())) {

                    this.plugin.spawnCreeperForBow(aloc, p);
                    a.remove();
                    this.plugin.bowCreepers++;
                    plugin.getStats().addBowStats("Creeper");

                }

            }
        }

    }

}
