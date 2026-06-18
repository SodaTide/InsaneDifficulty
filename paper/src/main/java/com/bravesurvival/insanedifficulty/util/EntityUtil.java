package com.bravesurvival.insanedifficulty.util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public final class EntityUtil {

    private static final Random RANDOM = new Random();

    private EntityUtil() {}

    public static void setAttribute(LivingEntity entity, Attribute attribute, double value) {
        if (value <= 0) return;
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    public static void addEffect(LivingEntity entity, PotionEffectType type, int duration, int amplifier) {
        entity.addPotionEffect(new PotionEffect(type, duration, amplifier, false, false));
    }

    public static void addEffect(LivingEntity entity, PotionEffectType type, int duration, int amplifier, boolean showParticles) {
        entity.addPotionEffect(new PotionEffect(type, duration, amplifier, false, showParticles));
    }

    public static void setItemUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
    }

    public static void addEnchant(ItemStack item, org.bukkit.enchantments.Enchantment enchantment, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        }
    }

    public static Player getNearestPlayer(Entity entity, double range) {
        Player nearest = null;
        double minDist = range * range;
        for (Entity e : entity.getNearbyEntities(range, range, range)) {
            if (e instanceof Player player) {
                if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) continue;
                double dist = entity.getLocation().distanceSquared(player.getLocation());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = player;
                }
            }
        }
        return nearest;
    }

    public static boolean isWearingFullGold(Player player) {
        EntityEquipment eq = player.getEquipment();
        if (eq == null) return false;
        return eq.getHelmet() != null && eq.getHelmet().getType() == Material.GOLDEN_HELMET &&
               eq.getChestplate() != null && eq.getChestplate().getType() == Material.GOLDEN_CHESTPLATE &&
               eq.getLeggings() != null && eq.getLeggings().getType() == Material.GOLDEN_LEGGINGS &&
               eq.getBoots() != null && eq.getBoots().getType() == Material.GOLDEN_BOOTS;
    }

    public static void generateRandomArmor(LivingEntity entity) {
        EntityEquipment eq = entity.getEquipment();
        if (eq == null) return;

        Material[] helmets = {Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET};
        Material[] chestplates = {Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE};
        Material[] leggings = {Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS};
        Material[] boots = {Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS};
        Material[] weapons = {Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SHOVEL, Material.IRON_SHOVEL, Material.GOLDEN_SHOVEL};

        if (RANDOM.nextDouble() < 0.5) eq.setHelmet(new ItemStack(helmets[RANDOM.nextInt(helmets.length)]));
        if (RANDOM.nextDouble() < 0.5) eq.setChestplate(new ItemStack(chestplates[RANDOM.nextInt(chestplates.length)]));
        if (RANDOM.nextDouble() < 0.5) eq.setLeggings(new ItemStack(leggings[RANDOM.nextInt(leggings.length)]));
        if (RANDOM.nextDouble() < 0.5) eq.setBoots(new ItemStack(boots[RANDOM.nextInt(boots.length)]));
        if (RANDOM.nextDouble() < 0.5 && !(entity instanceof Drowned)) {
            eq.setItemInMainHand(new ItemStack(weapons[RANDOM.nextInt(weapons.length)]));
        }
    }

    public static boolean isRawFood(Material material) {
        return material == Material.COD || material == Material.SALMON || material == Material.TROPICAL_FISH ||
               material == Material.MUTTON || material == Material.CHICKEN || material == Material.RABBIT ||
               material == Material.BEEF || material == Material.PORKCHOP;
    }

    public static boolean isFood(Material material) {
        return isRawFood(material) ||
               material == Material.COOKED_BEEF || material == Material.COOKED_PORKCHOP ||
               material == Material.COOKED_CHICKEN || material == Material.COOKED_MUTTON ||
               material == Material.COOKED_RABBIT || material == Material.COOKED_COD ||
               material == Material.COOKED_SALMON || material == Material.APPLE ||
               material == Material.BREAD || material == Material.COOKIE ||
               material == Material.MELON_SLICE || material == Material.DRIED_KELP ||
               material == Material.CARROT || material == Material.POTATO ||
               material == Material.BAKED_POTATO || material == Material.PUMPKIN_PIE ||
               material == Material.BEETROOT || material == Material.SWEET_BERRIES ||
               material == Material.HONEY_BOTTLE || material == Material.MUSHROOM_STEW ||
               material == Material.RABBIT_STEW || material == Material.BEETROOT_SOUP;
    }

    public static boolean isMeat(Material material) {
        return isRawFood(material) ||
               material == Material.COOKED_BEEF || material == Material.COOKED_PORKCHOP ||
               material == Material.COOKED_CHICKEN || material == Material.COOKED_MUTTON ||
               material == Material.COOKED_RABBIT || material == Material.COOKED_COD ||
               material == Material.COOKED_SALMON;
    }

    public static boolean isPotion(Material material) {
        return material == Material.POTION || material == Material.SPLASH_POTION || material == Material.LINGERING_POTION;
    }

    public static boolean isOverworldOre(Material material) {
        return material == Material.COAL_ORE || material == Material.DEEPSLATE_COAL_ORE ||
               material == Material.IRON_ORE || material == Material.DEEPSLATE_IRON_ORE ||
               material == Material.GOLD_ORE || material == Material.DEEPSLATE_GOLD_ORE ||
               material == Material.DIAMOND_ORE || material == Material.DEEPSLATE_DIAMOND_ORE ||
               material == Material.EMERALD_ORE || material == Material.DEEPSLATE_EMERALD_ORE ||
               material == Material.LAPIS_ORE || material == Material.DEEPSLATE_LAPIS_ORE ||
               material == Material.REDSTONE_ORE || material == Material.DEEPSLATE_REDSTONE_ORE;
    }

    public static boolean isGoodPickaxe(Material material) {
        return material == Material.IRON_PICKAXE || material == Material.GOLDEN_PICKAXE ||
               material == Material.DIAMOND_PICKAXE || material == Material.NETHERITE_PICKAXE;
    }

    public static boolean isInfestedStone(Material material) {
        return material == Material.INFESTED_STONE || material == Material.INFESTED_COBBLESTONE ||
               material == Material.INFESTED_STONE_BRICKS || material == Material.INFESTED_MOSSY_STONE_BRICKS ||
               material == Material.INFESTED_CRACKED_STONE_BRICKS || material == Material.INFESTED_CHISELED_STONE_BRICKS;
    }

    public static boolean isHorse(Entity entity) {
        return entity instanceof Horse || entity instanceof SkeletonHorse || entity instanceof ZombieHorse;
    }
}
