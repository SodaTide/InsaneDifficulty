package xyz.mocoder.bravesurvival.fabric.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.IronGolemEntity;
import xyz.mocoder.bravesurvival.fabric.entity.FabricEntityWrapper;
import xyz.mocoder.bravesurvival.core.logic.mob.MobEnhancer;

/**
 * Fabric事件适配器实现
 */
public class FabricEventAdapter {
    
    /**
     * 注册所有事件
     */
    public static void registerEvents() {
        // 主要的怪物强化逻辑通过Mixin注入到各个实体类中
    }
    
    /**
     * 处理实体强化
     */
    public static void handleEntityEnhancement(LivingEntity entity) {
        FabricEntityWrapper wrapper = new FabricEntityWrapper(entity);
        
        if (entity instanceof ZombieEntity) {
            MobEnhancer.enhanceZombie(wrapper);
        } else if (entity instanceof CreeperEntity) {
            MobEnhancer.enhanceCreeper(wrapper);
        } else if (entity instanceof EndermanEntity) {
            MobEnhancer.enhanceEnderman(wrapper);
        } else if (entity instanceof SkeletonEntity) {
            MobEnhancer.enhanceSkeleton(wrapper);
        } else if (entity instanceof SpiderEntity) {
            MobEnhancer.enhanceSpider(wrapper);
        } else if (entity instanceof PiglinEntity) {
            MobEnhancer.enhancePiglin(wrapper);
        } else if (entity instanceof PhantomEntity) {
            MobEnhancer.enhancePhantom(wrapper);
        } else if (entity instanceof GuardianEntity) {
            MobEnhancer.enhanceGuardian(wrapper);
        } else if (entity instanceof HoglinEntity) {
            MobEnhancer.enhanceHoglin(wrapper);
        } else if (entity instanceof BlazeEntity) {
            MobEnhancer.enhanceBlaze(wrapper);
        } else if (entity instanceof GhastEntity) {
            MobEnhancer.enhanceGhast(wrapper);
        } else if (entity instanceof PillagerEntity) {
            MobEnhancer.enhancePillager(wrapper);
        } else if (entity instanceof WitchEntity) {
            MobEnhancer.enhanceWitch(wrapper);
        } else if (entity instanceof IllusionerEntity) {
            MobEnhancer.enhanceIllusioner(wrapper);
        } else if (entity instanceof WitherSkeletonEntity) {
            MobEnhancer.enhanceWitherSkeleton(wrapper);
        } else if (entity instanceof SilverfishEntity) {
            MobEnhancer.enhanceSilverfish(wrapper);
        } else if (entity instanceof EndermiteEntity) {
            MobEnhancer.enhanceEndermite(wrapper);
        } else if (entity instanceof IronGolemEntity) {
            MobEnhancer.handleIronGolemBehavior(wrapper);
        }
    }
}
