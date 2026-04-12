package me.minesuchtiiii.trollboss.utils;

import me.minesuchtiiii.trollboss.TrollBoss;
import me.minesuchtiiii.trollboss.manager.TrollManager;
import me.minesuchtiiii.trollboss.trolls.TrollType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.*;

// Should probably be replaced by using a schematic and FAWE
public class Ufo {
    public final Map<UUID, List<Location>> ufoBlockLocations = new HashMap<>();

    private final TrollBoss plugin;

    public Ufo(Player player, TrollBoss plugin) {

        this.plugin = plugin;
        UUID playerUUID = player.getUniqueId();
        Location loc = player.getLocation();
        ufoBlockLocations.put(playerUUID, new ArrayList<>());

        int baseY = (int) (loc.getY() + 75);
        int floor = baseY - 1;
        int first = baseY;
        int second = baseY + 1;
        int third = baseY + 2;
        int forth = baseY + 3;
        int fifth = baseY + 4;
        int ug = baseY - 2;
        int ug2 = baseY - 3;

        World world = loc.getWorld();
        int baseX = loc.getBlockX();
        int baseZ = loc.getBlockZ();

        // ---------------------------
        // 1. FLOOR
        // ---------------------------
        int[][] glassFloorOffsets = {
                {0, 1}, {0, 2}, {0, -1}, {0, -2},
                {-1, 0}, {-2, 0}, {1, 0}, {2, 0},
                {-1, 1}, {1, 1}, {-1, -1}, {1, -1}
        };
        for (int[] off : glassFloorOffsets) {
            setBlock(playerUUID, world, baseX + off[0], floor, baseZ + off[1], Material.BLACK_STAINED_GLASS);
        }

        // Green floor (LIME_TERRACOTTA)
        int[][] greenFloorOffsets = {
                {0, -3}, {0, -4}, {0, -5}, {0, -6}, {0, -7},
                {0, 3}, {0, 4}, {0, 5}, {0, 6}, {0, 7},
                {1, 2}, {1, 3}, {1, 4}, {1, 5}, {1, 6}, {1, 7},
                {-1, 2}, {-1, 3}, {-1, 4}, {-1, 5}, {-1, 6}, {-1, 7},
                {1, -2}, {1, -3}, {1, -4}, {1, -5}, {1, -6},
                {-1, -2}, {-1, -3}, {-1, -4}, {-1, -5}, {-1, -6},
                {2, 1}, {2, 2}, {2, 3}, {2, 4}, {2, 5}, {2, 6},
                {-2, 1}, {-2, 2}, {-2, 3}, {-2, 4}, {-2, 5}, {-2, 6},
                {2, -1}, {2, -2}, {2, -3}, {2, -4}, {2, -5},
                {-2, -1}, {-2, -2}, {-2, -3}, {-2, -4}, {-2, -5},
                {3, 0}, {4, 0}, {5, 0}, {6, 0},
                {-3, 0}, {-4, 0}, {-5, 0}, {-6, 0},
                {3, 1}, {3, 2}, {3, 3},
                {3, -1}, {3, -2}, {3, -3}, {3, -4},
                {-3, 1}, {-3, 2}, {-3, 3},
                {-3, -1}, {-3, -2}, {-3, -3}, {-3, -4},
                {4, 1}, {4, 2},
                {4, -1}, {4, -2},
                {-4, 1}, {-4, 2},
                {-4, -1}, {-4, -2},
                {5, 1},
                {5, -1}
        };
        for (int[] off : greenFloorOffsets) {
            setBlock(playerUUID, world, baseX + off[0], floor, baseZ + off[1], Material.LIME_TERRACOTTA);
        }

        // ---------------------------
        // 2. FLOOR
        // ---------------------------
        setBlock(playerUUID, world, baseX, first, baseZ + 4, Material.POLISHED_ANDESITE_STAIRS);
        setBlock(playerUUID, world, baseX, first, baseZ - 6, Material.TORCH);

        // Smooth stone wall
        int[][] wall1Offsets = {
                {6, 0}, {-6, 0}, {0, -7},
                {5, 1}, {5, -1}, {-5, 1}, {-5, -1},
                {4, 2}, {4, -2}, {-4, 2}, {-4, -2},
                {3, 3}, {3, -3}, {3, -4},
                {-3, 3}, {-3, -3}, {-3, -4},
                {2, 4}, {2, 5}, {2, 6},
                {-2, 4}, {-2, 5}, {-2, 6},
                {2, -5}, {-2, -5},
                {1, -6}, {-1, -6}
        };
        for (int[] off : wall1Offsets) {
            setBlock(playerUUID, world, baseX + off[0], first, baseZ + off[1], Material.SMOOTH_STONE);
        }

        int[][] windshield1Offsets = {
                {0, 7}, {1, 7}, {-1, 7}, {1, 6}, {-1, 6}
        };
        for (int[] off : windshield1Offsets) {
            setBlock(playerUUID, world, baseX + off[0], first, baseZ + off[1], Material.BLACK_STAINED_GLASS_PANE);
        }

        // ---------------------------
        // 3. FLOOR
        // ---------------------------
        int[][] wall2GlassOffsets = {
                {6, 0}, {-6, 0}, {0, -7}
        };
        for (int[] off : wall2GlassOffsets) {
            setBlock(playerUUID, world, baseX + off[0], second, baseZ + off[1], Material.BLACK_STAINED_GLASS_PANE);
        }
        // Smooth stone wall
        int[][] wall2StoneOffsets = {
                {5, 1}, {5, -1}, {-5, 1}, {-5, -1},
                {4, 2}, {4, -2}, {-4, 2}, {-4, -2},
                {3, 3}, {3, -3}, {3, -4},
                {-3, 3}, {-3, -3}, {-3, -4},
                {2, 4}, {2, 5}, {2, 6},
                {-2, 4}, {-2, 5}, {-2, 6},
                {2, -5}, {-2, -5},
                {1, -6}, {-1, -6}
        };
        for (int[] off : wall2StoneOffsets) {
            setBlock(playerUUID, world, baseX + off[0], second, baseZ + off[1], Material.SMOOTH_STONE);
        }

        for (int[] off : windshield1Offsets) {
            setBlock(playerUUID, world, baseX + off[0], second, baseZ + off[1], Material.BLACK_STAINED_GLASS_PANE);
        }

        // ---------------------------
        // 4. FLOOR
        // ---------------------------
        int[][] wall3StoneOffsets = {
                {6, 0}, {-6, 0}, {0, -7},
                {5, 1}, {5, -1}, {-5, 1}, {-5, -1},
                {4, 2}, {4, -2}, {-4, 2}, {-4, -2},
                {3, 3}, {3, -3}, {3, -4},
                {-3, 3}, {-3, -3}, {-3, -4},
                {2, 4}, {2, 5}, {2, 6},
                {-2, 4}, {-2, 5}, {-2, 6},
                {2, -5}, {-2, -5},
                {1, -6}, {-1, -6}
        };
        for (int[] off : wall3StoneOffsets) {
            setBlock(playerUUID, world, baseX + off[0], third, baseZ + off[1], Material.SMOOTH_STONE);
        }

        int[][] windshield3Offsets = {
                {0, 7}, {1, 7}, {-1, 7}, {1, 6}, {-1, 6}
        };
        for (int[] off : windshield3Offsets) {
            setBlock(playerUUID, world, baseX + off[0], third, baseZ + off[1], Material.SMOOTH_STONE);
        }

        // ---------------------------
        // 5. FLOOR
        // ---------------------------
        BlockPlacement[] wall4Placements = {
                new BlockPlacement(5, 0, Material.SMOOTH_STONE),
                new BlockPlacement(-5, 0, Material.SMOOTH_STONE),
                new BlockPlacement(0, -6, Material.SMOOTH_STONE),
                new BlockPlacement(0, 3, Material.SMOOTH_STONE),
                new BlockPlacement(0, 4, Material.BLACK_STAINED_GLASS),
                new BlockPlacement(0, 5, Material.SMOOTH_STONE),
                new BlockPlacement(0, 6, Material.SMOOTH_STONE),
                new BlockPlacement(1, 3, Material.SMOOTH_STONE),
                new BlockPlacement(1, 4, Material.SMOOTH_STONE),
                new BlockPlacement(1, 5, Material.SMOOTH_STONE),
                new BlockPlacement(-1, 3, Material.SMOOTH_STONE),
                new BlockPlacement(-1, 4, Material.SMOOTH_STONE),
                new BlockPlacement(-1, 5, Material.SMOOTH_STONE),
                new BlockPlacement(2, 2, Material.SMOOTH_STONE),
                new BlockPlacement(2, 3, Material.SMOOTH_STONE),
                new BlockPlacement(-2, 2, Material.SMOOTH_STONE),
                new BlockPlacement(-2, 3, Material.SMOOTH_STONE),
                new BlockPlacement(3, 2, Material.SMOOTH_STONE),
                new BlockPlacement(3, -2, Material.SMOOTH_STONE),
                new BlockPlacement(-3, 2, Material.SMOOTH_STONE),
                new BlockPlacement(-3, -2, Material.SMOOTH_STONE),
                new BlockPlacement(4, 1, Material.SMOOTH_STONE),
                new BlockPlacement(4, -1, Material.SMOOTH_STONE),
                new BlockPlacement(-4, 1, Material.SMOOTH_STONE),
                new BlockPlacement(-4, -1, Material.SMOOTH_STONE),
                new BlockPlacement(2, -3, Material.SMOOTH_STONE),
                new BlockPlacement(2, -4, Material.SMOOTH_STONE),
                new BlockPlacement(-2, -3, Material.SMOOTH_STONE),
                new BlockPlacement(-2, -4, Material.SMOOTH_STONE),
                new BlockPlacement(1, -5, Material.SMOOTH_STONE),
                new BlockPlacement(-1, -5, Material.SMOOTH_STONE)
        };
        for (BlockPlacement bp : wall4Placements) {
            setBlock(playerUUID, world, baseX + bp.dx, forth, baseZ + bp.dz, bp.material);
        }

        // ---------------------------
        // 6. FLOOR
        // ---------------------------
        setBlock(playerUUID, world, baseX, fifth, baseZ, Material.BLACK_STAINED_GLASS);

        int[][] floor5SmoothOffsets = {
                {0, 1}, {0, 2}, {0, 3}, {0, 4},
                {0, -1}, {0, -2}, {0, -3}, {0, -4}, {0, -5},
                {1, 0}, {2, 0}, {3, 0}, {4, 0},
                {-1, 0}, {-2, 0}, {-3, 0}, {-4, 0},
                {1, 1}, {1, 2},
                {-1, 1}, {-1, 2},
                {2, 1}, {3, 1},
                {-2, 1}, {-3, 1},
                {1, -1}, {2, -1}, {3, -1},
                {-1, -1}, {-2, -1}, {-3, -1},
                {1, -2}, {2, -2},
                {-1, -2}, {-2, -2},
                {1, -3}, {1, -4},
                {-1, -3}, {-1, -4}
        };
        for (int[] off : floor5SmoothOffsets) {
            setBlock(playerUUID, world, baseX + off[0], fifth, baseZ + off[1], Material.SMOOTH_STONE);
        }

        setBlock(playerUUID, world, baseX, fifth, baseZ + 4, Material.BLACK_STAINED_GLASS);

        // ---------------------------
        // RING
        // ---------------------------
        BlockPlacement[] ringPlacements = {
                new BlockPlacement(6, 1, Material.LIME_TERRACOTTA),
                new BlockPlacement(6, -1, Material.LIME_TERRACOTTA),
                new BlockPlacement(-6, 1, Material.LIME_TERRACOTTA),
                new BlockPlacement(-6, -1, Material.LIME_TERRACOTTA),
                new BlockPlacement(5, 2, Material.LIME_TERRACOTTA),
                new BlockPlacement(5, -2, Material.LIME_TERRACOTTA),
                new BlockPlacement(-5, 2, Material.LIME_TERRACOTTA),
                new BlockPlacement(-5, -2, Material.LIME_TERRACOTTA),
                new BlockPlacement(4, 3, Material.LIME_TERRACOTTA),
                new BlockPlacement(-4, 3, Material.LIME_TERRACOTTA),
                new BlockPlacement(3, 4, Material.LIME_TERRACOTTA),
                new BlockPlacement(-3, 4, Material.LIME_TERRACOTTA),
                new BlockPlacement(4, -3, Material.LIME_TERRACOTTA),
                new BlockPlacement(4, -4, Material.LIME_TERRACOTTA),
                new BlockPlacement(-4, -3, Material.LIME_TERRACOTTA),
                new BlockPlacement(-4, -4, Material.LIME_TERRACOTTA),
                new BlockPlacement(3, -5, Material.LIME_TERRACOTTA),
                new BlockPlacement(-3, -5, Material.LIME_TERRACOTTA)
        };
        for (BlockPlacement bp : ringPlacements) {
            setBlock(playerUUID, world, baseX + bp.dx, second, baseZ + bp.dz, bp.material);
        }

        // ---------------------------
        // "Down"
        // ---------------------------
        int[][] downOffsets = {
                {0, 3}, {0, 4}, {0, 5}, {0, 6},
                {0, -3}, {0, -4}, {0, -5},
                {3, 0}, {-3, 0},
                {4, 0}, {-4, 0},
                {2, 1}, {3, -1}, {-2, 1}, {-3, -1},
                {1, 2}, {2, 2}, {1, -2}, {2, -2},
                {-1, 2}, {-2, 2}, {-1, -2}, {-2, -2},
                {1, -3}, {1, -4}, {-1, -3}, {-1, -4},
                {1, 3}, {1, 4}, {1, 5}, {-1, 3}, {-1, 4}, {-1, 5},
                {3, 1}, {-3, 1},
                {2, -1}, {-2, -1},
                {2, -3}, {-2, -3}
        };
        for (int[] off : downOffsets) {
            setBlock(playerUUID, world, baseX + off[0], ug, baseZ + off[1], Material.SMOOTH_STONE);
        }

        // ---------------------------
        // "Down2"
        // ---------------------------
        BlockPlacement[] down2Placements = {
                new BlockPlacement(3, 0, Material.SMOOTH_STONE),
                new BlockPlacement(-3, 0, Material.SMOOTH_STONE),
                new BlockPlacement(0, 3, Material.SMOOTH_STONE),
                new BlockPlacement(0, -3, Material.SMOOTH_STONE),
                new BlockPlacement(2, 1, Material.SMOOTH_STONE),
                new BlockPlacement(2, -1, Material.SMOOTH_STONE),
                new BlockPlacement(-2, 1, Material.SMOOTH_STONE),
                new BlockPlacement(-2, -1, Material.SMOOTH_STONE),
                new BlockPlacement(1, 2, Material.SMOOTH_STONE),
                new BlockPlacement(1, -2, Material.SMOOTH_STONE),
                new BlockPlacement(-1, 2, Material.SMOOTH_STONE),
                new BlockPlacement(-1, -2, Material.SMOOTH_STONE)
        };
        for (BlockPlacement bp : down2Placements) {
            setBlock(playerUUID, world, baseX + bp.dx, ug2, baseZ + bp.dz, bp.material);
        }

        // ---------------------------
        // Sign
        // ---------------------------
        setBlock(playerUUID, world, baseX, first, baseZ + 3, Material.OAK_WALL_SIGN);
        Block signBlock = world.getBlockAt(baseX, first, baseZ + 3);
        if (signBlock.getState() instanceof Sign) {
            Sign s = (Sign) signBlock.getState();
            s.setLine(0, "§a§lYou have been");
            s.setLine(1, "§a§labducted by");
            s.setLine(2, "§4§lALIENS!");
            s.setLine(3, "§f§lRest in Peace.");
            s.update();
        }

        setBlock(playerUUID, world, baseX, floor, baseZ, Material.BLACK_STAINED_GLASS);
    }

    /**
     * Sets a block at the specified location to the given material and records the block's location
     * under the player's UUID in the UFO block locations map.
     *
     * @param playerUUID The unique identifier of the player whose block changes are being tracked.
     * @param world      The world where the block is to be set.
     * @param x          The x-coordinate of the block's location.
     * @param y          The y-coordinate of the block's location.
     * @param z          The z-coordinate of the block's location.
     * @param material   The material to set the block to.
     */
    private void setBlock(UUID playerUUID, World world, int x, int y, int z, Material material) {
        world.getBlockAt(x, y, z).setType(material);
        ufoBlockLocations.get(playerUUID).add(new Location(world, x, y, z));
    }

    /**
     * Removes the UFO associated with the specified player UUID. This method
     * clears all blocks linked to the player's UFO, replacing them with air,
     * and removes the corresponding entry from the plugin's ufoBlockLocations map.
     *
     * @param playerUUID The unique identifier of the player whose UFO is to be removed.
     */
    public void removeUfo(UUID playerUUID) {
        List<Location> blocks = ufoBlockLocations.get(playerUUID);
        if (blocks != null) {
            for (Location loc : blocks) {
                loc.getBlock().setType(Material.AIR);
            }
            ufoBlockLocations.remove(playerUUID);
        }
    }

    public void teleportBackFromUfo(Player p) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            UUID playerUUID = p.getUniqueId();

            p.setHealth(20D);
            p.teleport(plugin.abductedCachedLocations.get(playerUUID));
            plugin.abductedCachedLocations.remove(playerUUID);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.setGameMode(GameMode.SURVIVAL), 10L);
            removeUfo(playerUUID);

        }, 15 * 20L);

    }

    /**
     * Small helper class to store relative block placements with material.
     */
    private static class BlockPlacement {
        int dx, dz;
        Material material;

        BlockPlacement(int dx, int dz, Material material) {
            this.dx = dx;
            this.dz = dz;
            this.material = material;
        }
    }
}
