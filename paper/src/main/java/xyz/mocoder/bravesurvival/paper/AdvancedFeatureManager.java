package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * 高级功能管理器
 * 负责实现需要特殊API的功能
 */
public class AdvancedFeatureManager implements Listener {

    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();

    public AdvancedFeatureManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 初始化高级功能
     */
    public void initialize() {
        setupReducedDebugInfo();
        setupForcedHardDifficulty();
        setupDeathMessageHiding();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void setupReducedDebugInfo() {
        if (plugin.getConfigManager().getWorldConfig().has("reduced_debug_info") &&
            plugin.getConfigManager().getWorldConfig().get("reduced_debug_info").getAsBoolean()) {
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            }
        }
    }

    /**
     * 强制困难难度
     */
    private void setupForcedHardDifficulty() {
        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(org.bukkit.Difficulty.HARD);
        }
    }

    /**
     * 死亡信息隐藏
     */
    private void setupDeathMessageHiding() {
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        }
    }

    // ==================== 结构检测 ====================

    /**
     * 使用Paper API检测结构
     */
    private boolean isInStructure(Location location, String structureName) {
        try {
            // Paper 1.21+ 使用 World.locateNearestStructure
            // 先尝试使用Registry API
            try {
                var structureKey = org.bukkit.Registry.STRUCTURE_TYPE.get(
                    org.bukkit.NamespacedKey.minecraft(structureName)
                );
                if (structureKey != null) {
                    var result = location.getWorld().locateNearestStructure(location, structureKey, 100, false);
                    if (result != null) {
                        double distance = location.distance(result.getLocation());
                        return distance < 30;
                    }
                }
            } catch (Exception e1) {
                // Registry API不可用，使用方块检测
            }

            // 回退：使用简单的方块检测
            return isInStructureByBlocks(location, structureName);
        } catch (Exception e) {
            return isInStructureByBlocks(location, structureName);
        }
    }

    /**
     * 通过方块模式检测结构（回退方案）
     */
    private boolean isInStructureByBlocks(Location location, String structureName) {
        switch (structureName) {
            case "desert_pyramid":
                return detectDesertPyramid(location);
            case "shipwreck":
                return detectShipwreck(location);
            case "ruined_portal":
                return detectRuinedPortal(location);
            case "jungle_pyramid":
                return detectJunglePyramid(location);
            case "buried_treasure":
                return detectBuriedTreasure(location);
            default:
                return false;
        }
    }

    private boolean detectDesertPyramid(Location loc) {
        // 沙漠神殿特征：砂岩+蓝色陶瓦
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                Block block = loc.getBlock().getRelative(x, 0, z);
                if (block.getType() == Material.BLUE_TERRACOTTA ||
                    block.getType() == Material.ORANGE_TERRACOTTA ||
                    block.getType() == Material.CHISELED_SANDSTONE) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean detectShipwreck(Location loc) {
        // 沉船特征：木板+木头
        for (int x = -20; x <= 20; x++) {
            for (int z = -20; z <= 20; z++) {
                for (int y = -5; y <= 5; y++) {
                    Block block = loc.getBlock().getRelative(x, y, z);
                    if (block.getType() == Material.OAK_PLANKS ||
                        block.getType() == Material.SPRUCE_PLANKS) {
                        // 检查附近是否有水
                        if (loc.getBlock().getRelative(x, y + 1, z).getType() == Material.WATER) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean detectRuinedPortal(Location loc) {
        // 废弃传送门特征：黑曜石+哭泣的黑曜石
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                for (int y = -5; y <= 5; y++) {
                    Block block = loc.getBlock().getRelative(x, y, z);
                    if (block.getType() == Material.CRYING_OBSIDIAN) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean detectJunglePyramid(Location loc) {
        // 丛林神殿特征：苔石砖+石头
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                for (int y = -10; y <= 10; y++) {
                    Block block = loc.getBlock().getRelative(x, y, z);
                    if (block.getType() == Material.MOSSY_STONE_BRICKS ||
                        block.getType() == Material.CHISELED_STONE_BRICKS) {
                        // 检查是否在丛林生物群系
                        if (loc.getBlock().getBiome().name().contains("JUNGLE")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean detectBuriedTreasure(Location loc) {
        // 埋藏宝藏特征：箱子在沙子/砂砾下面
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = -3; y <= 3; y++) {
                    Block block = loc.getBlock().getRelative(x, y, z);
                    if (block.getType() == Material.CHEST) {
                        Block above = block.getRelative(0, 1, 0);
                        if (above.getType() == Material.SAND || above.getType() == Material.GRAVEL) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // ==================== 结构守卫（修复版）====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveForStructureGuards(org.bukkit.event.player.PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Player player = event.getPlayer();
        Location loc = player.getLocation();

        // 沙漠神殿尸壳守卫
        if (isInStructure(loc, "desert_pyramid")) {
            spawnDesertTempleGuards(loc, player);
        }

        // 沉船溺尸守卫
        if (isInStructure(loc, "shipwreck")) {
            spawnShipwreckGuards(loc, player);
        }

        // 废弃传送门幻术师守卫
        if (isInStructure(loc, "ruined_portal")) {
            spawnRuinedPortalGuards(loc, player);
        }

        // 丛林神殿幻术师守卫
        if (isInStructure(loc, "jungle_pyramid")) {
            spawnJunglePyramidGuards(loc, player);
        }
    }

    private boolean hasGuardSpawned(Location loc, String tag) {
        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 50, 50, 50)) {
            if (entity.getScoreboardTags().contains(tag)) {
                return true;
            }
        }
        return false;
    }

    private void spawnDesertTempleGuards(Location loc, Player player) {
        if (hasGuardSpawned(loc, "guard_desert_temple")) return;

        // 生成12只尸壳
        for (int i = 0; i < 12; i++) {
            Location spawnLoc = loc.clone().add(
                random.nextInt(16) - 8,
                0,
                random.nextInt(16) - 8
            );
            spawnLoc.setY(loc.getWorld().getHighestBlockYAt(spawnLoc));
            Husk husk = loc.getWorld().spawn(spawnLoc, Husk.class);
            husk.addScoreboardTag("guard_desert_temple");
            husk.setTarget(player);
        }

        // 标记已生成
        AreaEffectCloud marker = loc.getWorld().spawn(loc, AreaEffectCloud.class);
        marker.addScoreboardTag("guard_desert_temple");
        marker.setDuration(Integer.MAX_VALUE);
    }

    private void spawnShipwreckGuards(Location loc, Player player) {
        if (hasGuardSpawned(loc, "guard_shipwreck")) return;

        // 生成8只溺尸
        for (int i = 0; i < 8; i++) {
            Location spawnLoc = loc.clone().add(
                random.nextInt(16) - 8,
                0,
                random.nextInt(16) - 8
            );
            spawnLoc.setY(loc.getWorld().getHighestBlockYAt(spawnLoc));
            Drowned drowned = loc.getWorld().spawn(spawnLoc, Drowned.class);
            drowned.addScoreboardTag("guard_shipwreck");
            drowned.setTarget(player);
        }

        AreaEffectCloud marker = loc.getWorld().spawn(loc, AreaEffectCloud.class);
        marker.addScoreboardTag("guard_shipwreck");
        marker.setDuration(Integer.MAX_VALUE);
    }

    private void spawnRuinedPortalGuards(Location loc, Player player) {
        if (hasGuardSpawned(loc, "guard_ruined_portal")) return;

        // 生成4只幻术师
        for (int i = 0; i < 4; i++) {
            Location spawnLoc = loc.clone().add(
                random.nextInt(10) - 5,
                0,
                random.nextInt(10) - 5
            );
            spawnLoc.setY(loc.getWorld().getHighestBlockYAt(spawnLoc));
            Illusioner illusioner = loc.getWorld().spawn(spawnLoc, Illusioner.class);
            illusioner.addScoreboardTag("guard_ruined_portal");
            illusioner.setTarget(player);
        }

        AreaEffectCloud marker = loc.getWorld().spawn(loc, AreaEffectCloud.class);
        marker.addScoreboardTag("guard_ruined_portal");
        marker.setDuration(Integer.MAX_VALUE);
    }

    private void spawnJunglePyramidGuards(Location loc, Player player) {
        if (hasGuardSpawned(loc, "guard_jungle_pyramid")) return;

        // 生成4只幻术师
        for (int i = 0; i < 4; i++) {
            Location spawnLoc = loc.clone().add(
                random.nextInt(10) - 5,
                0,
                random.nextInt(10) - 5
            );
            spawnLoc.setY(loc.getWorld().getHighestBlockYAt(spawnLoc));
            Illusioner illusioner = loc.getWorld().spawn(spawnLoc, Illusioner.class);
            illusioner.addScoreboardTag("guard_jungle_pyramid");
            illusioner.setTarget(player);
        }

        AreaEffectCloud marker = loc.getWorld().spawn(loc, AreaEffectCloud.class);
        marker.addScoreboardTag("guard_jungle_pyramid");
        marker.setDuration(Integer.MAX_VALUE);
    }

    // ==================== 沙漠神殿宝箱TNT陷阱 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractForDesertTempleTNT(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        // 检查是否是箱子
        if (block.getType() == Material.CHEST) {
            Location loc = block.getLocation();
            // 检查是否在沙漠神殿中
            if (isInStructure(loc, "desert_pyramid")) {
                // 在箱子下方生成点燃的TNT
                Location tntLoc = loc.clone().add(0, -1.1, 0);
                TNTPrimed tnt = loc.getWorld().spawn(tntLoc, TNTPrimed.class);
                tnt.setFuseTicks(100); // 5秒引信
                loc.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
            }
        }
    }

    // ==================== 埋藏宝藏守卫 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForBuriedTreasure(org.bukkit.event.block.BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Block block = event.getBlock();
        if (block.getType() == Material.CHEST) {
            Location loc = block.getLocation();
            if (isInStructure(loc, "buried_treasure")) {
                if (!hasGuardSpawned(loc, "guard_buried_treasure")) {
                    // 生成4只恼鬼
                    for (int i = 0; i < 4; i++) {
                        Location spawnLoc = loc.clone().add(
                            random.nextInt(10) - 5,
                            2,
                            random.nextInt(10) - 5
                        );
                        Vex vex = loc.getWorld().spawn(spawnLoc, Vex.class);
                        vex.addScoreboardTag("guard_buried_treasure");
                        vex.setTarget(event.getPlayer());
                    }

                    AreaEffectCloud marker = loc.getWorld().spawn(loc, AreaEffectCloud.class);
                    marker.addScoreboardTag("guard_buried_treasure");
                    marker.setDuration(Integer.MAX_VALUE);
                }
            }
        }
    }

    // ==================== 兔子替换牛和羊 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForReplacement(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (plugin.getConfigManager().getMobConfig("rabbit") != null &&
            plugin.getConfigManager().getMobConfig("rabbit").has("replace_cows_and_sheep") &&
            plugin.getConfigManager().getMobConfig("rabbit").get("replace_cows_and_sheep").getAsBoolean()) {

            if (event.getEntity() instanceof Cow || event.getEntity() instanceof Sheep) {
                event.setCancelled(true);
                event.getLocation().getWorld().spawn(event.getLocation(), Rabbit.class);
            }
        }
    }

    // ==================== 骷髅自动瞄准 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForSkeletonAim(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof Skeleton skeleton && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("skeleton").has("auto_aim") &&
                plugin.getConfigManager().getMobConfig("skeleton").get("auto_aim").getAsBoolean()) {
                final Location playerLoc = player.getLocation();
                final Location skeletonLoc = skeleton.getLocation();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        double dx = playerLoc.getX() - skeletonLoc.getX();
                        double dy = playerLoc.getY() - skeletonLoc.getY();
                        double dz = playerLoc.getZ() - skeletonLoc.getZ();
                        double yaw = Math.toDegrees(Math.atan2(-dx, dz));
                        double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                        skeletonLoc.setYaw((float) yaw);
                        skeletonLoc.setPitch((float) pitch);
                        skeleton.teleport(skeletonLoc);
                    } catch (Exception e) {}
                });
            }
        }

        if (event.getDamager() instanceof Pillager pillager && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("pillager").has("auto_aim") &&
                plugin.getConfigManager().getMobConfig("pillager").get("auto_aim").getAsBoolean()) {
                final Location playerLoc = player.getLocation();
                final Location pillagerLoc = pillager.getLocation();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        double dx = playerLoc.getX() - pillagerLoc.getX();
                        double dy = playerLoc.getY() - pillagerLoc.getY();
                        double dz = playerLoc.getZ() - pillagerLoc.getZ();
                        double yaw = Math.toDegrees(Math.atan2(-dx, dz));
                        double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                        pillagerLoc.setYaw((float) yaw);
                        pillagerLoc.setPitch((float) pitch);
                        pillager.teleport(pillagerLoc);
                    } catch (Exception e) {}
                });
            }
        }

        if (event.getDamager() instanceof Illusioner illusioner && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("illusioner").has("auto_aim") &&
                plugin.getConfigManager().getMobConfig("illusioner").get("auto_aim").getAsBoolean()) {
                final Location playerLoc = player.getLocation();
                final Location illusionerLoc = illusioner.getLocation();

                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        double dx = playerLoc.getX() - illusionerLoc.getX();
                        double dy = playerLoc.getY() - illusionerLoc.getY();
                        double dz = playerLoc.getZ() - illusionerLoc.getZ();
                        double yaw = Math.toDegrees(Math.atan2(-dx, dz));
                        double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                        illusionerLoc.setYaw((float) yaw);
                        illusionerLoc.setPitch((float) pitch);
                        illusioner.teleport(illusionerLoc);
                    } catch (Exception e) {}
                });
            }
        }
    }

    // ==================== 铁傀儡跳跃爆炸 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForIronGolemExplosion(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof IronGolem golem && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("iron_golem").has("jump_explosion") &&
                plugin.getConfigManager().getMobConfig("iron_golem").get("jump_explosion").getAsBoolean()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        if (golem.getVelocity().getY() > 0.1) {
                            golem.getWorld().createExplosion(golem.getLocation(), 4.0F, true);
                        }
                    } catch (Exception e) {}
                });
            }
        }
    }
}
