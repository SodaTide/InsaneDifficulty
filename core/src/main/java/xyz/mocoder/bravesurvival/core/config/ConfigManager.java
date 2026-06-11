package xyz.mocoder.bravesurvival.core.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 配置管理器
 * 负责加载和管理游戏配置
 */
public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("BraveSurvival");
    private static final Gson GSON = new Gson();
    private static JsonObject config;
    private static File configFile;

    /**
     * 初始化配置管理器
     * @param dataFolder 数据文件夹
     */
    public static void initialize(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        configFile = new File(dataFolder, "config.json");
        
        if (!configFile.exists()) {
            createDefaultConfig();
        }
        
        loadConfig();
    }

    /**
     * 创建默认配置
     */
    private static void createDefaultConfig() {
        JsonObject defaultConfig = new JsonObject();
        
        // 怪物配置
        JsonObject mobs = new JsonObject();
        
        // 僵尸配置
        JsonObject zombie = new JsonObject();
        zombie.addProperty("enabled", true);
        zombie.addProperty("health", 20.0);
        zombie.addProperty("damage", 8.0);
        zombie.addProperty("speed", 0.35);
        zombie.addProperty("follow_range", 55.0);
        zombie.addProperty("armor", 3.0);
        zombie.addProperty("burn_in_daylight", false);
        zombie.addProperty("enhanced_armor", true);
        zombie.addProperty("enhanced_enchantments", true);
        mobs.add("zombie", zombie);
        
        // 苦力怕配置
        JsonObject creeper = new JsonObject();
        creeper.addProperty("enabled", true);
        creeper.addProperty("always_charged", true);
        creeper.addProperty("instant_fuse", true);
        creeper.addProperty("invisible", true);
        mobs.add("creeper", creeper);
        
        // 末影人配置
        JsonObject enderman = new JsonObject();
        enderman.addProperty("enabled", true);
        enderman.addProperty("health", 40.0);
        enderman.addProperty("damage", 14.0);
        enderman.addProperty("speed", 0.75);
        enderman.addProperty("follow_range", 64.0);
        enderman.addProperty("spawn_endermites", true);
        enderman.addProperty("destroy_blocks", true);
        mobs.add("enderman", enderman);
        
        // 骷髅配置
        JsonObject skeleton = new JsonObject();
        skeleton.addProperty("enabled", true);
        skeleton.addProperty("damage", 10.0);
        skeleton.addProperty("burn_in_daylight", false);
        skeleton.addProperty("enhanced_armor", true);
        skeleton.addProperty("enhanced_enchantments", true);
        mobs.add("skeleton", skeleton);
        
        // 猪灵配置
        JsonObject piglin = new JsonObject();
        piglin.addProperty("enabled", true);
        piglin.addProperty("health", 16.0);
        piglin.addProperty("damage", 9.0);
        piglin.addProperty("speed", 0.30);
        piglin.addProperty("arrow_speed", 3.2);
        mobs.add("piglin", piglin);
        
        // 幻翼配置
        JsonObject phantom = new JsonObject();
        phantom.addProperty("enabled", true);
        phantom.addProperty("base_damage", 9.0);
        phantom.addProperty("burn_in_daylight", false);
        mobs.add("phantom", phantom);
        
        // 守卫者配置
        JsonObject guardian = new JsonObject();
        guardian.addProperty("enabled", true);
        guardian.addProperty("health", 30.0);
        guardian.addProperty("damage", 6.0);
        guardian.addProperty("speed", 0.5);
        guardian.addProperty("follow_range", 16.0);
        mobs.add("guardian", guardian);
        
        // 疣猪兽配置
        JsonObject hoglin = new JsonObject();
        hoglin.addProperty("enabled", true);
        hoglin.addProperty("health", 40.0);
        hoglin.addProperty("damage", 11.0);
        hoglin.addProperty("speed", 0.3);
        hoglin.addProperty("knockback_resistance", 0.6);
        mobs.add("hoglin", hoglin);
        
        // 铁傀儡配置
        JsonObject ironGolem = new JsonObject();
        ironGolem.addProperty("enabled", true);
        ironGolem.addProperty("attack_players", true);
        mobs.add("iron_golem", ironGolem);
        
        defaultConfig.add("mobs", mobs);
        
        // 玩家配置
        JsonObject player = new JsonObject();
        player.addProperty("fall_damage_debuff", true);
        player.addProperty("weakness_duration", 10);
        player.addProperty("slowness_duration", 10);
        defaultConfig.add("player", player);
        
        // 世界配置
        JsonObject world = new JsonObject();
        world.addProperty("spawn_multiplier", 8);
        world.addProperty("thunder_density", true);
        world.addProperty("skeleton_horse_chance", 0.01);
        defaultConfig.add("world", world);
        
        // 方块配置
        JsonObject blocks = new JsonObject();
        blocks.addProperty("silverfish_chance", 0.0625); // 1/16
        blocks.add("silverfish_blocks", GSON.toJsonTree(new String[]{
            "minecraft:stone", "minecraft:sandstone", "minecraft:cobblestone",
            "minecraft:andesite", "minecraft:granite", "minecraft:basalt", "minecraft:blackstone"
        }));
        defaultConfig.add("blocks", blocks);
        
        // 物品配置
        JsonObject items = new JsonObject();
        items.addProperty("shield_durability", 200);
        defaultConfig.add("items", items);
        
        // 船配置
        JsonObject boat = new JsonObject();
        boat.addProperty("sink_after_ticks", 600);
        boat.addProperty("sink_speed_multiplier", 0.05);
        defaultConfig.add("boat", boat);
        
        // 配方配置
        JsonObject recipes = new JsonObject();
        recipes.addProperty("planks_per_log", 2);
        defaultConfig.add("recipes", recipes);
        
        // 保存配置
        try (Writer writer = new FileWriter(configFile)) {
            GSON.toJson(defaultConfig, writer);
            LOGGER.info("已创建默认配置文件: {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("无法创建配置文件", e);
        }
    }

    /**
     * 加载配置
     */
    private static void loadConfig() {
        try (Reader reader = new FileReader(configFile)) {
            config = GSON.fromJson(reader, JsonObject.class);
            LOGGER.info("已加载配置文件");
        } catch (IOException e) {
            LOGGER.error("无法加载配置文件", e);
            config = new JsonObject();
        }
    }

    /**
     * 重新加载配置
     */
    public static void reloadConfig() {
        loadConfig();
    }

    /**
     * 获取配置值
     */
    public static JsonObject getConfig() {
        return config;
    }

    /**
     * 获取怪物配置
     */
    public static JsonObject getMobConfig(String mobType) {
        if (config != null && config.has("mobs")) {
            JsonObject mobs = config.getAsJsonObject("mobs");
            if (mobs.has(mobType)) {
                return mobs.getAsJsonObject(mobType);
            }
        }
        return new JsonObject();
    }

    /**
     * 检查怪物是否启用
     */
    public static boolean isMobEnabled(String mobType) {
        JsonObject mobConfig = getMobConfig(mobType);
        return mobConfig.has("enabled") && mobConfig.get("enabled").getAsBoolean();
    }

    /**
     * 获取怪物属性值
     */
    public static double getMobAttribute(String mobType, String attribute, double defaultValue) {
        JsonObject mobConfig = getMobConfig(mobType);
        if (mobConfig.has(attribute)) {
            return mobConfig.get(attribute).getAsDouble();
        }
        return defaultValue;
    }

    /**
     * 获取玩家配置
     */
    public static JsonObject getPlayerConfig() {
        if (config != null && config.has("player")) {
            return config.getAsJsonObject("player");
        }
        return new JsonObject();
    }

    /**
     * 获取世界配置
     */
    public static JsonObject getWorldConfig() {
        if (config != null && config.has("world")) {
            return config.getAsJsonObject("world");
        }
        return new JsonObject();
    }

    /**
     * 获取方块配置
     */
    public static JsonObject getBlocksConfig() {
        if (config != null && config.has("blocks")) {
            return config.getAsJsonObject("blocks");
        }
        return new JsonObject();
    }

    /**
     * 获取物品配置
     */
    public static JsonObject getItemsConfig() {
        if (config != null && config.has("items")) {
            return config.getAsJsonObject("items");
        }
        return new JsonObject();
    }

    /**
     * 获取船配置
     */
    public static JsonObject getBoatConfig() {
        if (config != null && config.has("boat")) {
            return config.getAsJsonObject("boat");
        }
        return new JsonObject();
    }

    /**
     * 获取配方配置
     */
    public static JsonObject getRecipesConfig() {
        if (config != null && config.has("recipes")) {
            return config.getAsJsonObject("recipes");
        }
        return new JsonObject();
    }
    
    /**
     * 检查是否启用雷电密度
     */
    public static boolean isThunderDensityEnabled() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("thunder_density") && worldConfig.get("thunder_density").getAsBoolean();
    }
    
    /**
     * 获取骷髅马生成概率
     */
    public static double getSkeletonHorseChance() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("skeleton_horse_chance") ? 
               worldConfig.get("skeleton_horse_chance").getAsDouble() : 0.01;
    }
    
    /**
     * 获取生成倍数
     */
    public static int getSpawnMultiplier() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("spawn_multiplier") ? 
               worldConfig.get("spawn_multiplier").getAsInt() : 8;
    }
    
    /**
     * 获取战斗配置
     */
    public static JsonObject getCombatConfig() {
        if (config != null && config.has("combat")) {
            return config.getAsJsonObject("combat");
        }
        return new JsonObject();
    }
    
    /**
     * 获取结构配置
     */
    public static JsonObject getStructuresConfig() {
        if (config != null && config.has("structures")) {
            return config.getAsJsonObject("structures");
        }
        return new JsonObject();
    }
    
    /**
     * 检查是否启用通用愤怒
     */
    public static boolean isUniversalAnger() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("universal_anger") && worldConfig.get("universal_anger").getAsBoolean();
    }
    
    /**
     * 检查是否禁用自然再生
     */
    public static boolean isNaturalRegenerationDisabled() {
        JsonObject playerConfig = getPlayerConfig();
        return playerConfig.has("no_natural_regeneration") && playerConfig.get("no_natural_regeneration").getAsBoolean();
    }
    
    /**
     * 检查是否启用挖掘疲劳（不拿工具）
     */
    public static boolean isMiningFatigueWithoutTools() {
        JsonObject playerConfig = getPlayerConfig();
        return playerConfig.has("mining_fatigue_without_tools") && playerConfig.get("mining_fatigue_without_tools").getAsBoolean();
    }
    
    /**
     * 检查是否启用重甲减速
     */
    public static boolean isHeavyArmorSlowness() {
        JsonObject playerConfig = getPlayerConfig();
        return playerConfig.has("heavy_armor_slowness") && playerConfig.get("heavy_armor_slowness").getAsBoolean();
    }
    
    /**
     * 检查是否启用受伤掉落物品
     */
    public static boolean isDropItemsOnHit() {
        JsonObject playerConfig = getPlayerConfig();
        return playerConfig.has("drop_items_on_hit") && playerConfig.get("drop_items_on_hit").getAsBoolean();
    }
    
    /**
     * 检查是否启用移动掉落物品
     */
    public static boolean isDropItemsOnMove() {
        JsonObject playerConfig = getPlayerConfig();
        return playerConfig.has("drop_items_on_move") && playerConfig.get("drop_items_on_move").getAsBoolean();
    }
    
    /**
     * 检查是否启用岩浆伤害
     */
    public static boolean isLavaHeatDamage() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("lava_heat_damage") && worldConfig.get("lava_heat_damage").getAsBoolean();
    }
    
    /**
     * 检查是否启用仙人掌中毒
     */
    public static boolean isCactusPoison() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("cactus_poison") && worldConfig.get("cactus_poison").getAsBoolean();
    }
    
    /**
     * 检查门是否在水中损坏
     */
    public static boolean isDoorsBreakInWater() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("doors_break_in_water") && worldConfig.get("doors_break_in_water").getAsBoolean();
    }
    
    /**
     * 检查是否启用溺水效果
     */
    public static boolean isDrowningEffects() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("drowning_effects") && worldConfig.get("drowning_effects").getAsBoolean();
    }
    
    /**
     * 检查是否启用睡觉跳过部分夜晚
     */
    public static boolean isSleepSkipsPartOfNight() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("sleep_skips_third_of_night") && worldConfig.get("sleep_skips_third_of_night").getAsBoolean();
    }
    
    /**
     * 检查睡醒是否遇到幻翼
     */
    public static boolean isWakeUpToPhantoms() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("wake_up_to_phantoms") && worldConfig.get("wake_up_to_phantoms").getAsBoolean();
    }
    
    /**
     * 检查是否启用随机雷暴
     */
    public static boolean isRandomLightningStorms() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("random_lightning_storms") && worldConfig.get("random_lightning_storms").getAsBoolean();
    }
    
    /**
     * 检查传送门是否可能损坏
     */
    public static boolean isPortalBreakChance() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("portal_break_chance") && worldConfig.get("portal_break_chance").getAsBoolean();
    }
    
    /**
     * 检查是否启用黑暗中随机声音
     */
    public static boolean isRandomMobNoisesInDark() {
        JsonObject worldConfig = getWorldConfig();
        return worldConfig.has("random_mob_noises_in_dark") && worldConfig.get("random_mob_noises_in_dark").getAsBoolean();
    }
    
    /**
     * 检查是否启用箭矢误射
     */
    public static boolean isArrowsMisfire() {
        JsonObject combatConfig = getCombatConfig();
        return combatConfig.has("arrows_misfire_chance") && combatConfig.get("arrows_misfire_chance").getAsDouble() > 0;
    }
    
    /**
     * 获取箭矢误射概率
     */
    public static double getArrowsMisfireChance() {
        JsonObject combatConfig = getCombatConfig();
        return combatConfig.has("arrows_misfire_chance") ? combatConfig.get("arrows_misfire_chance").getAsDouble() : 0.1;
    }
    
    /**
     * 检查末影珍珠是否总是生成末影螨
     */
    public static boolean isEnderPearlsAlwaysSpawnEndermites() {
        JsonObject combatConfig = getCombatConfig();
        return combatConfig.has("ender_pearls_always_spawn_endermites") && combatConfig.get("ender_pearls_always_spawn_endermites").getAsBoolean();
    }
    
    /**
     * 检查末地水晶是否反射弹射物
     */
    public static boolean isEndCrystalsReflectProjectiles() {
        JsonObject combatConfig = getCombatConfig();
        return combatConfig.has("ender_crystals_reflect_projectiles") && combatConfig.get("ender_crystals_reflect_projectiles").getAsBoolean();
    }
    
    /**
     * 检查图腾是否削弱
     */
    public static boolean isTotemNerfed() {
        JsonObject combatConfig = getCombatConfig();
        return combatConfig.has("totem_nerfed_drop") && combatConfig.get("totem_nerfed_drop").getAsBoolean();
    }
    
    /**
     * 检查进入村庄是否获得不祥之兆
     */
    public static boolean isBadOmenOnVillageEnter() {
        JsonObject combatConfig = getCombatConfig();
        return combatConfig.has("bad_omen_on_village_enter") && combatConfig.get("bad_omen_on_village_enter").getAsBoolean();
    }
    
    /**
     * 检查盾牌耐久度倍数
     */
    public static double getShieldDurabilityMultiplier() {
        JsonObject itemsConfig = getItemsConfig();
        return itemsConfig.has("shield_durability_multiplier") ? itemsConfig.get("shield_durability_multiplier").getAsDouble() : 3.0;
    }
    
    /**
     * 检查烈焰棒是否烧伤
     */
    public static boolean isBlazeRodBurns() {
        JsonObject itemsConfig = getItemsConfig();
        return itemsConfig.has("blaze_rod_burns") && itemsConfig.get("blaze_rod_burns").getAsBoolean();
    }
    
    /**
     * 检查岩浆桶是否烧伤
     */
    public static boolean isLavaBucketBurns() {
        JsonObject itemsConfig = getItemsConfig();
        return itemsConfig.has("lava_bucket_burns") && itemsConfig.get("lava_bucket_burns").getAsBoolean();
    }
    
    /**
     * 检查肉类变质概率
     */
    public static double getMeatSpoilChance() {
        JsonObject itemsConfig = getItemsConfig();
        return itemsConfig.has("meat_spoil_chance") ? itemsConfig.get("meat_spoil_chance").getAsDouble() : 0.0166;
    }
    
    /**
     * 检查食物中毒概率
     */
    public static double getFoodPoisoningChance() {
        JsonObject itemsConfig = getItemsConfig();
        return itemsConfig.has("food_poisoning_chance") ? itemsConfig.get("food_poisoning_chance").getAsDouble() : 0.0166;
    }
    
    /**
     * 检查石头掉落概率（用低品质镐）
     */
    public static double getStoneDropChanceWithLowPickaxe() {
        JsonObject blocksConfig = getBlocksConfig();
        return blocksConfig.has("stone_drop_chance_with_low_pickaxe") ? blocksConfig.get("stone_drop_chance_with_low_pickaxe").getAsDouble() : 0.5;
    }
    
    /**
     * 检查矿石掉落概率
     */
    public static double getOreDropChance() {
        JsonObject blocksConfig = getBlocksConfig();
        return blocksConfig.has("ore_drop_chance") ? blocksConfig.get("ore_drop_chance").getAsDouble() : 0.5;
    }
    
    /**
     * 检查TNT破坏爆炸概率
     */
    public static double getTntBreakExplodesChance() {
        JsonObject blocksConfig = getBlocksConfig();
        return blocksConfig.has("tnt_break_explodes_chance") ? blocksConfig.get("tnt_break_explodes_chance").getAsDouble() : 0.1;
    }
    
    /**
     * 检查是否启用沙漠神殿卫道士
     */
    public static boolean isDesertTempleHusks() {
        JsonObject structuresConfig = getStructuresConfig();
        return structuresConfig.has("desert_temple_husks") && structuresConfig.get("desert_temple_husks").getAsBoolean();
    }
    
    /**
     * 检查是否启用废弃传送门幻术师
     */
    public static boolean isRuinedPortalIllusioners() {
        JsonObject structuresConfig = getStructuresConfig();
        return structuresConfig.has("ruined_portal_illusioners") && structuresConfig.get("ruined_portal_illusioners").getAsBoolean();
    }
    
    /**
     * 检查是否启用沉船溺尸守卫
     */
    public static boolean isShipwrecksGuardedByDrowned() {
        JsonObject structuresConfig = getStructuresConfig();
        return structuresConfig.has("shipwrecks_guarded_by_drowned") && structuresConfig.get("shipwrecks_guarded_by_drowned").getAsBoolean();
    }
}
