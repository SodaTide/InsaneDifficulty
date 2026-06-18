package xyz.mocoder.bravesurvival.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.Material;
import org.bukkit.GameRule;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import xyz.mocoder.bravesurvival.core.config.ConfigManager;
import xyz.mocoder.bravesurvival.core.logic.mob.MobEnhancer;
import xyz.mocoder.bravesurvival.core.logic.player.PlayerLogic;
import xyz.mocoder.bravesurvival.core.logic.world.WorldLogic;
import xyz.mocoder.bravesurvival.paper.entity.PaperEntityWrapper;

import java.util.Random;

/**
 * BraveSurvival Paper插件主类
 */
public class BraveSurvivalPlugin extends JavaPlugin implements Listener {

    private final Random random = new Random();
    private ConfigManager configManager;
    private RecipeManager recipeManager;
    private VillagerTradeManager villagerTradeManager;
    private EntityBehaviorManager entityBehaviorManager;
    private AdvancedFeatureManager advancedFeatureManager;
    private MobTransformManager mobTransformManager;
    private EnvironmentManager environmentManager;
    private FoodSystemManager foodSystemManager;

    @Override
    public void onEnable() {
        getLogger().info("正在启用 BraveSurvival 插件...");

        // 初始化配置
        ConfigManager.initialize(getDataFolder());
        configManager = new ConfigManager();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);

        // 注册配方管理器
        recipeManager = new RecipeManager(this);
        recipeManager.registerRecipes();

        // 注册村民交易管理器
        villagerTradeManager = new VillagerTradeManager(this);
        getServer().getPluginManager().registerEvents(villagerTradeManager, this);

        // 注册实体行为管理器
        entityBehaviorManager = new EntityBehaviorManager(this);
        getServer().getPluginManager().registerEvents(entityBehaviorManager, this);

        // 注册高级功能管理器
        advancedFeatureManager = new AdvancedFeatureManager(this);
        advancedFeatureManager.initialize();

        // 注册生物变形管理器
        mobTransformManager = new MobTransformManager(this);
        mobTransformManager.initialize();

        // 注册环境管理器
        environmentManager = new EnvironmentManager(this);
        environmentManager.initialize();
        environmentManager.registerHayBlockRecipe();

        // 注册食物系统管理器
        foodSystemManager = new FoodSystemManager(this);
        foodSystemManager.initialize();

        // 注册命令
        getCommand("bravesurvival").setExecutor((sender, command, label, args) -> {
            if (args.length > 0 && args[0].equals("reload")) {
                ConfigManager.reloadConfig();
                sender.sendMessage("§a配置已重新加载！");
                return true;
            }
            sender.sendMessage("§eBraveSurvival 插件 v" + getDescription().getVersion());
            sender.sendMessage("§e使用 /bravesurvival reload 重新加载配置");
            return true;
        });

        // 设置游戏规则
        setupGameRules();

        getLogger().info("BraveSurvival 插件启用成功！");
    }

    /**
     * 获取配置管理器
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        getLogger().info("BraveSurvival 插件已禁用！");
    }

    /**
     * 设置游戏规则
     */
    private void setupGameRules() {
        // 强制困难难度
        getServer().getWorlds().forEach(world -> {
            world.setDifficulty(org.bukkit.Difficulty.HARD);
        });

        if (ConfigManager.isNaturalRegenerationDisabled()) {
            getServer().getWorlds().forEach(world -> {
                world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            });
        }

        if (ConfigManager.isUniversalAnger()) {
            getServer().getWorlds().forEach(world -> {
                world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, false);
                world.setGameRule(GameRule.UNIVERSAL_ANGER, true);
            });
        }

        if (ConfigManager.getWorldConfig().has("reduced_debug_info") &&
            ConfigManager.getWorldConfig().get("reduced_debug_info").getAsBoolean()) {
            getServer().getWorlds().forEach(world -> {
                world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            });
        }

        // 隐藏死亡信息
        getServer().getWorlds().forEach(world -> {
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        });
    }

    // ==================== 怪物强化 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        LivingEntity entity = event.getEntity();
        PaperEntityWrapper wrapper = new PaperEntityWrapper(entity);

        if (entity instanceof Zombie zombie) {
            enhanceZombie(zombie, wrapper);
        } else if (entity instanceof Creeper creeper) {
            enhanceCreeper(creeper, wrapper);
        } else if (entity instanceof Skeleton skeleton) {
            enhanceSkeleton(skeleton, wrapper);
        } else if (entity instanceof Spider spider) {
            enhanceSpider(spider, wrapper);
        } else if (entity instanceof Enderman enderman) {
            enhanceEnderman(enderman, wrapper);
        } else if (entity instanceof Phantom phantom) {
            enhancePhantom(phantom, wrapper);
        } else if (entity instanceof Blaze blaze) {
            enhanceBlaze(blaze, wrapper);
        } else if (entity instanceof Ghast ghast) {
            enhanceGhast(ghast, wrapper);
        } else if (entity instanceof IronGolem golem) {
            enhanceIronGolem(golem, wrapper);
        } else if (entity instanceof Silverfish silverfish) {
            // 银鱼：抗击退 + 快速 (数据包: knockback_resistance=1, speed=0.4)
            wrapper.setKnockbackResistance(1.0);
            wrapper.setMovementSpeed(0.4);
        } else if (entity instanceof Endermite endermite) {
            // 末影螨：抗击退 + 快速 (数据包: knockback_resistance=1, speed=0.4)
            wrapper.setKnockbackResistance(1.0);
            wrapper.setMovementSpeed(0.4);
        }
    }

    private void enhanceZombie(Zombie zombie, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("zombie")) return;

        wrapper.setMaxHealth(ConfigManager.getMobAttribute("zombie", "health", 20.0));
        wrapper.setAttackDamage(ConfigManager.getMobAttribute("zombie", "damage", 5.0));
        wrapper.setMovementSpeed(ConfigManager.getMobAttribute("zombie", "speed", 0.23));
        wrapper.setFollowRange(ConfigManager.getMobAttribute("zombie", "follow_range", 64.0));
        wrapper.setArmor(ConfigManager.getMobAttribute("zombie", "armor", 3.0));

        if (ConfigManager.getMobConfig("zombie").has("fire_resistance") &&
            ConfigManager.getMobConfig("zombie").get("fire_resistance").getAsBoolean()) {
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        }

        if (!ConfigManager.getMobConfig("zombie").has("burn_in_daylight") ||
            !ConfigManager.getMobConfig("zombie").get("burn_in_daylight").getAsBoolean()) {
            zombie.setShouldBurnInDay(false);
        }

        if (ConfigManager.getMobConfig("zombie").has("enhanced_armor") &&
            ConfigManager.getMobConfig("zombie").get("enhanced_armor").getAsBoolean()) {
            generateRandomArmor(zombie);
        }
    }

    private void enhanceCreeper(Creeper creeper, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("creeper")) return;

        if (ConfigManager.getMobConfig("creeper").has("always_charged") &&
            ConfigManager.getMobConfig("creeper").get("always_charged").getAsBoolean()) {
            creeper.setPowered(true);
        }

        if (ConfigManager.getMobConfig("creeper").has("fuse_ticks")) {
            creeper.setMaxFuseTicks(ConfigManager.getMobConfig("creeper").get("fuse_ticks").getAsInt());
        }

        // 修复：爆炸半径设为10（数据包值）
        if (ConfigManager.getMobConfig("creeper").has("explosion_radius")) {
            creeper.setExplosionRadius(ConfigManager.getMobConfig("creeper").get("explosion_radius").getAsInt());
        } else {
            creeper.setExplosionRadius(10);
        }

        if (ConfigManager.getMobConfig("creeper").has("invisible") &&
            ConfigManager.getMobConfig("creeper").get("invisible").getAsBoolean()) {
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }

        if (ConfigManager.getMobConfig("creeper").has("speed")) {
            wrapper.setMovementSpeed(ConfigManager.getMobConfig("creeper").get("speed").getAsDouble());
        } else {
            wrapper.setMovementSpeed(0.3);
        }
    }

    private void enhanceSkeleton(Skeleton skeleton, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("skeleton")) return;

        wrapper.setAttackDamage(ConfigManager.getMobAttribute("skeleton", "damage", 10.0));

        if (!ConfigManager.getMobConfig("skeleton").has("burn_in_daylight") ||
            !ConfigManager.getMobConfig("skeleton").get("burn_in_daylight").getAsBoolean()) {
            skeleton.setShouldBurnInDay(false);
        }

        if (ConfigManager.getMobConfig("skeleton").has("enhanced_armor") &&
            ConfigManager.getMobConfig("skeleton").get("enhanced_armor").getAsBoolean()) {
            generateSkeletonEquipment(skeleton);
        }
    }

    private void enhanceSpider(Spider spider, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("spider")) return;

        if (ConfigManager.getMobConfig("spider").has("invisible") &&
            ConfigManager.getMobConfig("spider").get("invisible").getAsBoolean()) {
            spider.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }

        if (ConfigManager.getMobConfig("spider").has("speed")) {
            wrapper.setMovementSpeed(ConfigManager.getMobConfig("spider").get("speed").getAsDouble());
        }
    }

    private void enhanceEnderman(Enderman enderman, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("enderman")) return;

        wrapper.setMaxHealth(ConfigManager.getMobAttribute("enderman", "health", 40.0));
        wrapper.setAttackDamage(ConfigManager.getMobAttribute("enderman", "damage", 14.0));
        wrapper.setMovementSpeed(ConfigManager.getMobAttribute("enderman", "speed", 0.75));
        wrapper.setFollowRange(ConfigManager.getMobAttribute("enderman", "follow_range", 64.0));
    }

    private void enhancePhantom(Phantom phantom, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("phantom")) return;

        // 幻翼免疫火焰
        phantom.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        phantom.setShouldBurnInDay(false);

        if (ConfigManager.getMobConfig("phantom").has("damage")) {
            wrapper.setAttackDamage(ConfigManager.getMobConfig("phantom").get("damage").getAsDouble());
        } else {
            wrapper.setAttackDamage(3.0);
        }
    }

    private void enhanceBlaze(Blaze blaze, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("blaze")) return;
        blaze.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
    }

    private void enhanceGhast(Ghast ghast, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("ghast")) return;

        // 恶魂爆炸威力设为6（数据包值）
        ghast.setExplosionPower(6);

        if (ConfigManager.getMobConfig("ghast").has("invisible") &&
            ConfigManager.getMobConfig("ghast").get("invisible").getAsBoolean()) {
            ghast.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }
    }

    private void enhanceIronGolem(IronGolem golem, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("iron_golem")) return;

        if (ConfigManager.getMobConfig("iron_golem").has("increased_health") &&
            ConfigManager.getMobConfig("iron_golem").get("increased_health").getAsBoolean()) {
            double health = ConfigManager.getMobConfig("iron_golem").has("health_value") ?
                ConfigManager.getMobConfig("iron_golem").get("health_value").getAsDouble() : 200.0;
            wrapper.setMaxHealth(health);
        }
    }

    // ==================== 装备生成 ====================

    private void generateRandomArmor(Mob mob) {
        EntityEquipment equipment = mob.getEquipment();
        if (equipment == null) return;

        if (random.nextDouble() < 0.5) equipment.setHelmet(getRandomHelmet());
        if (random.nextDouble() < 0.5) equipment.setChestplate(getRandomChestplate());
        if (random.nextDouble() < 0.5) equipment.setLeggings(getRandomLeggings());
        if (random.nextDouble() < 0.5) equipment.setBoots(getRandomBoots());
        if (random.nextDouble() < 0.5) equipment.setItemInMainHand(getRandomSword());
    }

    private void generateSkeletonEquipment(Skeleton skeleton) {
        EntityEquipment equipment = skeleton.getEquipment();
        if (equipment == null) return;

        if (ConfigManager.getMobConfig("skeleton").has("netherite_helmet") &&
            ConfigManager.getMobConfig("skeleton").get("netherite_helmet").getAsBoolean()) {
            equipment.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        }
        // 给骷髅力量V弓（数据包值）
        ItemStack bow = new ItemStack(Material.BOW);
        org.bukkit.inventory.meta.ItemMeta meta = bow.getItemMeta();
        if (meta != null) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.POWER, 5, true);
            meta.addEnchant(org.bukkit.enchantments.Enchantment.PUNCH, 2, true);
            bow.setItemMeta(meta);
        }
        equipment.setItemInMainHand(bow);
    }

    private ItemStack getRandomHelmet() {
        Material[] helmets = {Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET};
        return new ItemStack(helmets[random.nextInt(helmets.length)]);
    }

    private ItemStack getRandomChestplate() {
        Material[] chestplates = {Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE};
        return new ItemStack(chestplates[random.nextInt(chestplates.length)]);
    }

    private ItemStack getRandomLeggings() {
        Material[] leggings = {Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS};
        return new ItemStack(leggings[random.nextInt(leggings.length)]);
    }

    private ItemStack getRandomBoots() {
        Material[] boots = {Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS,
            Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS};
        return new ItemStack(boots[random.nextInt(boots.length)]);
    }

    private ItemStack getRandomSword() {
        Material[] swords = {Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD, Material.GOLDEN_SWORD};
        return new ItemStack(swords[random.nextInt(swords.length)]);
    }

    // ==================== 玩家事件 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (ConfigManager.getPlayerConfig().has("fall_damage_debuff") &&
                    ConfigManager.getPlayerConfig().get("fall_damage_debuff").getAsBoolean()) {
                    int weaknessDuration = ConfigManager.getPlayerConfig().has("weakness_duration") ?
                        ConfigManager.getPlayerConfig().get("weakness_duration").getAsInt() : 10;
                    int slownessDuration = ConfigManager.getPlayerConfig().has("slowness_duration") ?
                        ConfigManager.getPlayerConfig().get("slowness_duration").getAsInt() : 10;

                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration * 20, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slownessDuration * 20, 0, false, false));
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                if (ConfigManager.isLavaHeatDamage()) {
                    event.setDamage(event.getDamage() + 2.0);
                    player.setFireTicks(100);
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
                if (ConfigManager.isCactusPoison()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, false, false));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof Spider && event.getEntity() instanceof Player) {
            if (ConfigManager.getMobConfig("spider").has("poison_on_hit") &&
                ConfigManager.getMobConfig("spider").get("poison_on_hit").getAsBoolean()) {
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false));
            }
        }
    }

    // ==================== 方块事件 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        String blockType = event.getBlock().getType().toString();
        if (WorldLogic.shouldSpawnSilverfish(blockType)) {
            if (Math.random() < WorldLogic.getSilverfishChance()) {
                event.getBlock().getWorld().spawnEntity(
                    event.getBlock().getLocation().add(0.5, 0, 0.5),
                    EntityType.SILVERFISH
                );
            }
        }

        if (event.getBlock().getType() == Material.TNT) {
            if (Math.random() < MobEnhancer.getTntBreakExplodesChance()) {
                event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 4.0F, true);
            }
        }

        if (event.getBlock().getType() == Material.HAY_BLOCK) {
            if (ConfigManager.getBlocksConfig().has("hay_block_no_drop_without_silk_touch") &&
                ConfigManager.getBlocksConfig().get("hay_block_no_drop_without_silk_touch").getAsBoolean()) {
                if (!event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(org.bukkit.enchantments.Enchantment.SILK_TOUCH)) {
                    event.setDropItems(false);
                }
            }
        }
    }

    // ==================== 移动事件 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        // 移动掉落物品 - 数据包逻辑：0.083%概率
        if (ConfigManager.getPlayerConfig().has("drop_items_on_move") &&
            ConfigManager.getPlayerConfig().get("drop_items_on_move").getAsBoolean()) {
            double chance = ConfigManager.getPlayerConfig().has("drop_items_on_move_chance") ?
                ConfigManager.getPlayerConfig().get("drop_items_on_move_chance").getAsDouble() : 0.00083;
            if (random.nextDouble() < chance) {
                dropRandomItem(event.getPlayer());
            }
        }
    }

    // ==================== 受伤掉落物品 ====================

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageForItemDrop(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;
        
        // 创造模式和旁观者模式不触发
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE || 
            player.getGameMode() == org.bukkit.GameMode.SPECTATOR) return;
        
        // 忽略自然恢复和经验获取等伤害来源（根据数据包逻辑，只处理普通伤害）
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID ||
            event.getCause() == EntityDamageEvent.DamageCause.WITHER ||
            event.getCause() == EntityDamageEvent.DamageCause.STARVATION) return;

        if (!ConfigManager.getPlayerConfig().has("drop_items_on_hit") ||
            !ConfigManager.getPlayerConfig().get("drop_items_on_hit").getAsBoolean()) {
            return;
        }
        
        double chance = ConfigManager.getPlayerConfig().has("drop_items_on_hit_chance") ?
            ConfigManager.getPlayerConfig().get("drop_items_on_hit_chance").getAsDouble() : 0.5;
        int count = ConfigManager.getPlayerConfig().has("drop_items_on_hit_count") ?
            ConfigManager.getPlayerConfig().get("drop_items_on_hit_count").getAsInt() : 4;

        // 数据包逻辑：4次50%概率，随机选择槽位(0-40)
        for (int i = 0; i < count; i++) {
            if (random.nextDouble() < chance) {
                dropRandomItem(player);
            }
        }
    }

    /**
     * 从玩家背包随机掉落一个物品
     * 模拟数据包的 drop.mcfunction 逻辑
     */
    private void dropRandomItem(Player player) {
        // 随机选择槽位 (0-40，对应背包36格+4装备格)
        int slotIndex = random.nextInt(41);
        
        ItemStack item = null;
        
        if (slotIndex < 36) {
            // 背包槽位 0-35
            item = player.getInventory().getItem(slotIndex);
        } else if (slotIndex == 36) {
            item = player.getInventory().getHelmet();
        } else if (slotIndex == 37) {
            item = player.getInventory().getChestplate();
        } else if (slotIndex == 38) {
            item = player.getInventory().getLeggings();
        } else if (slotIndex == 39) {
            item = player.getInventory().getBoots();
        } else if (slotIndex == 40) {
            item = player.getInventory().getItemInOffHand();
        }
        
        if (item != null && item.getType() != Material.AIR) {
            // 在玩家头顶生成掉落物
            player.getWorld().dropItemNaturally(
                player.getLocation().add(0, 1, 0), 
                item.clone()
            );
            
            // 清除原槽位
            if (slotIndex < 36) {
                player.getInventory().setItem(slotIndex, null);
            } else if (slotIndex == 36) {
                player.getInventory().setHelmet(null);
            } else if (slotIndex == 37) {
                player.getInventory().setChestplate(null);
            } else if (slotIndex == 38) {
                player.getInventory().setLeggings(null);
            } else if (slotIndex == 39) {
                player.getInventory().setBoots(null);
            } else if (slotIndex == 40) {
                player.getInventory().setItemInOffHand(null);
            }
        }
    }

    // ==================== 重甲减速 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForArmor(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        if (ConfigManager.isHeavyArmorSlowness()) {
            Player player = event.getPlayer();
            int armorCount = 0;

            if (isHeavyArmor(player.getInventory().getHelmet())) armorCount++;
            if (isHeavyArmor(player.getInventory().getChestplate())) armorCount++;
            if (isHeavyArmor(player.getInventory().getLeggings())) armorCount++;
            if (isHeavyArmor(player.getInventory().getBoots())) armorCount++;

            if (armorCount > 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, armorCount - 1, false, false));
            }
        }
    }

    private boolean isHeavyArmor(ItemStack item) {
        if (item == null) return false;
        Material material = item.getType();
        return material == Material.IRON_HELMET || material == Material.IRON_CHESTPLATE ||
               material == Material.IRON_LEGGINGS || material == Material.IRON_BOOTS ||
               material == Material.DIAMOND_HELMET || material == Material.DIAMOND_CHESTPLATE ||
               material == Material.DIAMOND_LEGGINGS || material == Material.DIAMOND_BOOTS ||
               material == Material.NETHERITE_HELMET || material == Material.NETHERITE_CHESTPLATE ||
               material == Material.NETHERITE_LEGGINGS || material == Material.NETHERITE_BOOTS;
    }

    // ==================== 箭矢误射 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Arrow arrow) {
            double chance = ConfigManager.getCombatConfig().has("arrows_misfire_chance") ?
                ConfigManager.getCombatConfig().get("arrows_misfire_chance").getAsDouble() : 0.2;
            if (Math.random() < chance) {
                arrow.setVelocity(arrow.getVelocity().rotateAroundY(Math.toRadians(random.nextInt(90) - 45)));
            }
        }
    }

    // ==================== 末影珍珠生成末影螨 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;

        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            if (ConfigManager.getCombatConfig().has("ender_pearls_always_spawn_endermites") &&
                ConfigManager.getCombatConfig().get("ender_pearls_always_spawn_endermites").getAsBoolean()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = event.getPlayer();
                        if (player.isOnline()) {
                            player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMITE);
                        }
                    }
                }.runTaskLater(this, 20L);
            }
        }
    }

    // ==================== 末地水晶反射弹射物 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof EnderCrystal) {
            if (ConfigManager.getCombatConfig().has("ender_crystals_reflect_projectiles") &&
                ConfigManager.getCombatConfig().get("ender_crystals_reflect_projectiles").getAsBoolean()) {
                double range = ConfigManager.getCombatConfig().has("ender_crystals_reflect_range") ?
                    ConfigManager.getCombatConfig().get("ender_crystals_reflect_range").getAsDouble() : 5.0;
                event.getEntity().getNearbyEntities(range, range, range).stream()
                    .filter(e -> e instanceof Projectile)
                    .forEach(e -> {
                        Projectile projectile = (Projectile) e;
                        projectile.setVelocity(projectile.getVelocity().multiply(-1));
                    });
            }
        }
    }

    // ==================== 图腾削弱 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (event.isCancelled()) return;

        if (ConfigManager.getCombatConfig().has("totem_nerfed_drop") &&
            ConfigManager.getCombatConfig().get("totem_nerfed_drop").getAsBoolean()) {
            if (event.getEntity() instanceof Player player) {
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            }
        }
    }

    // ==================== 药水副作用 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.isCancelled()) return;

        if (ConfigManager.getCombatConfig().has("potion_side_effects") &&
            ConfigManager.getCombatConfig().get("potion_side_effects").getAsBoolean()) {
            double chance = ConfigManager.getCombatConfig().has("potion_side_effects_chance") ?
                ConfigManager.getCombatConfig().get("potion_side_effects_chance").getAsDouble() : 0.5;
            if (Math.random() < chance) {
                PotionEffectType[] negativeEffects = {
                    PotionEffectType.POISON, PotionEffectType.WITHER,
                    PotionEffectType.NAUSEA, PotionEffectType.BLINDNESS
                };
                PotionEffectType randomEffect = negativeEffects[random.nextInt(negativeEffects.length)];
                event.getAffectedEntities().forEach(entity -> {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(randomEffect, 100, 0, false, false));
                    }
                });
            }
        }
    }

    // ==================== 末地爆炸辐射 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplodeForRadiation(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity().getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END) {
            if (ConfigManager.getCombatConfig().has("explosion_radiation_in_end") &&
                ConfigManager.getCombatConfig().get("explosion_radiation_in_end").getAsBoolean()) {
                double range = ConfigManager.getCombatConfig().has("explosion_radiation_range") ?
                    ConfigManager.getCombatConfig().get("explosion_radiation_range").getAsDouble() : 10.0;
                event.getEntity().getNearbyEntities(range, range, range).stream()
                    .filter(e -> e instanceof Player)
                    .forEach(e -> {
                        Player player = (Player) e;
                        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0, false, false));
                    });
            }
        }
    }

    // ==================== 盾牌耐久度 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.isCancelled()) return;

        if (event.getItem().getType() == Material.SHIELD) {
            if (ConfigManager.getItemsConfig().has("shield_durability_multiplier")) {
                double multiplier = ConfigManager.getItemsConfig().get("shield_durability_multiplier").getAsDouble();
                event.setDamage((int) (event.getDamage() * multiplier));
            }
        }
    }

    // ==================== 门在水中损坏 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlaceForDoor(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        if (ConfigManager.isDoorsBreakInWater()) {
            if (isDoor(event.getBlock().getType())) {
                if (isAdjacentToWater(event.getBlock())) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (isDoor(event.getBlock().getType()) && isAdjacentToWater(event.getBlock())) {
                                event.getBlock().breakNaturally();
                            }
                        }
                    }.runTaskLater(this, 20L);
                }
            }
        }
    }

    private boolean isDoor(Material material) {
        return material.name().endsWith("_DOOR");
    }

    private boolean isAdjacentToWater(org.bukkit.block.Block block) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (block.getRelative(x, y, z).getType() == Material.WATER) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ==================== 传送门损坏 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;

        if (ConfigManager.isPortalBreakChance()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                double chance = ConfigManager.getWorldConfig().has("portal_break_chance_value") ?
                    ConfigManager.getWorldConfig().get("portal_break_chance_value").getAsDouble() : 0.5;
                if (Math.random() < chance) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player player = event.getPlayer();
                            if (player.isOnline()) {
                                for (int x = -2; x <= 2; x++) {
                                    for (int y = -2; y <= 2; y++) {
                                        for (int z = -2; z <= 2; z++) {
                                            org.bukkit.block.Block block = player.getLocation().getBlock().getRelative(x, y, z);
                                            if (block.getType() == Material.OBSIDIAN) {
                                                if (Math.random() < 0.5) {
                                                    block.breakNaturally();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }.runTaskLater(this, 20L);
                }
            }
        }
    }

    // ==================== 溺尸三叉戟 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForDrowned(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Drowned drowned) {
            if (ConfigManager.getMobConfig("zombie").has("drowned_always_trident") &&
                ConfigManager.getMobConfig("zombie").get("drowned_always_trident").getAsBoolean()) {
                drowned.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
            }
        }
    }

    // ==================== 尸壳/流浪者攻击效果 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForHusk(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof Husk && event.getEntity() instanceof Player player) {
            if (ConfigManager.getMobConfig("zombie").has("husk_hunger") &&
                ConfigManager.getMobConfig("zombie").get("husk_hunger").getAsBoolean()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1, false, false));
            }
        }

        if (event.getDamager() instanceof Stray && event.getEntity() instanceof Player player) {
            if (ConfigManager.getMobConfig("skeleton").has("stray_slowness") &&
                ConfigManager.getMobConfig("skeleton").get("stray_slowness").getAsBoolean()) {
                // 数据包逻辑: slowness 5 4 (5秒, 4级=AMPLIFIER 4)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 4, false, false));
            }
        }
        
        // 末影人攻击玩家后传送走
        if (event.getDamager() instanceof Enderman enderman && event.getEntity() instanceof Player) {
            if (ConfigManager.getMobConfig("enderman").has("teleport_after_hit") &&
                ConfigManager.getMobConfig("enderman").get("teleport_after_hit").getAsBoolean()) {
                Bukkit.getScheduler().runTaskLater(this, () -> {
                    try {
                        if (enderman.isValid() && !enderman.isDead()) {
                            // 数据包逻辑: spreadplayers, 0-24格随机, 对应高度
                            Location eLoc = enderman.getLocation();
                            double angle = random.nextDouble() * Math.PI * 2;
                            double dist = 8 + random.nextDouble() * 16;
                            double newX = eLoc.getX() + Math.cos(angle) * dist;
                            double newZ = eLoc.getZ() + Math.sin(angle) * dist;
                            int newY = eLoc.getWorld().getHighestBlockYAt((int)newX, (int)newZ) + 1;
                            enderman.teleport(new Location(eLoc.getWorld(), newX, newY, newZ));
                        }
                    } catch (Exception e) {}
                }, 5L);
            }
        }
    }

    // ==================== 矿石掉落率 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForOres(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (isOverworldOre(event.getBlock().getType())) {
            if (ConfigManager.getBlocksConfig().has("ore_drop_chance")) {
                double chance = ConfigManager.getBlocksConfig().get("ore_drop_chance").getAsDouble();
                if (Math.random() > chance) {
                    event.setDropItems(false);
                }
            }
        }
    }

    private boolean isOverworldOre(Material material) {
        return material == Material.COAL_ORE || material == Material.DEEPSLATE_COAL_ORE ||
               material == Material.IRON_ORE || material == Material.DEEPSLATE_IRON_ORE ||
               material == Material.GOLD_ORE || material == Material.DEEPSLATE_GOLD_ORE ||
               material == Material.DIAMOND_ORE || material == Material.DEEPSLATE_DIAMOND_ORE ||
               material == Material.EMERALD_ORE || material == Material.DEEPSLATE_EMERALD_ORE ||
               material == Material.LAPIS_ORE || material == Material.DEEPSLATE_LAPIS_ORE ||
               material == Material.REDSTONE_ORE || material == Material.DEEPSLATE_REDSTONE_ORE ||
               material == Material.COPPER_ORE || material == Material.DEEPSLATE_COPPER_ORE;
    }

    // ==================== 末影人死亡生成末影螨 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeathForEndermite(EntityDeathEvent event) {
        if (event.getEntity() instanceof Enderman enderman) {
            if (ConfigManager.getMobConfig("endermite").has("spawn_from_dead_endermen") &&
                ConfigManager.getMobConfig("endermite").get("spawn_from_dead_endermen").getAsBoolean()) {
                enderman.getWorld().spawnEntity(enderman.getLocation(), EntityType.ENDERMITE);
            }
        }
    }

    // ==================== 凋灵骷髅掉落物品 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeathForWitherSkeleton(EntityDeathEvent event) {
        if (event.getEntity() instanceof WitherSkeleton) {
            // 数据包: wither_skeletons/new.mcfunction 设置 hand_items: stone_sword + stone_pickaxe
            // 死亡时掉落石剑和石镐
            event.getDrops().add(new ItemStack(Material.STONE_SWORD));
            event.getDrops().add(new ItemStack(Material.STONE_PICKAXE));
        }
    }

    // ==================== 挖末地石生成末影螨 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForEndStone(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.END_STONE) {
            if (ConfigManager.getMobConfig("endermite").has("spawn_from_end_stone") &&
                ConfigManager.getMobConfig("endermite").get("spawn_from_end_stone").getAsBoolean()) {
                if (Math.random() < 0.1) {
                    event.getBlock().getWorld().spawnEntity(
                        event.getBlock().getLocation().add(0.5, 0, 0.5),
                        EntityType.ENDERMITE
                    );
                }
            }
        }
    }

    // ==================== 被虫蚀石头召唤唤魔者尖牙 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveForInfested(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        if (ConfigManager.getBlocksConfig().has("infested_stone_spawns_evoker_fangs") &&
            ConfigManager.getBlocksConfig().get("infested_stone_spawns_evoker_fangs").getAsBoolean()) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

                org.bukkit.block.Block blockBelow = event.getTo().getBlock().getRelative(0, -1, 0);
                if (blockBelow.getType() == Material.INFESTED_STONE ||
                    blockBelow.getType() == Material.INFESTED_COBBLESTONE ||
                    blockBelow.getType() == Material.INFESTED_DEEPSLATE) {
                    event.getPlayer().getWorld().spawnEntity(
                        event.getPlayer().getLocation(),
                        EntityType.EVOKER_FANGS
                    );
                }
            }
        }
    }

    // ==================== 死亡生成僵尸 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathForZombie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // 检查配置是否启用
        if (!ConfigManager.getPlayerConfig().has("death_spawn_zombie") ||
            !ConfigManager.getPlayerConfig().get("death_spawn_zombie").getAsBoolean()) {
            return;
        }
        
        double chance = ConfigManager.getPlayerConfig().has("death_spawn_zombie_chance") ?
            ConfigManager.getPlayerConfig().get("death_spawn_zombie_chance").getAsDouble() : 0.5;
        
        // 数据包逻辑: 50%概率生成
        if (Math.random() >= chance) {
            return;
        }
        
        Location loc = player.getLocation();
        World world = loc.getWorld();
        
        // 数据包去重逻辑: 4格内不能有同名Grave Zombie
        boolean hasNearbyGraveZombie = false;
        for (Entity entity : world.getNearbyEntities(loc, 4, 4, 4)) {
            if ((entity instanceof Zombie || entity instanceof Drowned) && 
                "Grave Zombie".equals(entity.getCustomName())) {
                hasNearbyGraveZombie = true;
                break;
            }
        }
        if (hasNearbyGraveZombie) return;
        
        // 数据包逻辑: 水中生成溺尸带三叉戟, 陆地上生成僵尸
        if (player.isUnderWater() || loc.getBlock().getType().name().contains("WATER")) {
            // 水中生成溺尸
            Drowned drowned = world.spawn(loc, Drowned.class);
            drowned.setCustomName("Grave Zombie");
            drowned.setCustomNameVisible(false);
            
            // 设置三叉戟
            ItemStack trident = new ItemStack(Material.TRIDENT);
            drowned.getEquipment().setItemInMainHand(trident);
            drowned.getEquipment().setItemInMainHandDropChance(0.085f);
            
            // 设置属性
            PaperEntityWrapper wrapper = new PaperEntityWrapper(drowned);
            wrapper.setMaxHealth(40.0);
            wrapper.setAttackDamage(8.0);
            wrapper.setMovementSpeed(0.3);
        } else {
            // 陆地上生成僵尸
            Zombie zombie = world.spawn(loc, Zombie.class);
            zombie.setCustomName("Grave Zombie");
            zombie.setCustomNameVisible(false);
            
            // 设置属性 - 与数据包一致: 5攻击, 0.4速度, 0.5击退抗性
            PaperEntityWrapper wrapper = new PaperEntityWrapper(zombie);
            wrapper.setMaxHealth(20.0);
            wrapper.setAttackDamage(5.0);
            wrapper.setMovementSpeed(0.4);
            wrapper.setKnockbackResistance(0.5);
            
            // 设置皮革头盔 (如果配置启用)
            if (ConfigManager.getPlayerConfig().has("death_spawn_zombie_leather_helmet") &&
                ConfigManager.getPlayerConfig().get("death_spawn_zombie_leather_helmet").getAsBoolean()) {
                ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                // 设置unbreakable
                org.bukkit.inventory.meta.ItemMeta meta = helmet.getItemMeta();
                if (meta != null) {
                    meta.setUnbreakable(true);
                    helmet.setItemMeta(meta);
                }
                zombie.getEquipment().setHelmet(helmet);
                zombie.getEquipment().setHelmetDropChance(0.085f);
            }
        }
    }
}
