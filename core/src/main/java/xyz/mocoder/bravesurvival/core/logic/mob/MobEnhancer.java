package xyz.mocoder.bravesurvival.core.logic.mob;

import xyz.mocoder.bravesurvival.core.config.ConfigManager;
import xyz.mocoder.bravesurvival.core.entity.EntityWrapper;

/**
 * 怪物强化器
 * 负责应用怪物强化逻辑
 */
public class MobEnhancer {
    
    /**
     * 强化僵尸
     */
    public static void enhanceZombie(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("zombie")) {
            return;
        }
        
        // 设置属性
        entity.setMaxHealth(ConfigManager.getMobAttribute("zombie", "health", 20.0));
        entity.setAttackDamage(ConfigManager.getMobAttribute("zombie", "damage", 5.0));
        entity.setMovementSpeed(ConfigManager.getMobAttribute("zombie", "speed", 0.23));
        entity.setFollowRange(ConfigManager.getMobAttribute("zombie", "follow_range", 64.0));
        entity.setArmor(ConfigManager.getMobAttribute("zombie", "armor", 0.0));
        
        // 设置不在日光下燃烧
        if (!ConfigManager.getMobConfig("zombie").has("burn_in_daylight") || 
            !ConfigManager.getMobConfig("zombie").get("burn_in_daylight").getAsBoolean()) {
            entity.setBurnsInDaylight(false);
        }
        
        // 设置火焰抗性
        if (ConfigManager.getMobConfig("zombie").has("fire_resistance") && 
            ConfigManager.getMobConfig("zombie").get("fire_resistance").getAsBoolean()) {
            entity.addStatusEffect("FIRE_RESISTANCE", 1000000, 0);
        }
        
        // 设置增援概率
        if (ConfigManager.getMobConfig("zombie").has("spawn_reinforcements")) {
            entity.setReinforcementChance(ConfigManager.getMobConfig("zombie").get("spawn_reinforcements").getAsDouble());
        }
    }
    
    /**
     * 强化苦力怕
     */
    public static void enhanceCreeper(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("creeper")) {
            return;
        }
        
        // 设置为高压状态
        if (ConfigManager.getMobConfig("creeper").has("always_charged") && 
            ConfigManager.getMobConfig("creeper").get("always_charged").getAsBoolean()) {
            entity.setCharged(true);
        }
        
        // 设置瞬间爆炸
        if (ConfigManager.getMobConfig("creeper").has("instant_fuse") && 
            ConfigManager.getMobConfig("creeper").get("instant_fuse").getAsBoolean()) {
            entity.setFuse(1);
        }
        
        // 设置隐形
        if (ConfigManager.getMobConfig("creeper").has("invisible") && 
            ConfigManager.getMobConfig("creeper").get("invisible").getAsBoolean()) {
            entity.addStatusEffect("INVISIBILITY", 1000000, 62);
        }
        
        // 设置移动速度
        if (ConfigManager.getMobConfig("creeper").has("speed")) {
            entity.setMovementSpeed(ConfigManager.getMobConfig("creeper").get("speed").getAsDouble());
        }
        
        // 设置爆炸半径
        if (ConfigManager.getMobConfig("creeper").has("explosion_radius")) {
            entity.setExplosionRadius(ConfigManager.getMobConfig("creeper").get("explosion_radius").getAsInt());
        }
    }
    
    /**
     * 强化末影人
     */
    public static void enhanceEnderman(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("enderman")) {
            return;
        }
        
        // 设置属性
        entity.setMaxHealth(ConfigManager.getMobAttribute("enderman", "health", 40.0));
        entity.setAttackDamage(ConfigManager.getMobAttribute("enderman", "damage", 7.0));
        entity.setMovementSpeed(ConfigManager.getMobAttribute("enderman", "speed", 0.3));
        entity.setFollowRange(ConfigManager.getMobAttribute("enderman", "follow_range", 64.0));
    }
    
    /**
     * 强化骷髅
     */
    public static void enhanceSkeleton(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("skeleton")) {
            return;
        }
        
        // 设置属性
        entity.setAttackDamage(ConfigManager.getMobAttribute("skeleton", "damage", 10.0));
        
        // 设置不在日光下燃烧
        if (!ConfigManager.getMobConfig("skeleton").has("burn_in_daylight") || 
            !ConfigManager.getMobConfig("skeleton").get("burn_in_daylight").getAsBoolean()) {
            entity.setBurnsInDaylight(false);
        }
        
        // 设置自动瞄准
        if (ConfigManager.getMobConfig("skeleton").has("auto_aim") && 
            ConfigManager.getMobConfig("skeleton").get("auto_aim").getAsBoolean()) {
            entity.setAutoAim(true);
        }
        
        // 设置力量弓
        if (ConfigManager.getMobConfig("skeleton").has("power_bow") && 
            ConfigManager.getMobConfig("skeleton").get("power_bow").getAsBoolean()) {
            entity.setPowerBow(true);
        }
        
        // 设置下界合金头盔
        if (ConfigManager.getMobConfig("skeleton").has("netherite_helmet") && 
            ConfigManager.getMobConfig("skeleton").get("netherite_helmet").getAsBoolean()) {
            entity.setNetheriteHelmet(true);
        }
    }
    
    /**
     * 强化蜘蛛
     */
    public static void enhanceSpider(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("spider")) {
            return;
        }
        
        // 设置隐形
        if (ConfigManager.getMobConfig("spider").has("invisible") && 
            ConfigManager.getMobConfig("spider").get("invisible").getAsBoolean()) {
            entity.addStatusEffect("INVISIBILITY", 1000000, 62);
        }
        
        // 设置速度
        if (ConfigManager.getMobConfig("spider").has("speed")) {
            entity.setMovementSpeed(ConfigManager.getMobConfig("spider").get("speed").getAsDouble());
        }
    }
    
    /**
     * 强化猪灵
     */
    public static void enhancePiglin(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("piglin")) {
            return;
        }
        
        // 设置属性
        entity.setMaxHealth(ConfigManager.getMobAttribute("piglin", "health", 16.0));
        entity.setAttackDamage(ConfigManager.getMobAttribute("piglin", "damage", 5.0));
        entity.setMovementSpeed(ConfigManager.getMobAttribute("piglin", "speed", 0.3));
    }
    
    /**
     * 强化幻翼
     */
    public static void enhancePhantom(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("phantom")) {
            return;
        }
        
        // 设置不在日光下燃烧
        if (!ConfigManager.getMobConfig("phantom").has("burn_in_daylight") || 
            !ConfigManager.getMobConfig("phantom").get("burn_in_daylight").getAsBoolean()) {
            entity.setBurnsInDaylight(false);
        }
        
        // 设置伤害
        if (ConfigManager.getMobConfig("phantom").has("damage")) {
            entity.setAttackDamage(ConfigManager.getMobConfig("phantom").get("damage").getAsDouble());
        }
    }
    
    /**
     * 强化守卫者
     */
    public static void enhanceGuardian(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("guardian")) {
            return;
        }
        
        // 设置属性
        entity.setMaxHealth(ConfigManager.getMobAttribute("guardian", "health", 30.0));
        entity.setAttackDamage(ConfigManager.getMobAttribute("guardian", "damage", 6.0));
        entity.setMovementSpeed(ConfigManager.getMobAttribute("guardian", "speed", 0.5));
        entity.setFollowRange(ConfigManager.getMobAttribute("guardian", "follow_range", 16.0));
    }
    
    /**
     * 强化疣猪兽
     */
    public static void enhanceHoglin(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("hoglin")) {
            return;
        }
        
        // 设置属性
        entity.setMaxHealth(ConfigManager.getMobAttribute("hoglin", "health", 40.0));
        entity.setAttackDamage(ConfigManager.getMobAttribute("hoglin", "damage", 6.0));
        entity.setMovementSpeed(ConfigManager.getMobAttribute("hoglin", "speed", 0.3));
        entity.setKnockbackResistance(ConfigManager.getMobAttribute("hoglin", "knockback_resistance", 0.6));
    }
    
    /**
     * 强化烈焰人
     */
    public static void enhanceBlaze(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("blaze")) {
            return;
        }
        
        // 设置速度
        if (ConfigManager.getMobConfig("blaze").has("increased_speed") && 
            ConfigManager.getMobConfig("blaze").get("increased_speed").getAsBoolean()) {
            entity.setMovementSpeed(0.3);
        }
    }
    
    /**
     * 强化恶魂
     */
    public static void enhanceGhast(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("ghast")) {
            return;
        }
        
        // 设置隐形
        if (ConfigManager.getMobConfig("ghast").has("invisible") && 
            ConfigManager.getMobConfig("ghast").get("invisible").getAsBoolean()) {
            entity.addStatusEffect("INVISIBILITY", 1000000, 62);
        }
    }
    
    /**
     * 强化卫道士
     */
    public static void enhancePillager(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("pillager")) {
            return;
        }
        
        // 快速装填5、多重射击在装备生成时处理
    }
    
    /**
     * 强化女巫
     */
    public static void enhanceWitch(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("witch")) {
            return;
        }
        
        // 凋灵骷髅和喷溅药水在行为中处理
    }
    
    /**
     * 强化幻术师
     */
    public static void enhanceIllusioner(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("illusioner")) {
            return;
        }
        
        // 自动瞄准和力量弓在行为中处理
    }
    
    /**
     * 强化凋灵骷髅
     */
    public static void enhanceWitherSkeleton(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("wither_skeleton")) {
            return;
        }
        
        // 凋灵效果在攻击时处理
    }
    
    /**
     * 强化蠹虫
     */
    public static void enhanceSilverfish(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("silverfish")) {
            return;
        }
        
        // 设置击退抗性
        if (ConfigManager.getMobConfig("silverfish").has("knockback_resistant") && 
            ConfigManager.getMobConfig("silverfish").get("knockback_resistant").getAsBoolean()) {
            entity.setKnockbackResistance(1.0);
        }
        
        // 设置速度
        if (ConfigManager.getMobConfig("silverfish").has("speed")) {
            entity.setMovementSpeed(ConfigManager.getMobConfig("silverfish").get("speed").getAsDouble());
        }
    }
    
    /**
     * 强化末影螨
     */
    public static void enhanceEndermite(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("endermite")) {
            return;
        }
        
        // 设置击退抗性
        if (ConfigManager.getMobConfig("endermite").has("knockback_resistant") && 
            ConfigManager.getMobConfig("endermite").get("knockback_resistant").getAsBoolean()) {
            entity.setKnockbackResistance(1.0);
        }
        
        // 设置速度
        if (ConfigManager.getMobConfig("endermite").has("speed")) {
            entity.setMovementSpeed(ConfigManager.getMobConfig("endermite").get("speed").getAsDouble());
        }
    }
    
    /**
     * 铁傀儡攻击玩家
     */
    public static void handleIronGolemBehavior(EntityWrapper entity) {
        if (!ConfigManager.isMobEnabled("iron_golem")) {
            return;
        }
        
        // 铁傀儡攻击最近的玩家
        // 具体实现在平台适配器中
    }
    
    /**
     * 检查是否应该生成强化装备
     */
    public static boolean shouldGenerateEnhancedArmor(String mobType) {
        return ConfigManager.getMobConfig(mobType).has("enhanced_armor") && 
               ConfigManager.getMobConfig(mobType).get("enhanced_armor").getAsBoolean();
    }
    
    /**
     * 检查是否应该生成强化附魔
     */
    public static boolean shouldGenerateEnhancedEnchantments(String mobType) {
        return ConfigManager.getMobConfig(mobType).has("enhanced_enchantments") && 
               ConfigManager.getMobConfig(mobType).get("enhanced_enchantments").getAsBoolean();
    }
    
    /**
     * 获取生成倍数
     */
    public static int getSpawnMultiplier() {
        return ConfigManager.getWorldConfig().has("spawn_multiplier") ? 
               ConfigManager.getWorldConfig().get("spawn_multiplier").getAsInt() : 8;
    }
    
    /**
     * 检查是否启用雷电密度
     */
    public static boolean isThunderDensityEnabled() {
        return ConfigManager.getWorldConfig().has("thunder_density") && 
               ConfigManager.getWorldConfig().get("thunder_density").getAsBoolean();
    }
    
    /**
     * 获取骷髅马生成概率
     */
    public static double getSkeletonHorseChance() {
        return ConfigManager.getWorldConfig().has("skeleton_horse_chance") ? 
               ConfigManager.getWorldConfig().get("skeleton_horse_chance").getAsDouble() : 0.01;
    }
    
    /**
     * 检查是否启用通用愤怒
     */
    public static boolean isUniversalAnger() {
        return ConfigManager.getWorldConfig().has("universal_anger") && 
               ConfigManager.getWorldConfig().get("universal_anger").getAsBoolean();
    }
    
    /**
     * 检查是否禁用自然再生
     */
    public static boolean isNaturalRegenerationDisabled() {
        return ConfigManager.getPlayerConfig().has("no_natural_regeneration") && 
               ConfigManager.getPlayerConfig().get("no_natural_regeneration").getAsBoolean();
    }
    
    /**
     * 检查是否启用挖掘疲劳（不拿工具）
     */
    public static boolean isMiningFatigueWithoutTools() {
        return ConfigManager.getPlayerConfig().has("mining_fatigue_without_tools") && 
               ConfigManager.getPlayerConfig().get("mining_fatigue_without_tools").getAsBoolean();
    }
    
    /**
     * 检查是否启用重甲减速
     */
    public static boolean isHeavyArmorSlowness() {
        return ConfigManager.getPlayerConfig().has("heavy_armor_slowness") && 
               ConfigManager.getPlayerConfig().get("heavy_armor_slowness").getAsBoolean();
    }
    
    /**
     * 检查是否启用受伤掉落物品
     */
    public static boolean isDropItemsOnHit() {
        return ConfigManager.getPlayerConfig().has("drop_items_on_hit") && 
               ConfigManager.getPlayerConfig().get("drop_items_on_hit").getAsBoolean();
    }
    
    /**
     * 检查是否启用移动掉落物品
     */
    public static boolean isDropItemsOnMove() {
        return ConfigManager.getPlayerConfig().has("drop_items_on_move") && 
               ConfigManager.getPlayerConfig().get("drop_items_on_move").getAsBoolean();
    }
    
    /**
     * 检查是否启用岩浆伤害
     */
    public static boolean isLavaHeatDamage() {
        return ConfigManager.getWorldConfig().has("lava_heat_damage") && 
               ConfigManager.getWorldConfig().get("lava_heat_damage").getAsBoolean();
    }
    
    /**
     * 检查是否启用仙人掌中毒
     */
    public static boolean isCactusPoison() {
        return ConfigManager.getWorldConfig().has("cactus_poison") && 
               ConfigManager.getWorldConfig().get("cactus_poison").getAsBoolean();
    }
    
    /**
     * 检查门是否在水中损坏
     */
    public static boolean isDoorsBreakInWater() {
        return ConfigManager.getWorldConfig().has("doors_break_in_water") && 
               ConfigManager.getWorldConfig().get("doors_break_in_water").getAsBoolean();
    }
    
    /**
     * 检查是否启用溺水效果
     */
    public static boolean isDrowningEffects() {
        return ConfigManager.getWorldConfig().has("drowning_effects") && 
               ConfigManager.getWorldConfig().get("drowning_effects").getAsBoolean();
    }
    
    /**
     * 检查是否启用睡觉跳过部分夜晚
     */
    public static boolean isSleepSkipsPartOfNight() {
        return ConfigManager.getWorldConfig().has("sleep_skips_third_of_night") && 
               ConfigManager.getWorldConfig().get("sleep_skips_third_of_night").getAsBoolean();
    }
    
    /**
     * 检查睡醒是否遇到幻翼
     */
    public static boolean isWakeUpToPhantoms() {
        return ConfigManager.getWorldConfig().has("wake_up_to_phantoms") && 
               ConfigManager.getWorldConfig().get("wake_up_to_phantoms").getAsBoolean();
    }
    
    /**
     * 检查是否启用随机雷暴
     */
    public static boolean isRandomLightningStorms() {
        return ConfigManager.getWorldConfig().has("random_lightning_storms") && 
               ConfigManager.getWorldConfig().get("random_lightning_storms").getAsBoolean();
    }
    
    /**
     * 检查传送门是否可能损坏
     */
    public static boolean isPortalBreakChance() {
        return ConfigManager.getWorldConfig().has("portal_break_chance") && 
               ConfigManager.getWorldConfig().get("portal_break_chance").getAsBoolean();
    }
    
    /**
     * 检查是否启用黑暗中随机声音
     */
    public static boolean isRandomMobNoisesInDark() {
        return ConfigManager.getWorldConfig().has("random_mob_noises_in_dark") && 
               ConfigManager.getWorldConfig().get("random_mob_noises_in_dark").getAsBoolean();
    }
    
    /**
     * 检查是否启用箭矢误射
     */
    public static boolean isArrowsMisfire() {
        return ConfigManager.getCombatConfig().has("arrows_misfire_chance") && 
               ConfigManager.getCombatConfig().get("arrows_misfire_chance").getAsDouble() > 0;
    }
    
    /**
     * 获取箭矢误射概率
     */
    public static double getArrowsMisfireChance() {
        return ConfigManager.getCombatConfig().has("arrows_misfire_chance") ? 
               ConfigManager.getCombatConfig().get("arrows_misfire_chance").getAsDouble() : 0.1;
    }
    
    /**
     * 检查末影珍珠是否总是生成末影螨
     */
    public static boolean isEnderPearlsAlwaysSpawnEndermites() {
        return ConfigManager.getCombatConfig().has("ender_pearls_always_spawn_endermites") && 
               ConfigManager.getCombatConfig().get("ender_pearls_always_spawn_endermites").getAsBoolean();
    }
    
    /**
     * 检查末地水晶是否反射弹射物
     */
    public static boolean isEndCrystalsReflectProjectiles() {
        return ConfigManager.getCombatConfig().has("ender_crystals_reflect_projectiles") && 
               ConfigManager.getCombatConfig().get("ender_crystals_reflect_projectiles").getAsBoolean();
    }
    
    /**
     * 检查图腾是否削弱
     */
    public static boolean isTotemNerfed() {
        return ConfigManager.getCombatConfig().has("totem_nerfed_drop") && 
               ConfigManager.getCombatConfig().get("totem_nerfed_drop").getAsBoolean();
    }
    
    /**
     * 检查进入村庄是否获得不祥之兆
     */
    public static boolean isBadOmenOnVillageEnter() {
        return ConfigManager.getCombatConfig().has("bad_omen_on_village_enter") && 
               ConfigManager.getCombatConfig().get("bad_omen_on_village_enter").getAsBoolean();
    }
    
    /**
     * 检查盾牌耐久度倍数
     */
    public static double getShieldDurabilityMultiplier() {
        return ConfigManager.getItemsConfig().has("shield_durability_multiplier") ? 
               ConfigManager.getItemsConfig().get("shield_durability_multiplier").getAsDouble() : 3.0;
    }
    
    /**
     * 检查烈焰棒是否烧伤
     */
    public static boolean isBlazeRodBurns() {
        return ConfigManager.getItemsConfig().has("blaze_rod_burns") && 
               ConfigManager.getItemsConfig().get("blaze_rod_burns").getAsBoolean();
    }
    
    /**
     * 检查岩浆桶是否烧伤
     */
    public static boolean isLavaBucketBurns() {
        return ConfigManager.getItemsConfig().has("lava_bucket_burns") && 
               ConfigManager.getItemsConfig().get("lava_bucket_burns").getAsBoolean();
    }
    
    /**
     * 检查肉类变质概率
     */
    public static double getMeatSpoilChance() {
        return ConfigManager.getItemsConfig().has("meat_spoil_chance") ? 
               ConfigManager.getItemsConfig().get("meat_spoil_chance").getAsDouble() : 0.0166;
    }
    
    /**
     * 检查食物中毒概率
     */
    public static double getFoodPoisoningChance() {
        return ConfigManager.getItemsConfig().has("food_poisoning_chance") ? 
               ConfigManager.getItemsConfig().get("food_poisoning_chance").getAsDouble() : 0.0166;
    }
    
    /**
     * 检查石头掉落概率（用低品质镐）
     */
    public static double getStoneDropChanceWithLowPickaxe() {
        return ConfigManager.getBlocksConfig().has("stone_drop_chance_with_low_pickaxe") ? 
               ConfigManager.getBlocksConfig().get("stone_drop_chance_with_low_pickaxe").getAsDouble() : 0.5;
    }
    
    /**
     * 检查矿石掉落概率
     */
    public static double getOreDropChance() {
        return ConfigManager.getBlocksConfig().has("ore_drop_chance") ? 
               ConfigManager.getBlocksConfig().get("ore_drop_chance").getAsDouble() : 0.5;
    }
    
    /**
     * 检查TNT破坏爆炸概率
     */
    public static double getTntBreakExplodesChance() {
        return ConfigManager.getBlocksConfig().has("tnt_break_explodes_chance") ? 
               ConfigManager.getBlocksConfig().get("tnt_break_explodes_chance").getAsDouble() : 0.1;
    }
    
    /**
     * 检查是否启用沙漠神殿卫道士
     */
    public static boolean isDesertTempleHusks() {
        return ConfigManager.getStructuresConfig().has("desert_temple_husks") && 
               ConfigManager.getStructuresConfig().get("desert_temple_husks").getAsBoolean();
    }
    
    /**
     * 检查是否启用废弃传送门幻术师
     */
    public static boolean isRuinedPortalIllusioners() {
        return ConfigManager.getStructuresConfig().has("ruined_portal_illusioners") && 
               ConfigManager.getStructuresConfig().get("ruined_portal_illusioners").getAsBoolean();
    }
    
    /**
     * 检查是否启用沉船溺尸守卫
     */
    public static boolean isShipwrecksGuardedByDrowned() {
        return ConfigManager.getStructuresConfig().has("shipwrecks_guarded_by_drowned") && 
               ConfigManager.getStructuresConfig().get("shipwrecks_guarded_by_drowned").getAsBoolean();
    }
}
