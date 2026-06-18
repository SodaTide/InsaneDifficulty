package com.bravesurvival.insanedifficulty.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    // 游戏规则
    private boolean naturalRegeneration;
    private boolean universalAnger;
    private boolean forgiveDeadPlayers;
    private boolean reducedDebugInfo;

    // 生物配置
    private final Map<String, Boolean> mobEnabled = new HashMap<>();
    private final Map<String, Double> mobFollowRange = new HashMap<>();
    private final Map<String, Double> mobDamage = new HashMap<>();
    private final Map<String, Double> mobSpeed = new HashMap<>();
    private final Map<String, Double> mobHealth = new HashMap<>();

    // 苦力怕配置
    private boolean creeperPowered;
    private int creeperFuseTicks;
    private int creeperExplosionRadius;
    private double creeperSpeed;

    // 蜘蛛配置
    private boolean spiderInvisible;
    private double spiderSpeed;

    // 烈焰人配置
    private boolean blazeNoGravity;
    private double blazeSpeed;

    // 幻翼配置
    private boolean phantomFireResist;
    private double phantomDamage;

    // 凋零骷髅配置
    private double witherSkeletonHealth;
    private double witherSkeletonDamage;
    private double witherSkeletonWitchChance;

    // 铁傀儡配置
    private double ironGolemHealth;
    private double ironGolemSlamChance;

    // 苦力怕配置
    private boolean enderDragonEnabled;
    private double enderDragonRegenChance;
    private double enderDragonRegenAmount;

    // 末影人配置
    private boolean endermanTeleportOnHit;
    private boolean endermanDestroyBlocks;
    private boolean endermanSpawnEndermites;

    // 骷髅配置
    private boolean skeletonAutoAim;
    private boolean skeletonPowerBow;
    private boolean skeletonNetheriteHelmet;

    // 掠夺者配置
    private boolean pillagerQuickCharge5;
    private boolean pillagerMultishot;
    private boolean pillagerSpawnInPairs;
    private boolean pillagerRavagerWith;

    // 女巫配置
    private boolean witchWitherSkulls;
    private boolean witchSplashPotions;
    private boolean witchSpawnWithWitherSkeletons;

    // 幻术师配置
    private boolean illusionerAutoAim;
    private boolean illusionerPowerBow;

    // 被动生物配置
    private boolean cowTurnToRabbit;
    private boolean sheepTurnToRabbit;
    private boolean dolphinTurnToCod;
    private boolean pigTurnToHoglin;
    private boolean chickenTurnToJockey;
    private double passiveTransformRange;

    // 鱼类配置
    private boolean fishTurnToGuardian;
    private double fishTransformChance;

    // 玩家配置
    private boolean fallDamageDebuff;
    private boolean heavyArmorSlowness;
    private boolean miningFatigueWithoutTools;
    private boolean hungerEffects;
    private boolean dropItemsOnMove;
    private double dropItemsOnMoveChance;
    private boolean dropItemsOnHit;
    private double dropItemsOnHitChance;
    private int dropItemsOnHitCount;
    private boolean deathSpawnZombie;
    private double deathSpawnZombieChance;

    // 摔落伤害配置
    private int fallDamageSlownessDuration;
    private int fallDamageWeaknessDuration;
    private int fallDamageBlindnessDuration;

    // 睡眠配置
    private boolean sleepSkipTime;
    private boolean sleepPhantomSpawn;
    private double sleepPhantomSpawnChance;
    private int sleepPhantomCount;
    private boolean sleepSpawnVex;

    // 饥饿配置
    private int hungerWeakness1Threshold;
    private int hungerWeakness2Threshold;
    private int hungerWeakness3Threshold;

    // 溺水配置
    private boolean drowningEffects;
    private boolean drowningSink;

    // 世界配置
    private boolean doorsBreakInWater;
    private boolean lavaHeatDamage;
    private boolean cactusPoison;
    private boolean lightningStorms;
    private double lightningStormChance;
    private boolean randomMobNoises;
    private double randomMobNoiseChance;

    // 闪电类型配置
    private double lightningNormalChance;
    private double lightningPrecisionChance;
    private double lightningBundledChance;
    private double lightningRecursiveChance;
    private double lightningGroupChance;

    // 岩浆配置
    private int lavaFireTicks;
    private boolean lavaWeaknessOnFire;

    // 传送门配置
    private boolean portalBreakChance;
    private double portalBreakChanceValue;

    // 桶配置
    private boolean bucketsLeak;
    private double bucketLeakChance;

    // 箭矢配置
    private boolean arrowsMisfire;
    private double arrowsMisfireChance;
    private boolean skeletonAutoAimArrows;

    // 末影水晶配置
    private boolean enderCrystalsReflect;
    private double enderCrystalsReflectRange;

    // 末影珍珠配置
    private boolean enderPearlsSpawnEndermites;

    // 图腾配置
    private boolean totemNerfed;
    private int totemRegenDuration1;
    private int totemRegenAmplifier1;
    private int totemRegenDuration2;
    private int totemRegenAmplifier2;

    // 药水配置
    private boolean potionSideEffects;
    private double potionSideEffectsChance;

    // 食物配置
    private boolean rawFoodDebuff;
    private boolean rottenFleshDebuff;
    private boolean foodPoisoning;
    private double foodPoisoningChance;
    private boolean foodSpoil;
    private double foodSpoilChance;

    // 盾牌配置
    private int shieldDurabilityPerBlock;

    // 矿石配置
    private boolean oreDropChance;
    private double oreDropChanceValue;

    // 石头配置
    private boolean stoneDropWithPickaxe;
    private double stoneDropChanceWooden;
    private double stoneDropChanceStone;

    // 干草块配置
    private boolean hayBlockSilkTouch;
    private int hayBlockToWheat;

    // TNT配置
    private boolean tntBreakExplodes;
    private double tntBreakExplodesChance;

    // 银鱼配置
    private boolean silverfishSpawnFromStone;
    private double silverfishSpawnChance;

    // 末影螨配置
    private boolean endermiteSpawnFromEndStone;
    private double endermiteSpawnChance;
    private boolean endermiteSpawnFromEnderman;

    // 感染石头配置
    private boolean infestedStoneSpawnsFangs;
    private double infestedStoneFangsChance;

    // 末影之眼配置
    private boolean eyeOfEnderExplode;
    private int eyeOfEnderMaxUses;

    // 村民配置
    private boolean villagerTradesExpensive;
    private double villagerTradeMultiplier;
    private boolean villagerBadOmen;
    private double villagerBadOmenChance;

    // 结构配置
    private boolean desertTempleHusks;
    private int desertTempleHuskCount;
    private boolean desertTempleTnt;
    private boolean shipwreckDrowned;
    private int shipwreckDrownedCount;
    private boolean buriedTreasureVex;
    private int buriedTreasureVexCount;
    private boolean ruinedPortalIllusioner;
    private int ruinedPortalIllusionerCount;
    private boolean junglePyramidIllusioner;
    private int junglePyramidIllusionerCount;

    // 流浪商人配置
    private boolean wanderingTraderReplace;
    private boolean wanderingTraderRavager;

    // 凋零骷髅破坏方块配置
    private boolean witherSkeletonDestroyBlocks;

    // 末影人破坏方块配置
    private boolean endermanDestroyBlocksNearHead;

    // 猪灵配置
    private boolean piglinAlwaysAngry;
    private boolean piglinFullGoldImmune;

    // 重型盔甲配置
    private Set<Material> heavyArmorMaterials;

    // 工具配置
    private Set<Material> toolMaterials;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadAllConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadAllConfig();
    }

    private void loadAllConfig() {
        // 游戏规则
        naturalRegeneration = config.getBoolean("game_rules.natural_regeneration", false);
        universalAnger = config.getBoolean("game_rules.universal_anger", true);
        forgiveDeadPlayers = config.getBoolean("game_rules.forgive_dead_players", false);
        reducedDebugInfo = config.getBoolean("game_rules.reduced_debug_info", true);

        // 苦力怕配置
        creeperPowered = config.getBoolean("mobs.creeper.always_charged", true);
        creeperFuseTicks = config.getInt("mobs.creeper.fuse_ticks", 1);
        creeperExplosionRadius = config.getInt("mobs.creeper.explosion_radius", 10);
        creeperSpeed = config.getDouble("mobs.creeper.speed", 0.3);

        // 蜘蛛配置
        spiderInvisible = config.getBoolean("mobs.spider.invisible", true);
        spiderSpeed = config.getDouble("mobs.spider.speed", 0.5);

        // 烈焰人配置
        blazeNoGravity = config.getBoolean("mobs.blaze.no_gravity", true);
        blazeSpeed = config.getDouble("mobs.blaze.speed", 1.0);

        // 幻翼配置
        phantomFireResist = config.getBoolean("mobs.phantom.fire_resistance", true);
        phantomDamage = config.getDouble("mobs.phantom.damage", 3.0);

        // 凋零骷髅配置
        witherSkeletonHealth = config.getDouble("mobs.wither_skeleton.health", 30.0);
        witherSkeletonDamage = config.getDouble("mobs.wither_skeleton.damage", 5.0);
        witherSkeletonWitchChance = config.getDouble("mobs.wither_skeleton.witch_spawn_chance", 0.2);
        witherSkeletonDestroyBlocks = config.getBoolean("mobs.wither_skeleton.destroy_blocks_near_skulls", true);

        // 铁傀儡配置
        ironGolemHealth = config.getDouble("mobs.iron_golem.health_value", 150.0);
        ironGolemSlamChance = config.getDouble("mobs.iron_golem.smash_chance", 0.015);

        // 末影龙配置
        enderDragonEnabled = config.getBoolean("ender_dragon.enabled", true);
        enderDragonRegenChance = config.getDouble("ender_dragon.regen_chance_per_tick", 0.01);
        enderDragonRegenAmount = config.getDouble("ender_dragon.regen_amount", 1.0);

        // 末影人配置
        endermanTeleportOnHit = config.getBoolean("mobs.enderman.teleport_after_hit", true);
        endermanDestroyBlocks = config.getBoolean("mobs.enderman.destroy_blocks", true);
        endermanSpawnEndermites = config.getBoolean("mobs.enderman.spawn_endermites_on_death", true);
        endermanDestroyBlocksNearHead = config.getBoolean("combat.enderman_block_break_on_teleport", true);

        // 骷髅配置
        skeletonAutoAim = config.getBoolean("mobs.skeleton.auto_aim", true);
        skeletonPowerBow = config.getBoolean("mobs.skeleton.power_bow", true);
        skeletonNetheriteHelmet = config.getBoolean("mobs.skeleton.netherite_helmet", true);

        // 掠夺者配置
        pillagerQuickCharge5 = config.getBoolean("mobs.pillager.quick_charge_5", true);
        pillagerMultishot = config.getBoolean("mobs.pillager.multishot", true);
        pillagerSpawnInPairs = config.getBoolean("mobs.pillager.spawn_in_pairs", true);
        pillagerRavagerWith = config.getBoolean("mobs.pillager.ravager_with_pillager", true);

        // 女巫配置
        witchWitherSkulls = config.getBoolean("mobs.witch.wither_skulls", true);
        witchSplashPotions = config.getBoolean("mobs.witch.splash_potions_on_damage", true);
        witchSpawnWithWitherSkeletons = config.getBoolean("mobs.witch.spawn_with_wither_skeletons", true);

        // 幻术师配置
        illusionerAutoAim = config.getBoolean("mobs.illusioner.auto_aim", true);
        illusionerPowerBow = config.getBoolean("mobs.illusioner.power_bow", true);

        // 被动生物配置
        cowTurnToRabbit = config.getBoolean("mobs.cow.turn_into_rabbit", true);
        sheepTurnToRabbit = config.getBoolean("mobs.sheep.turn_into_rabbit", true);
        dolphinTurnToCod = config.getBoolean("mobs.dolphin.turn_into_cod", true);
        pigTurnToHoglin = config.getBoolean("mobs.pig.turn_into_hoglin", true);
        chickenTurnToJockey = config.getBoolean("mobs.chicken.turn_into_jockey", true);
        passiveTransformRange = config.getDouble("mobs.cow.transform_range", 8.0);

        // 鱼类配置
        fishTurnToGuardian = config.getBoolean("mobs.fish.turn_into_guardian", true);
        fishTransformChance = config.getDouble("mobs.fish.transform_chance", 0.33);

        // 玩家配置
        fallDamageDebuff = config.getBoolean("player.fall_damage_debuff", true);
        heavyArmorSlowness = config.getBoolean("player.heavy_armor_slowness", true);
        miningFatigueWithoutTools = config.getBoolean("player.mining_fatigue_without_tools", true);
        hungerEffects = config.getBoolean("player.hunger_effects", true);
        dropItemsOnMove = config.getBoolean("player.drop_items_on_move", true);
        dropItemsOnMoveChance = config.getDouble("player.drop_items_on_move_chance", 0.00083);
        dropItemsOnHit = config.getBoolean("player.drop_items_on_hit", true);
        dropItemsOnHitChance = config.getDouble("player.drop_items_on_hit_chance", 0.5);
        dropItemsOnHitCount = config.getInt("player.drop_items_on_hit_count", 4);
        deathSpawnZombie = config.getBoolean("player.death_spawn_zombie", true);
        deathSpawnZombieChance = config.getDouble("player.death_spawn_zombie_chance", 0.5);

        // 摔落伤害配置
        fallDamageSlownessDuration = config.getInt("player.fall_damage_slowness_duration", 300);
        fallDamageWeaknessDuration = config.getInt("player.fall_damage_weakness_duration", 200);
        fallDamageBlindnessDuration = config.getInt("player.fall_damage_blindness_duration", 20);

        // 睡眠配置
        sleepSkipTime = config.getBoolean("sleep.skip_time", true);
        sleepPhantomSpawn = config.getBoolean("sleep.phantom_spawn", true);
        sleepPhantomSpawnChance = config.getDouble("sleep.phantom_spawn_chance", 0.5);
        sleepPhantomCount = config.getInt("sleep.phantom_count", 4);
        sleepSpawnVex = config.getBoolean("sleep.spawn_vex", true);

        // 饥饿配置
        hungerWeakness1Threshold = config.getInt("player.hunger_weakness_1_threshold", 10);
        hungerWeakness2Threshold = config.getInt("player.hunger_weakness_2_threshold", 7);
        hungerWeakness3Threshold = config.getInt("player.hunger_weakness_3_threshold", 5);

        // 溺水配置
        drowningEffects = config.getBoolean("player.drowning_effects", true);
        drowningSink = config.getBoolean("player.drowning_sink", true);

        // 世界配置
        doorsBreakInWater = config.getBoolean("world.doors_break_in_water", true);
        lavaHeatDamage = config.getBoolean("world.lava_heat_damage", true);
        cactusPoison = config.getBoolean("world.cactus_poison", true);
        lightningStorms = config.getBoolean("world.lightning_storms", true);
        lightningStormChance = config.getDouble("world.lightning_storm_chance", 0.000033);
        randomMobNoises = config.getBoolean("world.random_mob_noises", true);
        randomMobNoiseChance = config.getDouble("world.random_mob_noise_chance", 0.00166);

        // 闪电类型配置
        lightningNormalChance = config.getDouble("world.lightning.normal_chance", 0.025);
        lightningPrecisionChance = config.getDouble("world.lightning.precision_chance", 0.0025);
        lightningBundledChance = config.getDouble("world.lightning.bundled_chance", 0.0025);
        lightningRecursiveChance = config.getDouble("world.lightning.recursive_chance", 0.0025);
        lightningGroupChance = config.getDouble("world.lightning.group_chance", 0.0025);

        // 岩浆配置
        lavaFireTicks = config.getInt("world.lava_fire_ticks", 100);
        lavaWeaknessOnFire = config.getBoolean("world.lava_weakness_on_fire", true);

        // 传送门配置
        portalBreakChance = config.getBoolean("world.portal_break_chance", true);
        portalBreakChanceValue = config.getDouble("world.portal_break_chance_value", 0.5);

        // 桶配置
        bucketsLeak = config.getBoolean("items.buckets_leak", true);
        bucketLeakChance = config.getDouble("items.bucket_leak_chance", 0.1);

        // 箭矢配置
        arrowsMisfire = config.getBoolean("combat.arrows_misfire", true);
        arrowsMisfireChance = config.getDouble("combat.arrows_misfire_chance", 0.2);
        skeletonAutoAimArrows = config.getBoolean("mobs.skeleton.auto_aim", true);

        // 末影水晶配置
        enderCrystalsReflect = config.getBoolean("combat.ender_crystals_reflect", true);
        enderCrystalsReflectRange = config.getDouble("combat.ender_crystals_reflect_range", 5.0);

        // 末影珍珠配置
        enderPearlsSpawnEndermites = config.getBoolean("combat.ender_pearls_spawn_endermites", true);

        // 图腾配置
        totemNerfed = config.getBoolean("combat.totem_nerfed", true);
        totemRegenDuration1 = config.getInt("combat.totem_regen_duration_1", 460);
        totemRegenAmplifier1 = config.getInt("combat.totem_regen_amplifier_1", 0);
        totemRegenDuration2 = config.getInt("combat.totem_regen_duration_2", 300);
        totemRegenAmplifier2 = config.getInt("combat.totem_regen_amplifier_2", 1);

        // 药水配置
        potionSideEffects = config.getBoolean("combat.potion_side_effects", true);
        potionSideEffectsChance = config.getDouble("combat.potion_side_effects_chance", 0.5);

        // 食物配置
        rawFoodDebuff = config.getBoolean("food.raw_food_debuff", true);
        rottenFleshDebuff = config.getBoolean("food.rotten_flesh_debuff", true);
        foodPoisoning = config.getBoolean("food.food_poisoning", true);
        foodPoisoningChance = config.getDouble("food.food_poisoning_chance", 0.1);
        foodSpoil = config.getBoolean("food.food_spoil", true);
        foodSpoilChance = config.getDouble("food.food_spoil_chance", 0.00166);

        // 盾牌配置
        shieldDurabilityPerBlock = config.getInt("combat.shield_durability_per_block", 67);

        // 矿石配置
        oreDropChance = config.getBoolean("blocks.ore_drop_chance", true);
        oreDropChanceValue = config.getDouble("blocks.ore_drop_chance_value", 0.5);

        // 石头配置
        stoneDropWithPickaxe = config.getBoolean("blocks.stone_drop_with_pickaxe", true);
        stoneDropChanceWooden = config.getDouble("blocks.stone_drop_chance_wooden", 0.5);
        stoneDropChanceStone = config.getDouble("blocks.stone_drop_chance_stone", 0.75);

        // 干草块配置
        hayBlockSilkTouch = config.getBoolean("blocks.hay_block_silk_touch", true);
        hayBlockToWheat = config.getInt("blocks.hay_block_to_wheat", 4);

        // TNT配置
        tntBreakExplodes = config.getBoolean("blocks.tnt_break_explodes", true);
        tntBreakExplodesChance = config.getDouble("blocks.tnt_break_explodes_chance", 0.2);

        // 银鱼配置
        silverfishSpawnFromStone = config.getBoolean("blocks.silverfish_spawn_from_stone", true);
        silverfishSpawnChance = config.getDouble("blocks.silverfish_spawn_chance", 0.1);

        // 末影螨配置
        endermiteSpawnFromEndStone = config.getBoolean("blocks.endermite_spawn_from_end_stone", true);
        endermiteSpawnChance = config.getDouble("blocks.endermite_spawn_chance", 0.15);
        endermiteSpawnFromEnderman = config.getBoolean("mobs.enderman.spawn_endermites_on_death", true);

        // 感染石头配置
        infestedStoneSpawnsFangs = config.getBoolean("blocks.infested_stone_spawns_fangs", true);
        infestedStoneFangsChance = config.getDouble("blocks.infested_stone_fangs_chance", 0.1);

        // 末影之眼配置
        eyeOfEnderExplode = config.getBoolean("items.eye_of_ender_explode", true);
        eyeOfEnderMaxUses = config.getInt("items.eye_of_ender_max_uses", 81);

        // 村民配置
        villagerTradesExpensive = config.getBoolean("villager.trades_more_expensive", true);
        villagerTradeMultiplier = config.getDouble("villager.trade_multiplier", 2.0);
        villagerBadOmen = config.getBoolean("villager.bad_omen_on_village_enter", true);
        villagerBadOmenChance = config.getDouble("villager.bad_omen_chance", 0.33);

        // 结构配置
        desertTempleHusks = config.getBoolean("structures.desert_temple_husks", true);
        desertTempleHuskCount = config.getInt("structures.desert_temple_husk_count", 12);
        desertTempleTnt = config.getBoolean("structures.desert_temple_tnt", true);
        shipwreckDrowned = config.getBoolean("structures.shipwrecks_guarded_by_drowned", true);
        shipwreckDrownedCount = config.getInt("structures.shipwreck_drowned_count", 8);
        buriedTreasureVex = config.getBoolean("structures.buried_treasure_guarded_by_vexes", true);
        buriedTreasureVexCount = config.getInt("structures.buried_treasure_vex_count", 4);
        ruinedPortalIllusioner = config.getBoolean("structures.ruined_portal_illusioners", true);
        ruinedPortalIllusionerCount = config.getInt("structures.ruined_portal_illusioner_count", 4);
        junglePyramidIllusioner = config.getBoolean("structures.jungle_temple_illusioners", true);
        junglePyramidIllusionerCount = config.getInt("structures.jungle_temple_illusioner_count", 4);

        // 流浪商人配置
        wanderingTraderReplace = config.getBoolean("wandering_trader.replace_with_pillagers", true);
        wanderingTraderRavager = config.getBoolean("wandering_trader.replace_with_ravager", true);

        // 猪灵配置
        piglinAlwaysAngry = config.getBoolean("mobs.piglin.always_angry_unless_full_gold", true);
        piglinFullGoldImmune = config.getBoolean("mobs.piglin.full_gold_immune", true);

        // 初始化重型盔甲材料
        heavyArmorMaterials = new HashSet<>(Arrays.asList(
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS
        ));

        // 初始化工具材料
        toolMaterials = new HashSet<>(Arrays.asList(
            Material.WOODEN_SWORD, Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE,
            Material.STONE_SWORD, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_HOE,
            Material.IRON_SWORD, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_HOE,
            Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE,
            Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,
            Material.NETHERITE_SWORD, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE,
            Material.SHEARS
        ));
    }

    // Getter方法
    public boolean isNaturalRegeneration() { return naturalRegeneration; }
    public boolean isUniversalAnger() { return universalAnger; }
    public boolean isForgiveDeadPlayers() { return forgiveDeadPlayers; }
    public boolean isReducedDebugInfo() { return reducedDebugInfo; }

    public boolean isCreeperPowered() { return creeperPowered; }
    public int getCreeperFuseTicks() { return creeperFuseTicks; }
    public int getCreeperExplosionRadius() { return creeperExplosionRadius; }
    public double getCreeperSpeed() { return creeperSpeed; }

    public boolean isSpiderInvisible() { return spiderInvisible; }
    public double getSpiderSpeed() { return spiderSpeed; }

    public boolean isBlazeNoGravity() { return blazeNoGravity; }
    public double getBlazeSpeed() { return blazeSpeed; }

    public boolean isPhantomFireResist() { return phantomFireResist; }
    public double getPhantomDamage() { return phantomDamage; }

    public double getWitherSkeletonHealth() { return witherSkeletonHealth; }
    public double getWitherSkeletonDamage() { return witherSkeletonDamage; }
    public double getWitherSkeletonWitchChance() { return witherSkeletonWitchChance; }
    public boolean isWitherSkeletonDestroyBlocks() { return witherSkeletonDestroyBlocks; }

    public double getIronGolemHealth() { return ironGolemHealth; }
    public double getIronGolemSlamChance() { return ironGolemSlamChance; }

    public boolean isEnderDragonEnabled() { return enderDragonEnabled; }
    public double getEnderDragonRegenChance() { return enderDragonRegenChance; }
    public double getEnderDragonRegenAmount() { return enderDragonRegenAmount; }

    public boolean isEndermanTeleportOnHit() { return endermanTeleportOnHit; }
    public boolean isEndermanDestroyBlocks() { return endermanDestroyBlocks; }
    public boolean isEndermanSpawnEndermites() { return endermanSpawnEndermites; }
    public boolean isEndermanDestroyBlocksNearHead() { return endermanDestroyBlocksNearHead; }

    public boolean isSkeletonAutoAim() { return skeletonAutoAim; }
    public boolean isSkeletonPowerBow() { return skeletonPowerBow; }
    public boolean isSkeletonNetheriteHelmet() { return skeletonNetheriteHelmet; }

    public boolean isPillagerQuickCharge5() { return pillagerQuickCharge5; }
    public boolean isPillagerMultishot() { return pillagerMultishot; }
    public boolean isPillagerSpawnInPairs() { return pillagerSpawnInPairs; }
    public boolean isPillagerRavagerWith() { return pillagerRavagerWith; }

    public boolean isWitchWitherSkulls() { return witchWitherSkulls; }
    public boolean isWitchSplashPotions() { return witchSplashPotions; }
    public boolean isWitchSpawnWithWitherSkeletons() { return witchSpawnWithWitherSkeletons; }

    public boolean isIllusionerAutoAim() { return illusionerAutoAim; }
    public boolean isIllusionerPowerBow() { return illusionerPowerBow; }

    public boolean isCowTurnToRabbit() { return cowTurnToRabbit; }
    public boolean isSheepTurnToRabbit() { return sheepTurnToRabbit; }
    public boolean isDolphinTurnToCod() { return dolphinTurnToCod; }
    public boolean isPigTurnToHoglin() { return pigTurnToHoglin; }
    public boolean isChickenTurnToJockey() { return chickenTurnToJockey; }
    public double getPassiveTransformRange() { return passiveTransformRange; }

    public boolean isFishTurnToGuardian() { return fishTurnToGuardian; }
    public double getFishTransformChance() { return fishTransformChance; }

    public boolean isFallDamageDebuff() { return fallDamageDebuff; }
    public boolean isHeavyArmorSlowness() { return heavyArmorSlowness; }
    public boolean isMiningFatigueWithoutTools() { return miningFatigueWithoutTools; }
    public boolean isHungerEffects() { return hungerEffects; }
    public boolean isDropItemsOnMove() { return dropItemsOnMove; }
    public double getDropItemsOnMoveChance() { return dropItemsOnMoveChance; }
    public boolean isDropItemsOnHit() { return dropItemsOnHit; }
    public double getDropItemsOnHitChance() { return dropItemsOnHitChance; }
    public int getDropItemsOnHitCount() { return dropItemsOnHitCount; }
    public boolean isDeathSpawnZombie() { return deathSpawnZombie; }
    public double getDeathSpawnZombieChance() { return deathSpawnZombieChance; }

    public int getFallDamageSlownessDuration() { return fallDamageSlownessDuration; }
    public int getFallDamageWeaknessDuration() { return fallDamageWeaknessDuration; }
    public int getFallDamageBlindnessDuration() { return fallDamageBlindnessDuration; }

    public boolean isSleepSkipTime() { return sleepSkipTime; }
    public boolean isSleepPhantomSpawn() { return sleepPhantomSpawn; }
    public double getSleepPhantomSpawnChance() { return sleepPhantomSpawnChance; }
    public int getSleepPhantomCount() { return sleepPhantomCount; }
    public boolean isSleepSpawnVex() { return sleepSpawnVex; }

    public int getHungerWeakness1Threshold() { return hungerWeakness1Threshold; }
    public int getHungerWeakness2Threshold() { return hungerWeakness2Threshold; }
    public int getHungerWeakness3Threshold() { return hungerWeakness3Threshold; }

    public boolean isDrowningEffects() { return drowningEffects; }
    public boolean isDrowningSink() { return drowningSink; }

    public boolean isDoorsBreakInWater() { return doorsBreakInWater; }
    public boolean isLavaHeatDamage() { return lavaHeatDamage; }
    public boolean isCactusPoison() { return cactusPoison; }
    public boolean isLightningStorms() { return lightningStorms; }
    public double getLightningStormChance() { return lightningStormChance; }
    public boolean isRandomMobNoises() { return randomMobNoises; }
    public double getRandomMobNoiseChance() { return randomMobNoiseChance; }

    public double getLightningNormalChance() { return lightningNormalChance; }
    public double getLightningPrecisionChance() { return lightningPrecisionChance; }
    public double getLightningBundledChance() { return lightningBundledChance; }
    public double getLightningRecursiveChance() { return lightningRecursiveChance; }
    public double getLightningGroupChance() { return lightningGroupChance; }

    public int getLavaFireTicks() { return lavaFireTicks; }
    public boolean isLavaWeaknessOnFire() { return lavaWeaknessOnFire; }

    public boolean isPortalBreakChance() { return portalBreakChance; }
    public double getPortalBreakChanceValue() { return portalBreakChanceValue; }

    public boolean isBucketsLeak() { return bucketsLeak; }
    public double getBucketLeakChance() { return bucketLeakChance; }

    public boolean isArrowsMisfire() { return arrowsMisfire; }
    public double getArrowsMisfireChance() { return arrowsMisfireChance; }
    public boolean isSkeletonAutoAimArrows() { return skeletonAutoAimArrows; }

    public boolean isEnderCrystalsReflect() { return enderCrystalsReflect; }
    public double getEnderCrystalsReflectRange() { return enderCrystalsReflectRange; }

    public boolean isEnderPearlsSpawnEndermites() { return enderPearlsSpawnEndermites; }

    public boolean isTotemNerfed() { return totemNerfed; }
    public int getTotemRegenDuration1() { return totemRegenDuration1; }
    public int getTotemRegenAmplifier1() { return totemRegenAmplifier1; }
    public int getTotemRegenDuration2() { return totemRegenDuration2; }
    public int getTotemRegenAmplifier2() { return totemRegenAmplifier2; }

    public boolean isPotionSideEffects() { return potionSideEffects; }
    public double getPotionSideEffectsChance() { return potionSideEffectsChance; }

    public boolean isRawFoodDebuff() { return rawFoodDebuff; }
    public boolean isRottenFleshDebuff() { return rottenFleshDebuff; }
    public boolean isFoodPoisoning() { return foodPoisoning; }
    public double getFoodPoisoningChance() { return foodPoisoningChance; }
    public boolean isFoodSpoil() { return foodSpoil; }
    public double getFoodSpoilChance() { return foodSpoilChance; }

    public int getShieldDurabilityPerBlock() { return shieldDurabilityPerBlock; }

    public boolean isOreDropChance() { return oreDropChance; }
    public double getOreDropChanceValue() { return oreDropChanceValue; }

    public boolean isStoneDropWithPickaxe() { return stoneDropWithPickaxe; }
    public double getStoneDropChanceWooden() { return stoneDropChanceWooden; }
    public double getStoneDropChanceStone() { return stoneDropChanceStone; }

    public boolean isHayBlockSilkTouch() { return hayBlockSilkTouch; }
    public int getHayBlockToWheat() { return hayBlockToWheat; }

    public boolean isTntBreakExplodes() { return tntBreakExplodes; }
    public double getTntBreakExplodesChance() { return tntBreakExplodesChance; }

    public boolean isSilverfishSpawnFromStone() { return silverfishSpawnFromStone; }
    public double getSilverfishSpawnChance() { return silverfishSpawnChance; }

    public boolean isEndermiteSpawnFromEndStone() { return endermiteSpawnFromEndStone; }
    public double getEndermiteSpawnChance() { return endermiteSpawnChance; }
    public boolean isEndermiteSpawnFromEnderman() { return endermiteSpawnFromEnderman; }

    public boolean isInfestedStoneSpawnsFangs() { return infestedStoneSpawnsFangs; }
    public double getInfestedStoneFangsChance() { return infestedStoneFangsChance; }

    public boolean isEyeOfEnderExplode() { return eyeOfEnderExplode; }
    public int getEyeOfEnderMaxUses() { return eyeOfEnderMaxUses; }

    public boolean isVillagerTradesExpensive() { return villagerTradesExpensive; }
    public double getVillagerTradeMultiplier() { return villagerTradeMultiplier; }
    public boolean isVillagerBadOmen() { return villagerBadOmen; }
    public double getVillagerBadOmenChance() { return villagerBadOmenChance; }

    public boolean isDesertTempleHusks() { return desertTempleHusks; }
    public int getDesertTempleHuskCount() { return desertTempleHuskCount; }
    public boolean isDesertTempleTnt() { return desertTempleTnt; }
    public boolean isShipwreckDrowned() { return shipwreckDrowned; }
    public int getShipwreckDrownedCount() { return shipwreckDrownedCount; }
    public boolean isBuriedTreasureVex() { return buriedTreasureVex; }
    public int getBuriedTreasureVexCount() { return buriedTreasureVexCount; }
    public boolean isRuinedPortalIllusioner() { return ruinedPortalIllusioner; }
    public int getRuinedPortalIllusionerCount() { return ruinedPortalIllusionerCount; }
    public boolean isJunglePyramidIllusioner() { return junglePyramidIllusioner; }
    public int getJunglePyramidIllusionerCount() { return junglePyramidIllusionerCount; }

    public boolean isWanderingTraderReplace() { return wanderingTraderReplace; }
    public boolean isWanderingTraderRavager() { return wanderingTraderRavager; }

    public boolean isPiglinAlwaysAngry() { return piglinAlwaysAngry; }
    public boolean isPiglinFullGoldImmune() { return piglinFullGoldImmune; }

    public boolean isHeavyArmor(Material material) { return heavyArmorMaterials.contains(material); }
    public boolean isTool(Material material) { return toolMaterials.contains(material); }

    public boolean isMobEnabled(String mob) {
        return config.getBoolean("mobs." + mob + ".enabled", true);
    }
}
