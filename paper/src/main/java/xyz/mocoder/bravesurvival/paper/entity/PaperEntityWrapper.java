package xyz.mocoder.bravesurvival.paper.entity;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.mocoder.bravesurvival.core.entity.EntityWrapper;

/**
 * Paper实体包装器实现
 */
public class PaperEntityWrapper implements EntityWrapper {
    private final LivingEntity entity;
    
    public PaperEntityWrapper(LivingEntity entity) {
        this.entity = entity;
    }
    
    private AttributeInstance getAttribute(String key) {
        try {
            Attribute attr = Attribute.valueOf(key);
            return entity.getAttribute(attr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Override
    public void setMaxHealth(double health) {
        AttributeInstance attr = getAttribute("GENERIC_MAX_HEALTH");
        if (attr != null) {
            attr.setBaseValue(health);
        }
    }
    
    @Override
    public void setAttackDamage(double damage) {
        AttributeInstance attr = getAttribute("GENERIC_ATTACK_DAMAGE");
        if (attr != null) {
            attr.setBaseValue(damage);
        }
    }
    
    @Override
    public void setMovementSpeed(double speed) {
        AttributeInstance attr = getAttribute("GENERIC_MOVEMENT_SPEED");
        if (attr != null) {
            attr.setBaseValue(speed);
        }
    }
    
    @Override
    public void setFollowRange(double range) {
        AttributeInstance attr = getAttribute("GENERIC_FOLLOW_RANGE");
        if (attr != null) {
            attr.setBaseValue(range);
        }
    }
    
    @Override
    public void setArmor(double armor) {
        AttributeInstance attr = getAttribute("GENERIC_ARMOR");
        if (attr != null) {
            attr.setBaseValue(armor);
        }
    }
    
    @Override
    public void setKnockbackResistance(double resistance) {
        AttributeInstance attr = getAttribute("GENERIC_KNOCKBACK_RESISTANCE");
        if (attr != null) {
            attr.setBaseValue(resistance);
        }
    }
    
    @Override
    public void addStatusEffect(String effect, int duration, int amplifier) {
        PotionEffectType effectType = switch (effect) {
            case "WEAKNESS" -> PotionEffectType.WEAKNESS;
            case "SLOWNESS" -> PotionEffectType.SLOWNESS;
            case "INVISIBILITY" -> PotionEffectType.INVISIBILITY;
            case "FIRE_RESISTANCE" -> PotionEffectType.FIRE_RESISTANCE;
            case "POISON" -> PotionEffectType.POISON;
            case "HUNGER" -> PotionEffectType.HUNGER;
            case "NAUSEA" -> PotionEffectType.NAUSEA;
            case "BLINDNESS" -> PotionEffectType.BLINDNESS;
            case "MINING_FATIGUE" -> PotionEffectType.MINING_FATIGUE;
            case "WITHER" -> PotionEffectType.WITHER;
            default -> null;
        };
        
        if (effectType != null) {
            entity.addPotionEffect(new PotionEffect(effectType, duration, amplifier, false, false));
        }
    }
    
    @Override
    public boolean isInDaylight() {
        return entity.getWorld().isDayTime() && 
               !entity.getWorld().hasStorm() && 
               entity.getLocation().getY() > entity.getWorld().getHighestBlockYAt(entity.getLocation());
    }
    
    @Override
    public void setBurnsInDaylight(boolean burns) {
        // 需要在事件监听器中实现
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
        return entity.getLocation().getX();
    }
    
    @Override
    public double getY() {
        return entity.getLocation().getY();
    }
    
    @Override
    public double getZ() {
        return entity.getLocation().getZ();
    }
    
    @Override
    public void setPosition(double x, double y, double z) {
        entity.teleport(entity.getLocation().clone().add(x - getX(), y - getY(), z - getZ()));
    }
    
    @Override
    public void spawnEntity(Object entity) {
        if (entity instanceof LivingEntity livingEntity) {
            this.entity.getWorld().spawnEntity(this.entity.getLocation(), livingEntity.getType());
        }
    }
    
    @Override
    public void setCharged(boolean charged) {
        // 苦力怕高压状态需要在事件监听器中实现
    }
    
    @Override
    public void setFuse(int fuse) {
        // 苦力怕引信时间需要在事件监听器中实现
    }
    
    @Override
    public void setExplosionRadius(int radius) {
        // 苦力怕爆炸半径需要在事件监听器中实现
    }
    
    @Override
    public void setReinforcementChance(double chance) {
        // 僵尸增援概率需要在事件监听器中实现
    }
    
    @Override
    public void setAutoAim(boolean autoAim) {
        // 骷髅自动瞄准需要在事件监听器中实现
    }
    
    @Override
    public void setPowerBow(boolean powerBow) {
        // 骷髅力量弓需要在事件监听器中实现
    }
    
    @Override
    public void setNetheriteHelmet(boolean netheriteHelmet) {
        // 骷髅下界合金头盔需要在事件监听器中实现
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
        // 飞行状态需要在事件监听器中实现
    }
    
    @Override
    public void setCanTeleport(boolean canTeleport) {
        // 传送能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanDestroyBlocks(boolean canDestroyBlocks) {
        // 破坏方块能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanShootFireballs(boolean canShootFireballs) {
        // 发射火球能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanSummonMinions(boolean canSummonMinions) {
        // 召唤随从能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanThrowPotions(boolean canThrowPotions) {
        // 施放药水能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanWither(boolean canWither) {
        // 凋灵效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanPoison(boolean canPoison) {
        // 中毒效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanHunger(boolean canHunger) {
        // 饥饿效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanSlowness(boolean canSlowness) {
        // 缓慢效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanWeakness(boolean canWeakness) {
        // 虚弱效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanNausea(boolean canNausea) {
        // 反胃效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanBlindness(boolean canBlindness) {
        // 失明效果能力需要在事件监听器中实现
    }
    
    @Override
    public void setCanMiningFatigue(boolean canMiningFatigue) {
        // 挖掘疲劳效果能力需要在事件监听器中实现
    }
    
    public LivingEntity getEntity() {
        return entity;
    }
}
