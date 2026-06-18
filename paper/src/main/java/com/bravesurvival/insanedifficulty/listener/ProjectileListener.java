package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileListener implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public ProjectileListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;
        Projectile projectile = event.getEntity();

        // 玩家射出的箭矢误射
        if (projectile instanceof Arrow arrow) {
            ProjectileSource shooter = arrow.getShooter();
            if (shooter instanceof Player) {
                plugin.getCombatManager().onPlayerArrowLaunch(arrow);
            }
        }

        // 末影之眼使用计数
        if (projectile instanceof EnderPearl) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
                plugin.getCombatManager().incrementEyeOfEnderUses(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.isCancelled()) return;
        Projectile projectile = event.getEntity();

        // 末影龙火球爆炸范围扩大
        if (projectile instanceof DragonFireball fireball) {
            // 创建更大的爆炸效果
            if (event.getHitBlock() != null || event.getHitEntity() != null) {
                org.bukkit.Location loc = fireball.getLocation();
                // 创建AreaEffectCloud（末影龙火球默认会创建，但我们可以增强它）
                AreaEffectCloud cloud = (AreaEffectCloud) loc.getWorld().spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);
                cloud.setRadius(4.0f);
                cloud.setRadiusPerTick(0.05f);
                cloud.setDuration(400);
                cloud.setWaitTime(5);
                cloud.setReapplicationDelay(10);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        // 末地爆炸辐射
        plugin.getCombatManager().onExplosionInEnd(event.getLocation());
    }
}
