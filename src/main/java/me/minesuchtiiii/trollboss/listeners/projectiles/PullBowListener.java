package me.minesuchtiiii.trollboss.listeners.projectiles;

import me.minesuchtiiii.trollboss.TrollBoss;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class PullBowListener implements Listener {

    private final TrollBoss plugin;

    public PullBowListener(TrollBoss plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void onPullBowHit(EntityDamageByEntityEvent e) {

        final boolean condition = e.getCause() == DamageCause.PROJECTILE && e.getDamager() instanceof Projectile;
        if (condition) {

            final Projectile a = (Projectile) e.getDamager();

            if (a instanceof Arrow ar && ar.getShooter() instanceof Player p) {
                if (p.getInventory().getItemInMainHand() != null && !(p.getInventory().getItemInMainHand().getType() == Material.AIR)) {
                    if ("§ePull Bow".equalsIgnoreCase(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())) {

                        e.setCancelled(true);

                        final Location pulled = e.getEntity().getLocation();
                        final Location to = p.getLocation();
                        final Vector direction = to.toVector().subtract(pulled.toVector()).normalize();
                        direction.multiply(3.5);
                        direction.setY(0.9);
                        e.getEntity().setVelocity(direction);
                        a.remove();

                        plugin.getStats().addBowStats("Pull");

                    }

                }
            }
        }
    }

}
