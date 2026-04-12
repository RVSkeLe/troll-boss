package me.minesuchtiiii.trollboss.listeners.projectiles;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

                    spawnCreeperForBow(aloc, p);
                    a.remove();
                    this.plugin.bowCreepers++;
                    plugin.getStats().addBowStats("Creeper");

                }

            }
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

}
