package xyz.mocoder.bravesurvival.fabric.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import xyz.mocoder.bravesurvival.core.entity.EntityWrapper;

/**
 * Fabric实体包装器实现
 */
public class FabricEntityWrapper implements EntityWrapper {
    private final LivingEntity entity;
    
    public FabricEntityWrapper(LivingEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public void setMaxHealth(double health) {
        entity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(health);
    }
    
    @Override
    public void setAttackDamage(double damage) {
        entity.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(damage);
    }
    
    @Override
    public void setMovementSpeed(double speed) {
        entity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(speed);
    }
    
    @Override
    public void setFollowRange(double range) {
        entity.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(range);
    }
    
    @Override
    public void setArmor(double armor) {
        entity.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(armor);
    }
    
    @Override
    public void setKnockbackResistance(double resistance) {
        entity.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(resistance);
    }
    
    @Override
    public void addStatusEffect(String effect, int duration, int amplifier) {
        StatusEffectInstance statusEffect = switch (effect) {
            case "WEAKNESS" -> new StatusEffectInstance(StatusEffects.WEAKNESS, duration, amplifier, false, false);
            case "SLOWNESS" -> new StatusEffectInstance(StatusEffects.SLOWNESS, duration, amplifier, false, false);
            case "INVISIBILITY" -> new StatusEffectInstance(StatusEffects.INVISIBILITY, duration, amplifier, false, false);
            case "FIRE_RESISTANCE" -> new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, duration, amplifier, false, false);
            case "POISON" -> new StatusEffectInstance(StatusEffects.POISON, duration, amplifier, false, false);
            case "HUNGER" -> new StatusEffectInstance(StatusEffects.HUNGER, duration, amplifier, false, false);
            case "NAUSEA" -> new StatusEffectInstance(StatusEffects.NAUSEA, duration, amplifier, false, false);
            case "BLINDNESS" -> new StatusEffectInstance(StatusEffects.BLINDNESS, duration, amplifier, false, false);
            case "MINING_FATIGUE" -> new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration, amplifier, false, false);
            case "WITHER" -> new StatusEffectInstance(StatusEffects.WITHER, duration, amplifier, false, false);
            default -> null;
        };
        
        if (statusEffect != null) {
            entity.addStatusEffect(statusEffect);
        }
    }
    
    @Override
    public boolean isInDaylight() {
        World world = entity.getWorld();
        return world.isDay() && !world.isRaining() && 
               world.isSkyVisible(entity.getBlockPos());
    }
    
    @Override
    public void setBurnsInDaylight(boolean burns) {
        // 这个需要在Mixin中实现
    }
    
    @Override
    public String getEntityType() {
        return entity.getType().toString();
    }
    
    @Override
    public Object getWorld() {
        return entity.getWorld();
    }
    
    @Override
    public double getX() {
        return entity.getX();
    }
    
    @Override
    public double getY() {
        return entity.getY();
    }
    
    @Override
    public double getZ() {
        return entity.getZ();
    }
    
    @Override
    public void setPosition(double x, double y, double z) {
        entity.setPosition(x, y, z);
    }
    
    @Override
    public void spawnEntity(Object entity) {
        if (this.entity.getWorld() instanceof ServerWorld serverWorld && entity instanceof LivingEntity livingEntity) {
            serverWorld.spawnEntity(livingEntity);
        }
    }
    
    @Override
    public void setCharged(boolean charged) {
        // 苦力怕高压状态需要在Mixin中实现
    }
    
    @Override
    public void setFuse(int fuse) {
        // 苦力怕引信时间需要在Mixin中实现
    }
    
    @Override
    public void setExplosionRadius(int radius) {
        // 苦力怕爆炸半径需要在Mixin中实现
    }
    
    @Override
    public void setReinforcementChance(double chance) {
        // 僵尸增援概率需要在Mixin中实现
    }
    
    @Override
    public void setAutoAim(boolean autoAim) {
        // 骷髅自动瞄准需要在Mixin中实现
    }
    
    @Override
    public void setPowerBow(boolean powerBow) {
        // 骷髅力量弓需要在Mixin中实现
    }
    
    @Override
    public void setNetheriteHelmet(boolean netheriteHelmet) {
        // 骷髅下界合金头盔需要在Mixin中实现
    }
    
    @Override
    public void setInvisible(boolean invisible) {
        entity.setInvisible(invisible);
    }
    
    @Override
    public void setInvulnerable(boolean invulnerable) {
        entity.setInvulnerable(invulnerable);
    }
    
    @Override
    public void setFlying(boolean flying) {
        // 飞行状态需要在Mixin中实现
    }
    
    @Override
    public void setCanTeleport(boolean canTeleport) {
        // 传送能力需要在Mixin中实现
    }
    
    @Override
    public void setCanDestroyBlocks(boolean canDestroyBlocks) {
        // 破坏方块能力需要在Mixin中实现
    }
    
    @Override
    public void setCanShootFireballs(boolean canShootFireballs) {
        // 发射火球能力需要在Mixin中实现
    }
    
    @Override
    public void setCanSummonMinions(boolean canSummonMinions) {
        // 召唤随从能力需要在Mixin中实现
    }
    
    @Override
    public void setCanThrowPotions(boolean canThrowPotions) {
        // 施放药水能力需要在Mixin中实现
    }
    
    @Override
    public void setCanWither(boolean canWither) {
        // 凋灵效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanPoison(boolean canPoison) {
        // 中毒效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanHunger(boolean canHunger) {
        // 饥饿效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanSlowness(boolean canSlowness) {
        // 缓慢效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanWeakness(boolean canWeakness) {
        // 虚弱效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanNausea(boolean canNausea) {
        // 反胃效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanBlindness(boolean canBlindness) {
        // 失明效果能力需要在Mixin中实现
    }
    
    @Override
    public void setCanMiningFatigue(boolean canMiningFatigue) {
        // 挖掘疲劳效果能力需要在Mixin中实现
    }
    
    public LivingEntity getEntity() {
        return entity;
    }
}
