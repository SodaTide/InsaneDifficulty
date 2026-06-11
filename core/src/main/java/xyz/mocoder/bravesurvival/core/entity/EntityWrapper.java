package xyz.mocoder.bravesurvival.core.entity;

/**
 * 实体包装器接口
 * 用于统一不同平台的实体属性设置
 */
public interface EntityWrapper {
    
    /**
     * 设置最大生命值
     */
    void setMaxHealth(double health);
    
    /**
     * 设置攻击力
     */
    void setAttackDamage(double damage);
    
    /**
     * 设置移动速度
     */
    void setMovementSpeed(double speed);
    
    /**
     * 设置追踪范围
     */
    void setFollowRange(double range);
    
    /**
     * 设置护甲值
     */
    void setArmor(double armor);
    
    /**
     * 设置击退抗性
     */
    void setKnockbackResistance(double resistance);
    
    /**
     * 添加状态效果
     */
    void addStatusEffect(String effect, int duration, int amplifier);
    
    /**
     * 检查是否在日光下
     */
    boolean isInDaylight();
    
    /**
     * 设置是否在日光下燃烧
     */
    void setBurnsInDaylight(boolean burns);
    
    /**
     * 获取实体类型
     */
    String getEntityType();
    
    /**
     * 获取世界对象
     */
    Object getWorld();
    
    /**
     * 获取X坐标
     */
    double getX();
    
    /**
     * 获取Y坐标
     */
    double getY();
    
    /**
     * 获取Z坐标
     */
    double getZ();
    
    /**
     * 设置位置
     */
    void setPosition(double x, double y, double z);
    
    /**
     * 生成实体
     */
    void spawnEntity(Object entity);
    
    /**
     * 设置是否为高压状态（苦力怕）
     */
    void setCharged(boolean charged);
    
    /**
     * 设置引信时间（苦力怕）
     */
    void setFuse(int fuse);
    
    /**
     * 设置爆炸半径（苦力怕）
     */
    void setExplosionRadius(int radius);
    
    /**
     * 设置增援概率（僵尸）
     */
    void setReinforcementChance(double chance);
    
    /**
     * 设置自动瞄准（骷髅）
     */
    void setAutoAim(boolean autoAim);
    
    /**
     * 设置力量弓（骷髅）
     */
    void setPowerBow(boolean powerBow);
    
    /**
     * 设置下界合金头盔（骷髅）
     */
    void setNetheriteHelmet(boolean netheriteHelmet);
    
    /**
     * 设置是否为隐形
     */
    void setInvisible(boolean invisible);
    
    /**
     * 设置是否为无敌
     */
    void setInvulnerable(boolean invulnerable);
    
    /**
     * 设置是否可以飞行
     */
    void setFlying(boolean flying);
    
    /**
     * 设置是否可以瞬移
     */
    void setCanTeleport(boolean canTeleport);
    
    /**
     * 设置是否破坏方块
     */
    void setCanDestroyBlocks(boolean canDestroyBlocks);
    
    /**
     * 设置是否发射火球
     */
    void setCanShootFireballs(boolean canShootFireballs);
    
    /**
     * 设置是否召唤随从
     */
    void setCanSummonMinions(boolean canSummonMinions);
    
    /**
     * 设置是否施放药水
     */
    void setCanThrowPotions(boolean canThrowPotions);
    
    /**
     * 设置是否给予凋灵效果
     */
    void setCanWither(boolean canWither);
    
    /**
     * 设置是否给予中毒效果
     */
    void setCanPoison(boolean canPoison);
    
    /**
     * 设置是否给予饥饿效果
     */
    void setCanHunger(boolean canHunger);
    
    /**
     * 设置是否给予缓慢效果
     */
    void setCanSlowness(boolean canSlowness);
    
    /**
     * 设置是否给予虚弱效果
     */
    void setCanWeakness(boolean canWeakness);
    
    /**
     * 设置是否给予反胃效果
     */
    void setCanNausea(boolean canNausea);
    
    /**
     * 设置是否给予失明效果
     */
    void setCanBlindness(boolean canBlindness);
    
    /**
     * 设置是否给予挖掘疲劳效果
     */
    void setCanMiningFatigue(boolean canMiningFatigue);
}
