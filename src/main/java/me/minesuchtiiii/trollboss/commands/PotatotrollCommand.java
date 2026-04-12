package me.minesuchtiiii.trollboss.commands;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.utils.StringManager;
import me.minesuchtiiii.trollboss.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PotatotrollCommand implements CommandExecutor {
    private final TrollBoss plugin;

    public ArrayList<Integer> potatoTroll = new ArrayList<>();

    public PotatotrollCommand(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(StringManager.NOPLAYER);
            return true;
        }

        if (!player.hasPermission("troll.potatotroll")) {
            player.sendMessage(StringManager.NOPERM);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(StringManager.PREFIX + "§eUse §7/potatotroll [player]");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringManager.MUCHARGS);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Util.notOnline(player, args[0]);
            return true;
        }

        if (!plugin.canBeTrolled(target)) {
            player.sendMessage(StringManager.BYPASS);
            return true;
        }

        if (Util.isInventoryEmpty(target)) {
            player.sendMessage(StringManager.PREFIX + "§cCan't troll player §4" + target.getName() + " §cbecause their inventory is empty!");
            return true;
        }

        trollPlayerWithPotatoes(player, target);
        return true;
    }

    private void trollPlayerWithPotatoes(Player player, Player target) {
        plugin.getInventoryManager().storeInv(target);
        plugin.getInventoryManager().restoreInv(target, 20);

        replaceInventoryWithPotatoes(target);

        sendReplacedInventoryMessage(player, target, this.potatoTroll.size());

        plugin.addTroll();
        plugin.getStats().addStats("Potatotroll", player);
        this.potatoTroll.clear();
    }

    private void replaceInventoryWithPotatoes(Player target) {
        for (int i = 0; i < 36; i++) {
            if (target.getInventory().getItem(i) != null) {
                this.potatoTroll.add(i);
            }
        }

        this.potatoTroll.stream().mapToInt(Integer::intValue).forEach(slot -> {
            ItemStack potato = new ItemStack(Material.BAKED_POTATO);
            ItemMeta potatoMeta = potato.getItemMeta();

            potatoMeta.setDisplayName(Util.getRandomColor() + "Potato!");
            potato.setItemMeta(potatoMeta);

            target.getInventory().setItem(slot, potato);
        });
    }

    private void sendReplacedInventoryMessage(Player player, Player target, int itemCount) {
        String message = StringManager.PREFIX + "§eReplaced everything in §7" + target.getName() + "§e's inventory with a potato! §c(" + itemCount + (itemCount == 1 ? " item)" : " items)");
        player.sendMessage(message);
    }
}
