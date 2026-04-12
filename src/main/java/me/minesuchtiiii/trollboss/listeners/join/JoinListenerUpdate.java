package me.minesuchtiiii.trollboss.listeners.join;

import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.minesuchtiiii.trollboss.TrollBoss;

public class JoinListenerUpdate implements Listener {
	private final TrollBoss plugin;
	private static boolean worked = false; // what lol

	public JoinListenerUpdate(TrollBoss plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();

		if (!worked) return;
		if (!p.isOp()) return;

		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {

			p.sendMessage(StringManager.PREFIX + "§3An update of §cTroll §3has been downloaded successfully!");
			worked = false;

		}, 40L);
	}
}
