package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        // 减少F3信息
        setupReducedDebugInfo();
        
        // 注册事件监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * 设置减少F3信息
     */
    private void setupReducedDebugInfo() {
        // 减少F3信息 - 使用GameRule
        if (plugin.getConfigManager().getWorldConfig().has("reduced_debug_info") && 
            plugin.getConfigManager().getWorldConfig().get("reduced_debug_info").getAsBoolean()) {
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            }
        }
    }
    
    /**
     * 监听生物生成事件 - 兔子替换牛和羊
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForReplacement(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 兔子替换牛和羊
        if (plugin.getConfigManager().getMobConfig("rabbit") != null && 
            plugin.getConfigManager().getMobConfig("rabbit").has("replace_cows_and_sheep") && 
            plugin.getConfigManager().getMobConfig("rabbit").get("replace_cows_and_sheep").getAsBoolean()) {
            
            if (event.getEntity() instanceof Cow || event.getEntity() instanceof Sheep) {
                // 取消原生成
                event.setCancelled(true);
                // 生成兔子
                event.getLocation().getWorld().spawn(event.getLocation(), Rabbit.class);
            }
        }
    }
    
    /**
     * 监听生物生成事件 - 结构守卫
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForStructureGuards(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 沙漠神殿尸壳守卫
        if (plugin.getConfigManager().isDesertTempleHusks()) {
            if (event.getEntity() instanceof Husk) {
                // 检查是否在沙漠神殿结构中
                if (isInStructure(event.getLocation(), "desert_pyramid")) {
                    // 增加生成数量
                    for (int i = 0; i < 3; i++) {
                        event.getLocation().getWorld().spawn(event.getLocation(), Husk.class);
                    }
                }
            }
        }
        
        // 沉船溺尸守卫
        if (plugin.getConfigManager().isShipwrecksGuardedByDrowned()) {
            if (event.getEntity() instanceof Drowned) {
                // 检查是否在沉船结构中
                if (isInStructure(event.getLocation(), "shipwreck")) {
                    // 增加生成数量
                    for (int i = 0; i < 3; i++) {
                        event.getLocation().getWorld().spawn(event.getLocation(), Drowned.class);
                    }
                }
            }
        }
        
        // 废弃传送门幻术师守卫
        if (plugin.getConfigManager().isRuinedPortalIllusioners()) {
            if (event.getEntity() instanceof Illusioner) {
                // 检查是否在废弃传送门结构中
                if (isInStructure(event.getLocation(), "ruined_portal")) {
                    // 增加生成数量
                    for (int i = 0; i < 2; i++) {
                        event.getLocation().getWorld().spawn(event.getLocation(), Illusioner.class);
                    }
                }
            }
        }
    }
    
    /**
     * 检查位置是否在指定结构中
     * 注意：这是一个简化的实现，实际可能需要更复杂的结构检测
     */
    private boolean isInStructure(org.bukkit.Location location, String structureName) {
        // 这里需要使用Paper的Registry API来检测结构
        // 由于API限制，这里使用简化的实现
        // 实际应用中应该使用 Structure.checkForGeneration() 或类似API
        return false;
    }
    
    /**
     * 监听实体攻击事件 - 骷髅自动瞄准
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForSkeletonAim(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 骷髅自动瞄准
        if (event.getDamager() instanceof Skeleton skeleton && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("skeleton").has("auto_aim") && 
                plugin.getConfigManager().getMobConfig("skeleton").get("auto_aim").getAsBoolean()) {
                // 计算预测位置
                org.bukkit.Location playerLoc = player.getLocation();
                org.bukkit.Location skeletonLoc = skeleton.getLocation();
                
                // 计算方向
                double dx = playerLoc.getX() - skeletonLoc.getX();
                double dy = playerLoc.getY() - skeletonLoc.getY();
                double dz = playerLoc.getZ() - skeletonLoc.getZ();
                
                // 设置骷髅朝向
                double yaw = Math.toDegrees(Math.atan2(-dx, dz));
                double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                skeletonLoc.setYaw((float) yaw);
                skeletonLoc.setPitch((float) pitch);
                skeleton.teleport(skeletonLoc);
            }
        }
        
        // 掠夺者自动瞄准
        if (event.getDamager() instanceof Pillager pillager && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("pillager").has("auto_aim") && 
                plugin.getConfigManager().getMobConfig("pillager").get("auto_aim").getAsBoolean()) {
                // 计算预测位置
                org.bukkit.Location playerLoc = player.getLocation();
                org.bukkit.Location pillagerLoc = pillager.getLocation();
                
                // 计算方向
                double dx = playerLoc.getX() - pillagerLoc.getX();
                double dy = playerLoc.getY() - pillagerLoc.getY();
                double dz = playerLoc.getZ() - pillagerLoc.getZ();
                
                // 设置掠夺者朝向
                double yaw = Math.toDegrees(Math.atan2(-dx, dz));
                double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                pillagerLoc.setYaw((float) yaw);
                pillagerLoc.setPitch((float) pitch);
                pillager.teleport(pillagerLoc);
            }
        }
        
        // 幻术师自动瞄准
        if (event.getDamager() instanceof Illusioner illusioner && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("illusioner").has("auto_aim") && 
                plugin.getConfigManager().getMobConfig("illusioner").get("auto_aim").getAsBoolean()) {
                // 计算预测位置
                org.bukkit.Location playerLoc = player.getLocation();
                org.bukkit.Location illusionerLoc = illusioner.getLocation();
                
                // 计算方向
                double dx = playerLoc.getX() - illusionerLoc.getX();
                double dy = playerLoc.getY() - illusionerLoc.getY();
                double dz = playerLoc.getZ() - illusionerLoc.getZ();
                
                // 设置幻术师朝向
                double yaw = Math.toDegrees(Math.atan2(-dx, dz));
                double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                illusionerLoc.setYaw((float) yaw);
                illusionerLoc.setPitch((float) pitch);
                illusioner.teleport(illusionerLoc);
            }
        }
    }
    
    /**
     * 监听实体跳跃事件 - 铁傀儡跳跃爆炸
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForIronGolemExplosion(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 铁傀儡跳跃爆炸
        if (event.getDamager() instanceof IronGolem golem && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("iron_golem").has("jump_explosion") && 
                plugin.getConfigManager().getMobConfig("iron_golem").get("jump_explosion").getAsBoolean()) {
                // 检查铁傀儡是否在跳跃
                if (golem.getVelocity().getY() > 0.1) {
                    // 生成爆炸
                    golem.getWorld().createExplosion(golem.getLocation(), 4.0F, true);
                }
            }
        }
    }
}
