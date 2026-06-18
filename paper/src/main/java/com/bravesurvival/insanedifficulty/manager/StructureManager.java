package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.generator.structure.Structure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StructureManager {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;
    private final Map<String, Map<UUID, Boolean>> visitedStructures = new HashMap<>();
    private final Map<UUID, Long> lastBadOmenTime = new HashMap<>();

    // 结构对象缓存
    private Structure desertPyramid;
    private Structure shipwreck;
    private Structure buriedTreasure;
    private Structure ruinedPortal;
    private Structure junglePyramid;
    private Structure village;

    public StructureManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        initStructures();
    }

    private void initStructures() {
        try {
            desertPyramid = Registry.STRUCTURE.get(NamespacedKey.minecraft("desert_pyramid"));
            shipwreck = Registry.STRUCTURE.get(NamespacedKey.minecraft("shipwreck"));
            buriedTreasure = Registry.STRUCTURE.get(NamespacedKey.minecraft("buried_treasure"));
            ruinedPortal = Registry.STRUCTURE.get(NamespacedKey.minecraft("ruined_portal"));
            junglePyramid = Registry.STRUCTURE.get(NamespacedKey.minecraft("jungle_pyramid"));
            village = Registry.STRUCTURE.get(NamespacedKey.minecraft("village"));
        } catch (Exception e) {
            plugin.getLogger().warning("初始化结构检测失败: " + e.getMessage());
        }
    }

    public void onPlayerInteract(Player player, Block block) {
        // 沙漠神殿开箱触发TNT
        if (config.isDesertTempleTnt() && isDesertTempleChest(block)) {
            Location loc = block.getLocation();
            loc.getWorld().spawnEntity(loc.add(0, -1, 0), EntityType.TNT);
            loc.getWorld().playSound(loc, Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
        }
    }

    public void onPlayerMove(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        UUID uuid = player.getUniqueId();

        // 沙漠神殿生成尸壳
        if (config.isDesertTempleHusks() && isInStructure(world, loc, desertPyramid)) {
            if (!hasVisited("desert_pyramid", uuid)) {
                markVisited("desert_pyramid", uuid);
                spawnHusks(loc, config.getDesertTempleHuskCount());
                player.playSound(loc, Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 1.0f, 1.0f);
            }
        }

        // 沉船生成溺尸
        if (config.isShipwreckDrowned() && isInStructure(world, loc, shipwreck)) {
            if (!hasVisited("shipwreck", uuid)) {
                markVisited("shipwreck", uuid);
                spawnDrowned(loc, config.getShipwreckDrownedCount());
                player.playSound(loc, Sound.BLOCK_CONDUIT_ATTACK_TARGET, 1.0f, 1.0f);
            }
        }

        // 埋藏宝藏生成恼鬼
        if (config.isBuriedTreasureVex() && isInStructure(world, loc, buriedTreasure)) {
            if (!hasVisited("buried_treasure", uuid)) {
                markVisited("buried_treasure", uuid);
                spawnVexes(loc, config.getBuriedTreasureVexCount());
                player.playSound(loc, Sound.BLOCK_CONDUIT_DEACTIVATE, 1.0f, 1.0f);
            }
        }

        // 废墟传送门生成幻术师
        if (config.isRuinedPortalIllusioner() && isInStructure(world, loc, ruinedPortal)) {
            if (!hasVisited("ruined_portal", uuid)) {
                markVisited("ruined_portal", uuid);
                spawnIllusioners(loc, config.getRuinedPortalIllusionerCount());
                player.playSound(loc, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.0f, 1.0f);
            }
        }

        // 丛林神庙生成幻术师
        if (config.isJunglePyramidIllusioner() && isInStructure(world, loc, junglePyramid)) {
            if (!hasVisited("jungle_pyramid", uuid)) {
                markVisited("jungle_pyramid", uuid);
                spawnIllusioners(loc, config.getJunglePyramidIllusionerCount());
                player.playSound(loc, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);
            }
        }

        // 村庄进入时给予不祥之兆（每60秒最多一次）
        if (config.isVillagerBadOmen() && isInStructure(world, loc, village)) {
            long now = System.currentTimeMillis();
            Long lastTime = lastBadOmenTime.get(uuid);
            if (lastTime == null || now - lastTime > 60000) {
                if (RNG.chance(config.getVillagerBadOmenChance())) {
                    player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.BAD_OMEN, 6000, 0, false, false));
                    lastBadOmenTime.put(uuid, now);
                }
            }
        }
    }

    private boolean hasVisited(String structureType, UUID uuid) {
        Map<UUID, Boolean> structureMap = visitedStructures.get(structureType);
        return structureMap != null && structureMap.getOrDefault(uuid, false);
    }

    private void markVisited(String structureType, UUID uuid) {
        visitedStructures.computeIfAbsent(structureType, k -> new HashMap<>()).put(uuid, true);
    }

    private void spawnHusks(Location center, int count) {
        for (int i = 0; i < count; i++) {
            Location spawnLoc = randomSpawnLocation(center, 8);
            if (spawnLoc != null) {
                spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.HUSK);
            }
        }
    }

    private void spawnDrowned(Location center, int count) {
        for (int i = 0; i < count; i++) {
            Location spawnLoc = randomSpawnLocation(center, 8);
            if (spawnLoc != null) {
                spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.DROWNED);
            }
        }
    }

    private void spawnVexes(Location center, int count) {
        for (int i = 0; i < count; i++) {
            Location spawnLoc = randomSpawnLocation(center, 6);
            if (spawnLoc != null) {
                Vex vex = (Vex) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VEX);
                // 设置恼鬼生命时间为30秒（600 ticks）
                vex.setLimitedLifetime(true);
                vex.setLimitedLifetimeTicks(600);
            }
        }
    }

    private void spawnIllusioners(Location center, int count) {
        for (int i = 0; i < count; i++) {
            Location spawnLoc = randomSpawnLocation(center, 6);
            if (spawnLoc != null) {
                spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ILLUSIONER);
            }
        }
    }

    private Location randomSpawnLocation(Location center, int radius) {
        for (int attempts = 0; attempts < 10; attempts++) {
            double angle = Math.random() * Math.PI * 2;
            double dist = Math.random() * radius;
            double x = center.getX() + Math.cos(angle) * dist;
            double z = center.getZ() + Math.sin(angle) * dist;
            int y = center.getWorld().getHighestBlockYAt((int) x, (int) z);
            Location loc = new Location(center.getWorld(), x, y, z);
            if (loc.getBlock().getType() == Material.AIR) {
                return loc;
            }
        }
        return null;
    }

    private boolean isDesertTempleChest(Block block) {
        return block.getType() == Material.CHEST && isInStructure(block.getWorld(), block.getLocation(), desertPyramid);
    }

    private boolean isInStructure(World world, Location loc, Structure structure) {
        if (world == null || structure == null) return false;
        try {
            return world.hasStructureAt(loc, structure);
        } catch (Exception e) {
            return false;
        }
    }
}
