package me.minesuchtiiii.trollboss.items.bows;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Stream;

public final class BowsItem {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private BowsItem() {}

    private static final ItemStack BOLT_BOW = createBow(
            "<yellow>Bolt Bow</yellow>",
            "<gray>Strikes a lightning at the arrows location.</gray>"
    );

    private static final ItemStack BOOM_BOW = createBow(
            "<yellow>Boom Bow</yellow>",
            "<gray>Creates an explosion at the arrows location.</gray>"
    );

    private static final ItemStack CREEPER_BOW = createBow(
            "<yellow>Creeper Bow</yellow>",
            "<gray>Spawns a creeper at the arrows location.</gray>"
    );

    private static final ItemStack PULL_BOW = createBow(
            "<yellow>Pull Bow</yellow>",
            "<gray>Pulls the hit entity into your direction.</gray>"
    );

    private static final ItemStack ALL_BOWS = createBow(
            "<yellow>Get all bows at once</yellow>",
            "<gray>Adds every Troll-Bow to your inventory.</gray>"
    );

    private static final ItemStack BACK = createSimple(
            Material.IRON_DOOR,
            "<aqua>Return to the Troll-Gui</aqua>",
            "<gray>To return to the Troll-Gui.</gray>"
    );

    private static final ItemStack FILLER = createSimple(
            Material.GLASS_PANE,
            "<purple></purple>"
    );

    public static ItemStack boltBow() { return BOLT_BOW.clone(); }
    public static ItemStack boomBow() { return BOOM_BOW.clone(); }
    public static ItemStack creeperBow() { return CREEPER_BOW.clone(); }
    public static ItemStack pullBow() { return PULL_BOW.clone(); }
    public static ItemStack allBows() { return ALL_BOWS.clone(); }
    public static ItemStack back() { return BACK.clone(); }
    public static ItemStack filler() { return FILLER.clone(); }

    // Helpers
    private static ItemStack createBow(String name, String... lore) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(MINI.deserialize(name));
            meta.lore(deserializeLore(lore));
            meta.addEnchant(Enchantment.INFINITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }

        return item;
    }

    private static ItemStack createSimple(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(MINI.deserialize(name));
            if (lore.length > 0) {
                meta.lore(deserializeLore(lore));
            }
            item.setItemMeta(meta);
        }

        return item;
    }

    private static List<Component> deserializeLore(String... lore) {
        return Stream.of(lore)
                .map(MINI::deserialize)
                .toList();
    }
}
