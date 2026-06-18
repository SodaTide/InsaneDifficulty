package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.BlockUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WorldManager {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;
    private int stormTimer = 0;
    private boolean stormActive = false;

    public WorldManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public void tick() {
        tickDragon();
        tickBubbleColumns();
        tickDoorsInWater();
        tickMobNoise();
    }

    public void tickBlazes() {
        for (World world : plugin.getServer().getWorlds()) {
            // 烈焰人火球扩散火焰
            for (SmallFireball fireball : world.getEntitiesByClass(SmallFireball.class)) {
                Block block = fireball.getLocation().getBlock();
                if (BlockUtil.isFireAir(block.getType())) {
                    block.setType(Material.FIRE);
                }
            }

            // 烈焰人脚下火焰
            for (Blaze blaze : world.getEntitiesByClass(Blaze.class)) {
                Block below = blaze.getLocation().getBlock();
                if (BlockUtil.isFireAir(below.getType())) {
                    below.setType(Material.FIRE);
                }
            }

            // 玩家手持烈焰棒/烈焰粉/岩浆桶着火
            for (Player player : world.getPlayers()) {
                Material mainHand = player.getInventory().getItemInMainHand().getType();
                Material offHand = player.getInventory().getItemInOffHand().getType();
                boolean hasFire = mainHand == Material.BLAZE_ROD || mainHand == Material.BLAZE_POWDER || mainHand == Material.LAVA_BUCKET ||
                                  offHand == Material.BLAZE_ROD || offHand == Material.BLAZE_POWDER || offHand == Material.LAVA_BUCKET;
                if (hasFire) {
                    Block block = player.getLocation().getBlock();
                    if (BlockUtil.isFireAir(block.getType())) {
                        block.setType(Material.FIRE);
                    }
                }

                // 玩家着火时留下火焰轨迹
                if (player.getFireTicks() > 0) {
                    Block block = player.getLocation().getBlock();
                    if (BlockUtil.isFireAir(block.getType())) {
                        block.setType(Material.FIRE);
                    }
                }
            }

            // 下界炼药锅蒸发
            if (world.getEnvironment() == World.Environment.NETHER) {
                for (Player player : world.getPlayers()) {
                    Block block = player.getLocation().getBlock();
                    if (block.getType() == Material.CAULDRON) {
                        org.bukkit.block.data.Levelled cauldron = (org.bukkit.block.data.Levelled) block.getBlockData();
                        if (cauldron.getLevel() > 0) {
                            cauldron.setLevel(cauldron.getLevel() - 1);
                            block.setBlockData(cauldron);
                        }
                    }
                }
            }
        }
    }

    public void tickBuckets() {
        if (!config.isBucketsLeak()) return;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getWorld().getEnvironment() == World.Environment.NETHER) continue;
            if (!RNG.chance(config.getBucketLeakChance())) continue;

            Block block = player.getLocation().getBlock();
            if (!BlockUtil.isFireAir(block.getType())) continue;

            Material mainHand = player.getInventory().getItemInMainHand().getType();
            Material offHand = player.getInventory().getItemInOffHand().getType();

            if (mainHand == Material.WATER_BUCKET) {
                block.setType(Material.WATER);
                player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
            } else if (mainHand == Material.LAVA_BUCKET) {
                block.setType(Material.LAVA);
                player.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
            } else if (offHand == Material.WATER_BUCKET) {
                block.setType(Material.WATER);
                player.getInventory().setItemInOffHand(new ItemStack(Material.BUCKET));
            } else if (offHand == Material.LAVA_BUCKET) {
                block.setType(Material.LAVA);
                player.getInventory().setItemInOffHand(new ItemStack(Material.BUCKET));
            }
        }
    }

    public void tickWeather() {
        if (!config.isLightningStorms()) return;

        // 随机触发风暴
        if (!stormActive && RNG.chance(config.getLightningStormChance())) {
            startStorm();
        }

        if (stormActive) {
            stormTimer++;
            if (stormTimer >= 1 && stormTimer <= 600) {
                // 生成闪电
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                        generateLightning(player);
                    }
                }
            }
            if (stormTimer >= 600) {
                stormActive = false;
                stormTimer = 0;
            }
        }
    }

    private void startStorm() {
        stormActive = true;
        stormTimer = -100; // 延迟100 ticks开始

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getWorld().getEnvironment() == World.Environment.NORMAL) {
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 8.0f, 0.75f);
            }
        }

        for (World world : plugin.getServer().getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                world.setThundering(true);
                world.setWeatherDuration(700);
            }
        }
    }

    private void generateLightning(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();

        // 普通闪电
        if (RNG.chance(config.getLightningNormalChance())) {
            world.strikeLightning(randomLocation(loc, 32));
        }

        // 精准闪电（玩家附近4格）
        if (RNG.chance(config.getLightningPrecisionChance())) {
            world.strikeLightning(randomLocation(loc, 4));
        }

        // 捆绑闪电（单实体8次落雷）
        if (RNG.chance(config.getLightningBundledChance())) {
            Location sl = randomLocation(loc, 16);
            for (int dx = -2; dx <= 2; dx += 2) {
                for (int dz = -2; dz <= 2; dz += 2) {
                    world.strikeLightning(sl.clone().add(dx, 0, dz));
                }
            }
        }

        // 递归闪电（同一位置持续落雷20 ticks）
        if (RNG.chance(config.getLightningRecursiveChance())) {
            Location rl = randomLocation(loc, 32);
            // 延迟生成闪电，模拟持续落雷效果
            for (int i = 0; i < 20; i++) {
                final Location strikeLoc = rl.clone();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    world.strikeLightning(strikeLoc);
                }, i);
            }
        }

        // 群体闪电（16次分散落雷48格范围）
        if (RNG.chance(config.getLightningGroupChance())) {
            for (int i = 0; i < 16; i++) {
                world.strikeLightning(randomLocation(loc, 48));
            }
        }
    }

    private void tickDragon() {
        if (!config.isEnderDragonEnabled()) return;
        for (World world : plugin.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END) continue;
            for (EnderDragon dragon : world.getEntitiesByClass(EnderDragon.class)) {
                if (RNG.chance(config.getEnderDragonRegenChance())) {
                    double newHealth = Math.min(dragon.getHealth() + config.getEnderDragonRegenAmount(), dragon.getMaxHealth());
                    dragon.setHealth(newHealth);
                }
            }
        }
    }

    private void tickBubbleColumns() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Block below = player.getLocation().getBlock().getRelative(0, -1, 0);
            if (below.getType() == Material.BUBBLE_COLUMN) {
                org.bukkit.block.data.type.BubbleColumn bubble = (org.bukkit.block.data.type.BubbleColumn) below.getBlockData();
                if (bubble.isDrag()) {
                    player.setVelocity(player.getVelocity().add(new Vector(0, -0.5, 0)));
                }
            }
        }
    }

    private void tickDoorsInWater() {
        if (!config.isDoorsBreakInWater()) return;
        for (World world : plugin.getServer().getWorlds()) {
            for (Player player : world.getPlayers()) {
                Block block = player.getLocation().getBlock();
                if (BlockUtil.isDoor(block.getType()) && BlockUtil.isAdjacentToWater(block)) {
                    block.breakNaturally();
                }
            }
        }
    }

    private void tickMobNoise() {
        if (!config.isRandomMobNoises()) return;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getWorld().getEnvironment() != World.Environment.NORMAL) continue;
            if (player.getLocation().getBlock().getLightLevel() > 0) continue;
            if (!RNG.chance(config.getRandomMobNoiseChance())) continue;

            Sound sound = switch (RNG.range(0, 16)) {
                case 0, 1 -> Sound.ENTITY_ZOMBIE_AMBIENT;
                case 2, 3 -> Sound.ENTITY_SKELETON_AMBIENT;
                case 4 -> Sound.ENTITY_ARROW_HIT;
                case 5, 6 -> Sound.ENTITY_CREEPER_HURT;
                case 7 -> Sound.ENTITY_ENDERMAN_AMBIENT;
                case 8, 9 -> Sound.ENTITY_ENDERMAN_SCREAM;
                case 10 -> Sound.ENTITY_WITCH_AMBIENT;
                case 11 -> Sound.AMBIENT_CAVE;
                case 12 -> Sound.AMBIENT_WARPED_FOREST_MOOD;
                case 13 -> Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD;
                case 14 -> Sound.AMBIENT_NETHER_WASTES_MOOD;
                case 15 -> Sound.AMBIENT_CRIMSON_FOREST_MOOD;
                default -> Sound.AMBIENT_BASALT_DELTAS_MOOD;
            };
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    private Location randomLocation(Location center, int radius) {
        double angle = Math.random() * Math.PI * 2;
        double dist = Math.random() * radius;
        double x = center.getX() + Math.cos(angle) * dist;
        double z = center.getZ() + Math.sin(angle) * dist;
        int y = center.getWorld().getHighestBlockYAt((int) x, (int) z);
        return new Location(center.getWorld(), x, y, z);
    }
}
