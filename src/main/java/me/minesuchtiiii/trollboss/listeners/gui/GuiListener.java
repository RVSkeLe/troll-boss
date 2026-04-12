package me.minesuchtiiii.trollboss.listeners.gui;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.GuiManager;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.manager.trolltutorial.TutorialManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import me.minesuchtiiii.trollboss.utils.GuiItem;
import me.minesuchtiiii.trollboss.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class GuiListener implements Listener {

    private final TrollBoss plugin;

    public GuiListener(TrollBoss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteractInGui(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof final Player p)) {
            return;
        }

        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();

        // Basic Checks
        if (item == null || item.getType() == Material.AIR) return;

        // -----------------------------------------------------------
        // MAIN TROLL GUI
        // -----------------------------------------------------------
        if ("§cTroll-Gui".equals(title)) {
            e.setCancelled(true);

            final UUID targetUUID = this.plugin.trolling.get(p.getUniqueId());
            if (targetUUID == null) {
                this.plugin.closeGui(p);
                return;
            }

            final String targetName = Bukkit.getPlayer(targetUUID) != null ? Bukkit.getPlayer(targetUUID).getName() : null;

            if (targetName == null) {
                this.plugin.closeGui(p);
                p.sendMessage(StringManager.PREFIX + "§cSorry, the target is not online anymore!");
                this.plugin.trolling.remove(p.getUniqueId());
                return;
            }

            String commandToExecute = null;

            switch (item.getType()) {
                case EMERALD -> {
                    executeTroll(p, null);
                    return;
                }
                case CHEST -> {
                    if (!checkTutorial(p)) {
                        openChoseWindow(p);
                    }
                    return;
                }
                case BOW -> commandToExecute = "trollbows";
                case FISHING_ROD -> commandToExecute = "tptroll";

                // Items with Amount-Check (PAPER)
                case PAPER -> {
                    if (item.getAmount() == 1) commandToExecute = "turn " + targetName;
                    else if (item.getAmount() == 2) commandToExecute = "invtext " + targetName;
                }

                // Items with DisplayName-Check
                case COW_SPAWN_EGG -> {
                    if (isDisplayName(item, "§eTrample")) commandToExecute = "trample " + targetName + " 9";
                }
                case CREEPER_SPAWN_EGG -> {
                    if (isDisplayName(item, "§eCreeper")) commandToExecute = "creeper " + targetName;
                }

                // Special Logic: Garbage (Toggle)
                case WRITABLE_BOOK -> {
                    String state = TrollManager.isActive(targetUUID, TrollType.GARBAGE) ? "off" : "on";
                    commandToExecute = "garbage " + targetName + " " + state;
                }

                // Standard Trolls
                case APPLE -> commandToExecute = "badapple " + targetName;
                case FIRE_CHARGE -> commandToExecute = "bolt " + targetName;
                case TNT -> commandToExecute = "boom " + targetName;
                case LAVA_BUCKET -> commandToExecute = "burn " + targetName;
                case DIRT -> commandToExecute = "bury " + targetName + " 10";
                case STRING -> commandToExecute = "crash " + targetName;
                case LEATHER_BOOTS -> commandToExecute = "denymove " + targetName + " 20";
                case DIAMOND -> commandToExecute = "fakeop " + targetName;
                case GOLD_INGOT -> commandToExecute = "fakedeop " + targetName;
                case BLAZE_POWDER -> commandToExecute = "fakerestart 60";
                case WHITE_WOOL -> commandToExecute = "freefall " + targetName + " 50";
                case ICE -> commandToExecute = "freeze " + targetName;
                case SOUL_SAND -> commandToExecute = "gokill " + targetName + " 30";
                case GLOWSTONE_DUST -> commandToExecute = "herobrine " + targetName;
                case REDSTONE -> commandToExecute = "hurt " + targetName + " 10";
                case POTION -> commandToExecute = "infect " + targetName + " 25";
                case FIREWORK_ROCKET -> commandToExecute = "launch " + targetName;
                case GRASS_BLOCK -> commandToExecute = "nomine " + targetName + " 30";
                case BAKED_POTATO -> commandToExecute = "potatotroll " + targetName;
                case JACK_O_LANTERN -> commandToExecute = "pumpkinhead " + targetName;
                case FEATHER -> commandToExecute = "push " + targetName;
                case ENDER_PEARL -> commandToExecute = "randomtp " + targetName + " 10";
                case OAK_SIGN -> commandToExecute = "spam " + targetName + " 20";
                case COOKED_CHICKEN -> commandToExecute = "starve " + targetName + " 16";
                case BEDROCK -> commandToExecute = "trap " + targetName + " 30";
                case IRON_DOOR -> commandToExecute = "trollkick " + targetName;
                case OBSIDIAN -> commandToExecute = "void " + targetName;
                case COBWEB -> commandToExecute = "webtrap " + targetName + " 15";
                case BONE -> commandToExecute = "spank " + targetName;
                case LEVER -> commandToExecute = "stfu " + targetName;
                case BOOK -> commandToExecute = "popup " + targetName;
                case GLASS -> commandToExecute = "sky " + targetName + " 20";
                case CLOCK -> commandToExecute = "abduct " + targetName;
                case EXPERIENCE_BOTTLE -> commandToExecute = "popular " + targetName;
                case ARROW -> commandToExecute = "sparta " + targetName;
                case WHEAT -> commandToExecute = "drug " + targetName;
                case INK_SAC -> commandToExecute = "squidrain " + targetName + " 50";
                case DROPPER -> commandToExecute = "dropinv " + targetName;
                case ANVIL -> commandToExecute = "anvil " + targetName;
                case IRON_BOOTS -> commandToExecute = "runforrest " + targetName + " 60";
                case DEAD_BUSH -> commandToExecute = "border " + targetName;
                case VINE -> commandToExecute = "noob " + targetName;
                case PINK_TULIP -> commandToExecute = "schlong " + targetName;
            }

            if (commandToExecute != null) {
                executeTroll(p, commandToExecute);
            }
        }

        // -----------------------------------------------------------
        // SPECIAL CHOOSE GUI
        // -----------------------------------------------------------
        else if ("§cChoose the special".equals(title)) {
            e.setCancelled(true);

            switch (item.getType()) {
                case EMERALD -> {
                    if (item.getAmount() == 1) executeTroll(p, "special 1");
                    else if (item.getAmount() == 2) executeTroll(p, "special 2");
                }
                case IRON_DOOR -> GuiManager.openGui(p);
            }
        }
    }

    private void executeTroll(Player p, String command) {

        if (checkTutorial(p)) return;

        this.plugin.closeGui(p);
        this.plugin.trolling.remove(p.getUniqueId());

        if (command != null) {
            p.performCommand(command);
        }
    }

    private boolean checkTutorial(Player p) {
        if (TutorialManager.isUsable(p)) {
            p.sendMessage(StringManager.PREFIX + "§cNot available in the tutorial!");
            return true;
        }
        return false;
    }

    private boolean isDisplayName(ItemStack item, String name) {
        return item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && name.equals(item.getItemMeta().getDisplayName());
    }

    public void openChoseWindow(Player p) {
        final Inventory inv = Bukkit.createInventory(null, 9, "§cChoose the special");

        final ItemStack one = GuiItem.createGuiItem(1, Material.EMERALD, "§7#1 §eAk-47", "§7To get the AK-47.");
        final ItemStack two = GuiItem.createGuiItem(2, Material.EMERALD, "§7#2 §eBlock Shooter", "§7To get the Block Shooter.");
        final ItemStack back = GuiItem.createGuiItem(1, Material.IRON_DOOR, "§bReturn to the Troll-Gui", "§7To return to the Troll-Gui.");

        final ItemStack none = new ItemStack(Material.GLASS_PANE, 1);
        final ItemMeta nonemeta = none.getItemMeta();
        nonemeta.setDisplayName("§5");
        none.setItemMeta(nonemeta);

        inv.setItem(0, one);
        inv.setItem(1, two);
        inv.setItem(8, back);

        inv.setItem(2, none);
        inv.setItem(3, none);
        inv.setItem(4, none);
        inv.setItem(5, none);
        inv.setItem(6, none);
        inv.setItem(7, none);

        p.openInventory(inv);
    }

}
