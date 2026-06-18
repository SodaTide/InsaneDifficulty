package com.bravesurvival.insanedifficulty.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

public final class BlockUtil {

    private BlockUtil() {}

    public static boolean isFireAir(Material material) {
        return material == Material.AIR || material == Material.CAVE_AIR || material == Material.VOID_AIR ||
               material == Material.SHORT_GRASS || material == Material.FERN || material == Material.DEAD_BUSH ||
               material == Material.DANDELION || material == Material.POPPY || material == Material.BLUE_ORCHID ||
               material == Material.ALLIUM || material == Material.AZURE_BLUET || material == Material.CORNFLOWER ||
               material == Material.LILY_OF_THE_VALLEY || material == Material.OXEYE_DAISY ||
               material == Material.BROWN_MUSHROOM || material == Material.RED_MUSHROOM ||
               material == Material.TORCH || material == Material.WALL_TORCH ||
               material == Material.REDSTONE_TORCH || material == Material.REDSTONE_WALL_TORCH ||
               material == Material.SOUL_TORCH || material == Material.SOUL_WALL_TORCH ||
               material == Material.LEVER || material.name().endsWith("_CARPET") ||
               material.name().endsWith("_BUTTON") || material.name().endsWith("_PRESSURE_PLATE") ||
               material == Material.TALL_GRASS || material == Material.LARGE_FERN ||
               material == Material.VINE || material == Material.TWISTING_VINES || material == Material.WEEPING_VINES;
    }

    public static boolean isDoor(Material material) {
        return material.name().endsWith("_DOOR");
    }

    public static boolean isAdjacentToWater(Block block) {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (block.getRelative(x, y, z).getType() == Material.WATER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isNearLava(Block block, int range) {
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    if (block.getRelative(x, y, z).getType() == Material.LAVA) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void spreadFire(Block block) {
        if (isFireAir(block.getType())) {
            block.setType(Material.FIRE);
        }
    }
}
