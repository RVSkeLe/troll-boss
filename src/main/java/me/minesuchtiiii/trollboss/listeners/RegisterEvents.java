package me.minesuchtiiii.trollboss.listeners;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.listeners.anvil.AnvilBlockLandListener;
import me.minesuchtiiii.trollboss.listeners.anvil.DeathListenerAnvil;
import me.minesuchtiiii.trollboss.listeners.chat.ChatListener;
import me.minesuchtiiii.trollboss.listeners.chat.PlayerChatMuteListener;
import me.minesuchtiiii.trollboss.listeners.death.*;
import me.minesuchtiiii.trollboss.listeners.gui.BowGuiListener;
import me.minesuchtiiii.trollboss.listeners.gui.GuiListener;
import me.minesuchtiiii.trollboss.listeners.gui.StatisticsGuiListener;
import me.minesuchtiiii.trollboss.listeners.gui.TrollInvListener;
import me.minesuchtiiii.trollboss.listeners.interact.InteractBlockShooterListener;
import me.minesuchtiiii.trollboss.listeners.interact.InteractEventAK;
import me.minesuchtiiii.trollboss.listeners.join.JoinListenerUpdate;
import me.minesuchtiiii.trollboss.listeners.misc.DenyPickupListener;
import me.minesuchtiiii.trollboss.listeners.misc.MineListener;
import me.minesuchtiiii.trollboss.listeners.misc.MoveListener;
import me.minesuchtiiii.trollboss.listeners.projectiles.EntityDamageByBlockShooterListener;
import me.minesuchtiiii.trollboss.listeners.projectiles.ProjectileBowsHitListener;
import me.minesuchtiiii.trollboss.listeners.projectiles.PullBowListener;
import me.minesuchtiiii.trollboss.listeners.quit.QuitListener;
import me.minesuchtiiii.trollboss.listeners.quit.QuitListenerRestart;
import me.minesuchtiiii.trollboss.listeners.trollapple.DeathListenerApple;
import me.minesuchtiiii.trollboss.listeners.trollapple.InteractEventApple;
import me.minesuchtiiii.trollboss.listeners.trollcrash.JoinListenerWhenCrashed;
import me.minesuchtiiii.trollboss.listeners.trollcreeper.CreeperDamageByCreeperListener;
import me.minesuchtiiii.trollboss.listeners.trollcreeper.CreeperExplodeListener;
import me.minesuchtiiii.trollboss.listeners.trollherobrine.HerobrineListener;
import me.minesuchtiiii.trollboss.listeners.trollherobrine.HerobrineMoveListener;
import me.minesuchtiiii.trollboss.listeners.trollhurt.DeathListenerBug;
import me.minesuchtiiii.trollboss.listeners.trollteleport.InteractEventTptroll;
import me.minesuchtiiii.trollboss.listeners.trollteleport.ProjectileHitListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class RegisterEvents {
    private static boolean initialized = false;

    private RegisterEvents() {}

    public static void register(TrollBoss plugin) {
        if (!initialized) {
            initialized = true;

            registerEvents(plugin);
        }
    }

    private static void registerEvents(TrollBoss plugin) {
        final PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new QuitListener(plugin), plugin);
        pm.registerEvents(new MoveListener(), plugin);
        pm.registerEvents(new InteractEventAK(), plugin);
        pm.registerEvents(new InteractEventApple(plugin), plugin);
        pm.registerEvents(new DeathListenerApple(), plugin);
        pm.registerEvents(new InteractEventTptroll(), plugin);
        pm.registerEvents(new ProjectileHitListener(), plugin);
        pm.registerEvents(new JoinListenerUpdate(plugin), plugin);
//        pm.registerEvents(new HerobrineListener(), plugin);
        pm.registerEvents(new HerobrineMoveListener(plugin), plugin);
        pm.registerEvents(new QuitListenerRestart(), plugin);
        pm.registerEvents(new DeathListenerBug(), plugin);
        pm.registerEvents(new DeathListenerVoid(), plugin);
        pm.registerEvents(new MineListener(), plugin);
        pm.registerEvents(new InteractBlockShooterListener(plugin), plugin);
        pm.registerEvents(new EntityDamageByBlockShooterListener(), plugin);
        pm.registerEvents(new DeathRemoveSpecialListener(), plugin);
        pm.registerEvents(new GuiListener(plugin), plugin);
        pm.registerEvents(new JoinListenerWhenCrashed(), plugin);
        pm.registerEvents(new DeathListenerTrample(plugin), plugin);
        pm.registerEvents(new PlayerChatMuteListener(), plugin);
        pm.registerEvents(new TrollInvListener(plugin), plugin);
        pm.registerEvents(new CreeperExplodeListener(plugin), plugin);
        pm.registerEvents(new SpartaDeathListener(), plugin);
        pm.registerEvents(new DeathListenerSky(), plugin);
        pm.registerEvents(new BowGuiListener(plugin), plugin);
        pm.registerEvents(new ProjectileBowsHitListener(plugin), plugin);
        pm.registerEvents(new CreeperDamageByCreeperListener(plugin), plugin);
        pm.registerEvents(new DenyPickupListener(), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new DeathListenerAnvil(), plugin);
        pm.registerEvents(new PullBowListener(plugin), plugin);
        pm.registerEvents(new StatisticsGuiListener(plugin), plugin);
        pm.registerEvents(new AnvilBlockLandListener(), plugin);

    }
}
