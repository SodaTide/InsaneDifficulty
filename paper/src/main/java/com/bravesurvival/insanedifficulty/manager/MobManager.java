package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.EntityUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MobManager {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public MobManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    private boolean spawningPair = false;

    public void tick() {
        processAngryMobs();
        processPassiveMobs();
        processFishSpawner();
        processIronGolems();
        processTallMobs();
        processWitches();
        processGhasts();
        processWanderingTraders();
    }

    private void processAngryMobs() {
        for (World world : plugin.getServer().getWorlds()) {
            // 狼
            for (Wolf wolf : world.getEntitiesByClass(Wolf.class)) {
                if (!config.isMobEnabled("wolf")) continue;
                Player nearest = EntityUtil.getNearestPlayer(wolf, 16);
                if (nearest != null) wolf.setAngry(true);
            }

            // 蜜蜂
            for (Bee bee : world.getEntitiesByClass(Bee.class)) {
                if (!config.isMobEnabled("bee")) continue;
                Player nearest = EntityUtil.getNearestPlayer(bee, 16);
                if (nearest != null && !bee.hasStung()) bee.setTarget(nearest);
            }

            // 僵尸猪灵
            for (PigZombie pigman : world.getEntitiesByClass(PigZombie.class)) {
                if (!config.isMobEnabled("zombified_piglin")) continue;
                Player nearest = EntityUtil.getNearestPlayer(pigman, 32);
                if (nearest != null) {
                    pigman.setAnger(200);
                    pigman.setTarget(nearest);
                }
            }

            // 猪灵
            for (Piglin piglin : world.getEntitiesByClass(Piglin.class)) {
                if (!config.isMobEnabled("piglin")) continue;
                Player nearest = EntityUtil.getNearestPlayer(piglin, 16);
                if (nearest != null && !EntityUtil.isWearingFullGold(nearest)) {
                    piglin.setTarget(nearest);
                }
            }

            // 铁傀儡
            for (IronGolem golem : world.getEntitiesByClass(IronGolem.class)) {
                if (!config.isMobEnabled("iron_golem")) continue;
                Player nearest = EntityUtil.getNearestPlayer(golem, 16);
                if (nearest != null && golem.getTarget() == null) {
                    golem.setTarget(nearest);
                }
            }

            // 北极熊
            for (PolarBear bear : world.getEntitiesByClass(PolarBear.class)) {
                if (!config.isMobEnabled("polar_bear")) continue;
                Player nearest = EntityUtil.getNearestPlayer(bear, 16);
                if (nearest != null && bear.getTarget() == null) {
                    bear.setTarget(nearest);
                }
            }

            // 末影人
            for (Enderman enderman : world.getEntitiesByClass(Enderman.class)) {
                if (!config.isMobEnabled("enderman")) continue;
                Player nearest = EntityUtil.getNearestPlayer(enderman, 16);
                if (nearest != null && enderman.getTarget() == null) {
                    enderman.setTarget(nearest);
                }
            }
        }
    }

    private void processPassiveMobs() {
        for (World world : plugin.getServer().getWorlds()) {
            // 牛变兔子
            if (config.isCowTurnToRabbit()) {
                for (Cow cow : new java.util.ArrayList<>(world.getEntitiesByClass(Cow.class))) {
                    if (EntityUtil.getNearestPlayer(cow, config.getPassiveTransformRange()) != null) {
                        world.spawnEntity(cow.getLocation(), EntityType.RABBIT);
                        cow.remove();
                    }
                }
            }

            // 羊变兔子
            if (config.isSheepTurnToRabbit()) {
                for (Sheep sheep : new java.util.ArrayList<>(world.getEntitiesByClass(Sheep.class))) {
                    if (EntityUtil.getNearestPlayer(sheep, config.getPassiveTransformRange()) != null) {
                        world.spawnEntity(sheep.getLocation(), EntityType.RABBIT);
                        sheep.remove();
                    }
                }
            }

            // 海豚变鳕鱼
            if (config.isDolphinTurnToCod()) {
                for (Dolphin dolphin : new java.util.ArrayList<>(world.getEntitiesByClass(Dolphin.class))) {
                    if (EntityUtil.getNearestPlayer(dolphin, 10) != null) {
                        world.spawnEntity(dolphin.getLocation(), EntityType.COD);
                        dolphin.remove();
                    }
                }
            }

            // 猪变疣猪兽
            if (config.isPigTurnToHoglin()) {
                for (Pig pig : new java.util.ArrayList<>(world.getEntitiesByClass(Pig.class))) {
                    if (EntityUtil.getNearestPlayer(pig, config.getPassiveTransformRange()) != null) {
                        Hoglin hoglin = (Hoglin) world.spawnEntity(pig.getLocation(), EntityType.HOGLIN);
                        EntityUtil.setAttribute(hoglin, Attribute.MOVEMENT_SPEED, 0.35);
                        hoglin.setImmuneToZombification(true);
                        pig.remove();
                    }
                }
            }

            // 鸡变僵尸骑士
            if (config.isChickenTurnToJockey()) {
                for (Chicken chicken : new java.util.ArrayList<>(world.getEntitiesByClass(Chicken.class))) {
                    if (chicken.hasMetadata("jockey")) continue;
                    if (EntityUtil.getNearestPlayer(chicken, 16) != null) {
                        // 生成僵尸骑士（小僵尸骑在鸡上）
                        Zombie baby = (Zombie) world.spawnEntity(chicken.getLocation(), EntityType.ZOMBIE);
                        baby.setBaby(true);
                        chicken.addPassenger(baby);
                        chicken.setMetadata("jockey", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                        EntityUtil.setAttribute(chicken, Attribute.MAX_HEALTH, 10.0);
                        EntityUtil.setAttribute(chicken, Attribute.MOVEMENT_SPEED, 0.4);
                    }
                }
            }
        }
    }

    private void processFishSpawner() {
        if (!config.isFishTurnToGuardian()) return;
        for (World world : plugin.getServer().getWorlds()) {
            for (Cod cod : new java.util.ArrayList<>(world.getEntitiesByClass(Cod.class))) {
                if (!cod.hasMetadata("checked") && !cod.hasMetadata("transformed")) {
                    cod.setMetadata("checked", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                    if (RNG.chance(config.getFishTransformChance())) {
                        world.spawnEntity(cod.getLocation(), EntityType.GUARDIAN);
                        cod.remove();
                    }
                }
            }
            for (Salmon salmon : new java.util.ArrayList<>(world.getEntitiesByClass(Salmon.class))) {
                if (!salmon.hasMetadata("checked") && !salmon.hasMetadata("transformed")) {
                    salmon.setMetadata("checked", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                    if (RNG.chance(config.getFishTransformChance())) {
                        world.spawnEntity(salmon.getLocation(), EntityType.GUARDIAN);
                        salmon.remove();
                    }
                }
            }
            for (TropicalFish tropical : new java.util.ArrayList<>(world.getEntitiesByClass(TropicalFish.class))) {
                if (!tropical.hasMetadata("checked") && !tropical.hasMetadata("transformed")) {
                    tropical.setMetadata("checked", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                    if (RNG.chance(config.getFishTransformChance())) {
                        world.spawnEntity(tropical.getLocation(), EntityType.GUARDIAN);
                        tropical.remove();
                    }
                }
            }
        }
    }

    private void processIronGolems() {
        for (World world : plugin.getServer().getWorlds()) {
            for (IronGolem golem : world.getEntitiesByClass(IronGolem.class)) {
                Player nearest = EntityUtil.getNearestPlayer(golem, 5);
                if (nearest != null && nearest.getGameMode() != org.bukkit.GameMode.CREATIVE && nearest.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
                    if (RNG.chance(config.getIronGolemSlamChance())) {
                        golem.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 7, 8, false, false));
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            if (!golem.isDead()) {
                                // 给铁傀儡自身抗性（不保护玩家）
                                EntityUtil.addEffect(golem, PotionEffectType.RESISTANCE, 40, 4);
                                golem.getWorld().createExplosion(golem.getLocation(), 4.0F, false);
                            }
                        }, 22L);
                    }
                }
            }
        }
    }

    private void processTallMobs() {
        // 凋零骷髅破坏头部方块
        if (config.isWitherSkeletonDestroyBlocks()) {
            for (World world : plugin.getServer().getWorlds()) {
                for (WitherSkeleton ws : world.getEntitiesByClass(WitherSkeleton.class)) {
                    Player nearest = EntityUtil.getNearestPlayer(ws, 5);
                    if (nearest != null) {
                        org.bukkit.Location headLoc = ws.getLocation().add(0, 2.6, 0);
                        org.bukkit.block.Block headBlock = headLoc.getBlock();
                        if (!isHeadBreakImmune(headBlock.getType())) {
                            headBlock.breakNaturally();
                        }
                    }
                }
            }
        }

        // 末影人破坏头部方块
        if (config.isEndermanDestroyBlocksNearHead()) {
            for (World world : plugin.getServer().getWorlds()) {
                for (Enderman enderman : world.getEntitiesByClass(Enderman.class)) {
                    Player nearest = EntityUtil.getNearestPlayer(enderman, 5);
                    if (nearest != null) {
                        org.bukkit.Location headLoc = enderman.getLocation().add(0, 2.6, 0);
                        org.bukkit.block.Block headBlock = headLoc.getBlock();
                        if (!isHeadBreakImmune(headBlock.getType())) {
                            headBlock.breakNaturally();
                        }
                    }
                }
            }
        }
    }

    private void processWitches() {
        if (!config.isWitchWitherSkulls()) return;
        for (World world : plugin.getServer().getWorlds()) {
            for (Witch witch : world.getEntitiesByClass(Witch.class)) {
                Player nearest = EntityUtil.getNearestPlayer(witch, 12);
                if (nearest != null && RNG.chance(0.015)) {
                    org.bukkit.Location spawnLoc = witch.getLocation().add(0, 1, 0);
                    WitherSkull skull = world.spawn(spawnLoc, WitherSkull.class, sk -> {
                        sk.setCharged(true);
                        org.bukkit.util.Vector dir = nearest.getEyeLocation().toVector().subtract(spawnLoc.toVector()).normalize();
                        sk.setVelocity(dir.multiply(0.8));
                    });
                }
            }
        }
    }

    private void processGhasts() {
        // 恶魂三倍生成
        for (World world : plugin.getServer().getWorlds()) {
            for (Ghast ghast : world.getEntitiesByClass(Ghast.class)) {
                if (!ghast.hasMetadata("spawned_extra")) {
                    ghast.setMetadata("spawned_extra", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                    // 生成额外2个恶魂
                    for (int i = 0; i < 2; i++) {
                        Ghast extra = (Ghast) world.spawnEntity(ghast.getLocation(), EntityType.GHAST);
                        extra.setMetadata("spawned_extra", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                        extra.setExplosionPower(6);
                        extra.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    }
                }
            }
        }
    }

    private void processWanderingTraders() {
        if (!config.isWanderingTraderReplace()) return;
        for (World world : plugin.getServer().getWorlds()) {
            for (WanderingTrader trader : new java.util.ArrayList<>(world.getEntitiesByClass(WanderingTrader.class))) {
                if (trader.hasMetadata("converted")) continue;
                trader.setMetadata("converted", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                org.bukkit.Location traderLoc = trader.getLocation().clone();

                if (config.isWanderingTraderRavager()) {
                    Ravager ravager = (Ravager) world.spawnEntity(traderLoc, EntityType.RAVAGER);
                    Pillager pillager = (Pillager) world.spawnEntity(traderLoc, EntityType.PILLAGER);
                    ravager.addPassenger(pillager);
                } else {
                    if (RNG.chance(0.5)) {
                        world.spawnEntity(traderLoc, EntityType.PILLAGER);
                    } else {
                        world.spawnEntity(traderLoc, EntityType.VINDICATOR);
                    }
                }
                trader.remove();

                // 行商羊驼转化
                for (TraderLlama llama : new java.util.ArrayList<>(world.getEntitiesByClass(TraderLlama.class))) {
                    if (llama.getLocation().distanceSquared(traderLoc) < 100) {
                        if (RNG.chance(0.5)) {
                            world.spawnEntity(llama.getLocation(), EntityType.PILLAGER);
                        } else {
                            world.spawnEntity(llama.getLocation(), EntityType.VINDICATOR);
                        }
                        llama.remove();
                    }
                }
            }
        }
    }

    private boolean isHeadBreakImmune(Material material) {
        return material == Material.BEDROCK || material == Material.BARRIER ||
               material == Material.COMMAND_BLOCK || material == Material.REPEATING_COMMAND_BLOCK ||
               material == Material.CHAIN_COMMAND_BLOCK || material == Material.END_PORTAL_FRAME ||
               material == Material.END_PORTAL || material == Material.END_GATEWAY ||
               material == Material.SPAWNER || material == Material.DRAGON_EGG ||
               material == Material.NETHERITE_BLOCK || material == Material.ANCIENT_DEBRIS ||
               material == Material.CRYING_OBSIDIAN || material == Material.OBSIDIAN ||
               material == Material.NETHER_PORTAL || material == Material.END_CRYSTAL ||
               material == Material.BEACON || material == Material.ENCHANTING_TABLE;
    }

    public void onCreatureSpawn(LivingEntity entity) {
        if (entity instanceof Zombie zombie) {
            if (!config.isMobEnabled("zombie")) return;
            EntityUtil.setAttribute(zombie, Attribute.FOLLOW_RANGE, 64.0);
            EntityUtil.setAttribute(zombie, Attribute.ATTACK_DAMAGE, 5.0);
            zombie.setShouldBurnInDay(false);
            AttributeInstance reinforcementsAttr = zombie.getAttribute(Attribute.SPAWN_REINFORCEMENTS);
            if (reinforcementsAttr != null) {
                reinforcementsAttr.setBaseValue(0.15);
            }
            EntityUtil.addEffect(zombie, PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);
            if (zombie instanceof Drowned drowned) {
                drowned.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
            }
            if (RNG.chance(0.5)) EntityUtil.generateRandomArmor(zombie);
        } else if (entity instanceof Creeper bukkitCreeper) {
            if (!config.isMobEnabled("creeper")) return;
            if (config.isCreeperPowered()) bukkitCreeper.setPowered(true);
            bukkitCreeper.setMaxFuseTicks(config.getCreeperFuseTicks());
            bukkitCreeper.setExplosionRadius(config.getCreeperExplosionRadius());
            EntityUtil.setAttribute(bukkitCreeper, Attribute.MOVEMENT_SPEED, config.getCreeperSpeed());
            EntityUtil.addEffect(bukkitCreeper, PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
        } else if (entity instanceof Skeleton skeleton) {
            if (!config.isMobEnabled("skeleton")) return;
            EntityEquipment eq = skeleton.getEquipment();
            if (eq != null) {
                if (config.isSkeletonNetheriteHelmet()) {
                    ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
                    EntityUtil.setItemUnbreakable(helmet);
                    eq.setHelmet(helmet);
                }
                if (config.isSkeletonPowerBow()) {
                    ItemStack bow = new ItemStack(Material.BOW);
                    EntityUtil.addEnchant(bow, org.bukkit.enchantments.Enchantment.POWER, 5);
                    EntityUtil.addEnchant(bow, org.bukkit.enchantments.Enchantment.PUNCH, 2);
                    eq.setItemInMainHand(bow);
                }
            }
        } else if (entity instanceof Spider spider) {
            if (!config.isMobEnabled("spider")) return;
            EntityUtil.setAttribute(spider, Attribute.MOVEMENT_SPEED, config.getSpiderSpeed());
            if (config.isSpiderInvisible()) {
                EntityUtil.addEffect(spider, PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
            }
        } else if (entity instanceof Phantom phantom) {
            if (!config.isMobEnabled("phantom")) return;
            if (config.isPhantomFireResist()) {
                EntityUtil.addEffect(phantom, PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);
            }
            EntityUtil.setAttribute(phantom, Attribute.ATTACK_DAMAGE, config.getPhantomDamage());
        } else if (entity instanceof Blaze blaze) {
            if (!config.isMobEnabled("blaze")) return;
            EntityUtil.setAttribute(blaze, Attribute.MOVEMENT_SPEED, config.getBlazeSpeed());
            if (config.isBlazeNoGravity()) {
                blaze.setGravity(false);
            }
        } else if (entity instanceof Ghast ghast) {
            if (!config.isMobEnabled("ghast")) return;
            ghast.setExplosionPower(6);
            EntityUtil.addEffect(ghast, PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
        } else if (entity instanceof IronGolem golem) {
            if (config.isMobEnabled("iron_golem")) {
                EntityUtil.setAttribute(golem, Attribute.MAX_HEALTH, config.getIronGolemHealth());
                golem.setHealth(config.getIronGolemHealth());
            }
        } else if (entity instanceof Silverfish sf) {
            if (!config.isMobEnabled("silverfish")) return;
            EntityUtil.setAttribute(sf, Attribute.KNOCKBACK_RESISTANCE, 1.0);
            EntityUtil.setAttribute(sf, Attribute.MOVEMENT_SPEED, 0.4);
        } else if (entity instanceof Endermite em) {
            if (!config.isMobEnabled("endermite")) return;
            EntityUtil.setAttribute(em, Attribute.KNOCKBACK_RESISTANCE, 1.0);
            EntityUtil.setAttribute(em, Attribute.MOVEMENT_SPEED, 0.4);
        } else if (entity instanceof WitherSkeleton ws) {
            if (!config.isMobEnabled("wither_skeleton")) return;
            EntityEquipment eq = ws.getEquipment();
            if (eq != null) {
                eq.setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                eq.setItemInOffHand(new ItemStack(Material.STONE_PICKAXE));
            }
            EntityUtil.setAttribute(ws, Attribute.MAX_HEALTH, config.getWitherSkeletonHealth());
            EntityUtil.setAttribute(ws, Attribute.ATTACK_DAMAGE, config.getWitherSkeletonDamage());
            if (RNG.chance(config.getWitherSkeletonWitchChance())) {
                ws.getWorld().spawnEntity(ws.getLocation(), EntityType.WITCH);
            }
        } else if (entity instanceof Pillager pillager) {
            if (!config.isMobEnabled("pillager")) return;
            EntityEquipment eq = pillager.getEquipment();
            if (eq != null) {
                ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                if (config.isPillagerQuickCharge5()) {
                    EntityUtil.addEnchant(crossbow, org.bukkit.enchantments.Enchantment.QUICK_CHARGE, 5);
                }
                if (config.isPillagerMultishot()) {
                    EntityUtil.addEnchant(crossbow, org.bukkit.enchantments.Enchantment.MULTISHOT, 1);
                }
                eq.setItemInMainHand(crossbow);
            }
            if (config.isPillagerSpawnInPairs() && !pillager.hasMetadata("paired") && !spawningPair) {
                pillager.setMetadata("paired", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                spawningPair = true;
                try {
                    Pillager pair = (Pillager) pillager.getWorld().spawnEntity(pillager.getLocation(), EntityType.PILLAGER);
                    pair.setMetadata("paired", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                } finally {
                    spawningPair = false;
                }
            }
        } else if (entity instanceof Ravager ravager) {
            if (config.isPillagerRavagerWith() && ravager.getPassengers().isEmpty()) {
                Entity passenger = ravager.getWorld().spawnEntity(ravager.getLocation(), EntityType.PILLAGER);
                ravager.addPassenger(passenger);
            }
        } else if (entity instanceof Illusioner ill) {
            if (!config.isMobEnabled("illusioner")) return;
            EntityEquipment eq = ill.getEquipment();
            if (eq != null) {
                ItemStack bow = new ItemStack(Material.BOW);
                if (config.isIllusionerPowerBow()) {
                    EntityUtil.addEnchant(bow, org.bukkit.enchantments.Enchantment.POWER, 5);
                }
                eq.setItemInMainHand(bow);
            }
        }

        // 设置所有敌对生物追踪距离（仅对非僵尸的怪物，因为僵尸已单独设置64）
        if (entity instanceof Monster monster && !(entity instanceof Zombie)) {
            EntityUtil.setAttribute(monster, Attribute.FOLLOW_RANGE, 32.0);
        }
    }
}
