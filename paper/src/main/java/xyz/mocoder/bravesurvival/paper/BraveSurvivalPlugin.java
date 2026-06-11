package xyz.mocoder.bravesurvival.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.*;
import org.bukkit.event.world.*;
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
        getLogger().info("BraveSurvival 插件已禁用！");
    }
    
    /**
     * 设置游戏规则
     */
    private void setupGameRules() {
        // 无自然再生
        if (ConfigManager.isNaturalRegenerationDisabled()) {
            getServer().getWorlds().forEach(world -> {
                world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            });
        }
        
        // 通用愤怒
        if (ConfigManager.isUniversalAnger()) {
            getServer().getWorlds().forEach(world -> {
                world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, false);
                world.setGameRule(GameRule.UNIVERSAL_ANGER, true);
            });
        }
    }
    
    /**
     * 监听怪物生成事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        LivingEntity entity = event.getEntity();
        PaperEntityWrapper wrapper = new PaperEntityWrapper(entity);
        
        // 根据实体类型应用强化
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
        }
    }
    
    /**
     * 强化僵尸
     */
    private void enhanceZombie(Zombie zombie, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("zombie")) return;
        
        // 设置属性
        wrapper.setMaxHealth(ConfigManager.getMobAttribute("zombie", "health", 20.0));
        wrapper.setAttackDamage(ConfigManager.getMobAttribute("zombie", "damage", 5.0));
        wrapper.setMovementSpeed(ConfigManager.getMobAttribute("zombie", "speed", 0.23));
        wrapper.setFollowRange(ConfigManager.getMobAttribute("zombie", "follow_range", 64.0));
        
        // 火焰抗性
        if (ConfigManager.getMobConfig("zombie").has("fire_resistance") && 
            ConfigManager.getMobConfig("zombie").get("fire_resistance").getAsBoolean()) {
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        }
        
        // 不在日光下燃烧
        if (!ConfigManager.getMobConfig("zombie").has("burn_in_daylight") || 
            !ConfigManager.getMobConfig("zombie").get("burn_in_daylight").getAsBoolean()) {
            zombie.setShouldBurnInDay(false);
        }
        
        // 50%概率生成装备
        if (ConfigManager.getMobConfig("zombie").has("enhanced_armor") && 
            ConfigManager.getMobConfig("zombie").get("enhanced_armor").getAsBoolean()) {
            generateRandomArmor(zombie);
        }
    }
    
    /**
     * 强化苦力怕
     */
    private void enhanceCreeper(Creeper creeper, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("creeper")) return;
        
        // 高压状态
        if (ConfigManager.getMobConfig("creeper").has("always_charged") && 
            ConfigManager.getMobConfig("creeper").get("always_charged").getAsBoolean()) {
            creeper.setPowered(true);
        }
        
        // 瞬间爆炸
        if (ConfigManager.getMobConfig("creeper").has("instant_fuse") && 
            ConfigManager.getMobConfig("creeper").get("instant_fuse").getAsBoolean()) {
            creeper.setMaxFuseTicks(1);
        }
        
        // 爆炸半径
        if (ConfigManager.getMobConfig("creeper").has("explosion_radius")) {
            creeper.setExplosionRadius(ConfigManager.getMobConfig("creeper").get("explosion_radius").getAsInt());
        }
        
        // 隐形
        if (ConfigManager.getMobConfig("creeper").has("invisible") && 
            ConfigManager.getMobConfig("creeper").get("invisible").getAsBoolean()) {
            creeper.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }
        
        // 移动速度
        if (ConfigManager.getMobConfig("creeper").has("speed")) {
            wrapper.setMovementSpeed(ConfigManager.getMobConfig("creeper").get("speed").getAsDouble());
        }
    }
    
    /**
     * 强化骷髅
     */
    private void enhanceSkeleton(Skeleton skeleton, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("skeleton")) return;
        
        // 设置属性
        wrapper.setAttackDamage(ConfigManager.getMobAttribute("skeleton", "damage", 10.0));
        
        // 不在日光下燃烧
        if (!ConfigManager.getMobConfig("skeleton").has("burn_in_daylight") || 
            !ConfigManager.getMobConfig("skeleton").get("burn_in_daylight").getAsBoolean()) {
            skeleton.setShouldBurnInDay(false);
        }
        
        // 装备
        if (ConfigManager.getMobConfig("skeleton").has("enhanced_armor") && 
            ConfigManager.getMobConfig("skeleton").get("enhanced_armor").getAsBoolean()) {
            generateSkeletonEquipment(skeleton);
        }
    }
    
    /**
     * 强化蜘蛛
     */
    private void enhanceSpider(Spider spider, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("spider")) return;
        
        // 隐形
        if (ConfigManager.getMobConfig("spider").has("invisible") && 
            ConfigManager.getMobConfig("spider").get("invisible").getAsBoolean()) {
            spider.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }
        
        // 速度
        if (ConfigManager.getMobConfig("spider").has("speed")) {
            wrapper.setMovementSpeed(ConfigManager.getMobConfig("spider").get("speed").getAsDouble());
        }
    }
    
    /**
     * 强化末影人
     */
    private void enhanceEnderman(Enderman enderman, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("enderman")) return;
        
        // 设置属性
        wrapper.setMaxHealth(ConfigManager.getMobAttribute("enderman", "health", 40.0));
        wrapper.setAttackDamage(ConfigManager.getMobAttribute("enderman", "damage", 7.0));
        wrapper.setMovementSpeed(ConfigManager.getMobAttribute("enderman", "speed", 0.3));
        wrapper.setFollowRange(ConfigManager.getMobAttribute("enderman", "follow_range", 64.0));
    }
    
    /**
     * 强化幻翼
     */
    private void enhancePhantom(Phantom phantom, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("phantom")) return;
        
        // 不在日光下燃烧
        if (!ConfigManager.getMobConfig("phantom").has("burn_in_daylight") || 
            !ConfigManager.getMobConfig("phantom").get("burn_in_daylight").getAsBoolean()) {
            phantom.setShouldBurnInDay(false);
        }
        
        // 伤害
        if (ConfigManager.getMobConfig("phantom").has("damage")) {
            wrapper.setAttackDamage(ConfigManager.getMobConfig("phantom").get("damage").getAsDouble());
        }
    }
    
    /**
     * 强化烈焰人
     */
    private void enhanceBlaze(Blaze blaze, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("blaze")) return;
        
        // 火焰抗性
        blaze.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
    }
    
    /**
     * 强化恶魂
     */
    private void enhanceGhast(Ghast ghast, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("ghast")) return;
        
        // 隐形
        if (ConfigManager.getMobConfig("ghast").has("invisible") && 
            ConfigManager.getMobConfig("ghast").get("invisible").getAsBoolean()) {
            ghast.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        }
    }
    
    /**
     * 强化铁傀儡
     */
    private void enhanceIronGolem(IronGolem golem, PaperEntityWrapper wrapper) {
        if (!ConfigManager.isMobEnabled("iron_golem")) return;
        
        // 增加生命值
        if (ConfigManager.getMobConfig("iron_golem").has("increased_health") && 
            ConfigManager.getMobConfig("iron_golem").get("increased_health").getAsBoolean()) {
            wrapper.setMaxHealth(200.0);
        }
    }
    
    /**
     * 生成随机装备
     */
    private void generateRandomArmor(Mob mob) {
        EntityEquipment equipment = mob.getEquipment();
        if (equipment == null) return;
        
        // 50%概率生成每个部位
        if (random.nextDouble() < 0.5) {
            equipment.setHelmet(getRandomHelmet());
        }
        if (random.nextDouble() < 0.5) {
            equipment.setChestplate(getRandomChestplate());
        }
        if (random.nextDouble() < 0.5) {
            equipment.setLeggings(getRandomLeggings());
        }
        if (random.nextDouble() < 0.5) {
            equipment.setBoots(getRandomBoots());
        }
        if (random.nextDouble() < 0.5) {
            equipment.setItemInMainHand(getRandomSword());
        }
    }
    
    /**
     * 生成骷髅装备
     */
    private void generateSkeletonEquipment(Skeleton skeleton) {
        EntityEquipment equipment = skeleton.getEquipment();
        if (equipment == null) return;
        
        // 下界合金头盔
        if (ConfigManager.getMobConfig("skeleton").has("netherite_helmet") && 
            ConfigManager.getMobConfig("skeleton").get("netherite_helmet").getAsBoolean()) {
            equipment.setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        }
        
        // 弓
        equipment.setItemInMainHand(new ItemStack(Material.BOW));
    }
    
    /**
     * 获取随机头盔
     */
    private ItemStack getRandomHelmet() {
        Material[] helmets = {
            Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET
        };
        return new ItemStack(helmets[random.nextInt(helmets.length)]);
    }
    
    /**
     * 获取随机胸甲
     */
    private ItemStack getRandomChestplate() {
        Material[] chestplates = {
            Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE
        };
        return new ItemStack(chestplates[random.nextInt(chestplates.length)]);
    }
    
    /**
     * 获取随机护腿
     */
    private ItemStack getRandomLeggings() {
        Material[] leggings = {
            Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS
        };
        return new ItemStack(leggings[random.nextInt(leggings.length)]);
    }
    
    /**
     * 获取随机靴子
     */
    private ItemStack getRandomBoots() {
        Material[] boots = {
            Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS,
            Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS
        };
        return new ItemStack(boots[random.nextInt(boots.length)]);
    }
    
    /**
     * 获取随机剑
     */
    private ItemStack getRandomSword() {
        Material[] swords = {
            Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.GOLDEN_SWORD
        };
        return new ItemStack(swords[random.nextInt(swords.length)]);
    }
    
    /**
     * 监听玩家伤害事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player) {
            // 摔落伤害debuff
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (ConfigManager.getPlayerConfig().has("fall_damage_debuff") && 
                    ConfigManager.getPlayerConfig().get("fall_damage_debuff").getAsBoolean()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.isOnline()) {
                                int weaknessDuration = ConfigManager.getPlayerConfig().has("weakness_duration") ? 
                                    ConfigManager.getPlayerConfig().get("weakness_duration").getAsInt() : 10;
                                int slownessDuration = ConfigManager.getPlayerConfig().has("slowness_duration") ? 
                                    ConfigManager.getPlayerConfig().get("slowness_duration").getAsInt() : 10;
                                
                                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration * 20, 0, false, false));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slownessDuration * 20, 0, false, false));
                            }
                        }
                    }.runTaskLater(this, 1L);
                }
            }
        }
    }
    
    /**
     * 监听实体攻击事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 蜘蛛攻击附带中毒
        if (event.getDamager() instanceof Spider && event.getEntity() instanceof Player) {
            if (ConfigManager.getMobConfig("spider").has("poison_on_hit") && 
                ConfigManager.getMobConfig("spider").get("poison_on_hit").getAsBoolean()) {
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false));
            }
        }
    }
    
    /**
     * 监听方块破坏事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        // 检查是否应该生成蠹虫
        String blockType = event.getBlock().getType().toString();
        if (WorldLogic.shouldSpawnSilverfish(blockType)) {
            if (Math.random() < WorldLogic.getSilverfishChance()) {
                event.getBlock().getWorld().spawnEntity(
                    event.getBlock().getLocation().add(0.5, 0, 0.5),
                    EntityType.SILVERFISH
                );
            }
        }
        
        // TNT破坏爆炸
        if (event.getBlock().getType() == Material.TNT) {
            if (Math.random() < MobEnhancer.getTntBreakExplodesChance()) {
                event.getBlock().getWorld().createExplosion(
                    event.getBlock().getLocation(), 
                    4.0F, 
                    true
                );
            }
        }
    }
    
    /**
     * 监听玩家移动事件
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 检查是否移动了方块
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        // 移动掉落物品
        if (ConfigManager.getPlayerConfig().has("drop_items_on_move") && 
            ConfigManager.getPlayerConfig().get("drop_items_on_move").getAsBoolean()) {
            if (Math.random() < 0.001) { // 0.1%概率
                Player player = event.getPlayer();
                ItemStack[] contents = player.getInventory().getContents();
                for (int i = 0; i < contents.length; i++) {
                    if (contents[i] != null && contents[i].getType() != Material.AIR) {
                        player.getWorld().dropItemNaturally(player.getLocation(), contents[i]);
                        player.getInventory().setItem(i, null);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * 监听天气变化事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChange(WeatherChangeEvent event) {
        // 随机雷暴
        if (ConfigManager.isRandomLightningStorms()) {
            if (event.toWeatherState() && Math.random() < 0.1) {
                // 增加雷暴概率
                event.getWorld().setThundering(true);
                event.getWorld().setThunderDuration(6000); // 5分钟
            }
        }
    }
    
    /**
     * 监听睡觉事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.isCancelled()) return;
        
        // 睡醒遇到幻翼
        if (ConfigManager.isWakeUpToPhantoms()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = event.getPlayer();
                    if (player.isOnline()) {
                        // 生成幻翼
                        for (int i = 0; i < 3; i++) {
                            player.getWorld().spawnEntity(
                                player.getLocation().add(
                                    random.nextInt(10) - 5,
                                    5,
                                    random.nextInt(10) - 5
                                ),
                                EntityType.PHANTOM
                            );
                        }
                        // 给予饥饿和缓慢
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0, false, false));
                    }
                }
            }.runTaskLater(this, 100L); // 5秒后
        }
    }
    
    /**
     * 监听玩家饥饿事件
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player) {
            // 饥饿效果
            if (ConfigManager.getPlayerConfig().has("hunger_effects") && 
                ConfigManager.getPlayerConfig().get("hunger_effects").getAsBoolean()) {
                if (event.getFoodLevel() < 6) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0, false, false));
                }
            }
        }
    }
    
    /**
     * 监听实体受伤事件 - 受伤掉落物品
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForItemDrop(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player) {
            // 受伤掉落物品
            if (ConfigManager.getPlayerConfig().has("drop_items_on_hit") && 
                ConfigManager.getPlayerConfig().get("drop_items_on_hit").getAsBoolean()) {
                if (Math.random() < 0.3) { // 30%概率
                    ItemStack[] contents = player.getInventory().getContents();
                    for (int i = 0; i < contents.length; i++) {
                        if (contents[i] != null && contents[i].getType() != Material.AIR) {
                            player.getWorld().dropItemNaturally(player.getLocation(), contents[i]);
                            player.getInventory().setItem(i, null);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 监听实体攻击事件 - 箭矢误射
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;
        
        // 箭矢误射
        if (event.getEntity() instanceof Arrow arrow) {
            if (ConfigManager.getCombatConfig().has("arrows_misfire_chance")) {
                double chance = ConfigManager.getCombatConfig().get("arrows_misfire_chance").getAsDouble();
                if (Math.random() < chance) {
                    // 改变箭矢方向
                    arrow.setVelocity(arrow.getVelocity().rotateAroundY(Math.toRadians(random.nextInt(90) - 45)));
                }
            }
        }
    }
    
    /**
     * 监听末影珍珠使用
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        // 末影珍珠生成末影螨
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            if (ConfigManager.getCombatConfig().has("ender_pearls_always_spawn_endermites") && 
                ConfigManager.getCombatConfig().get("ender_pearls_always_spawn_endermites").getAsBoolean()) {
                // 延迟生成末影螨
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
    
    /**
     * 监听实体爆炸事件 - 末地水晶反射弹射物
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        
        // 末地水晶爆炸
        if (event.getEntity() instanceof EnderCrystal) {
            if (ConfigManager.getCombatConfig().has("ender_crystals_reflect_projectiles") && 
                ConfigManager.getCombatConfig().get("ender_crystals_reflect_projectiles").getAsBoolean()) {
                // 反射附近的弹射物
                event.getEntity().getNearbyEntities(10, 10, 10).stream()
                    .filter(e -> e instanceof Projectile)
                    .forEach(e -> {
                        Projectile projectile = (Projectile) e;
                        projectile.setVelocity(projectile.getVelocity().multiply(-1));
                    });
            }
        }
    }
    
    /**
     * 监听玩家使用图腾事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (event.isCancelled()) return;
        
        // 图腾削弱
        if (ConfigManager.getCombatConfig().has("totem_nerfed_drop") && 
            ConfigManager.getCombatConfig().get("totem_nerfed_drop").getAsBoolean()) {
            if (event.getEntity() instanceof Player player) {
                // 削弱图腾效果 - 移除再生效果
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            }
        }
    }
    
    /**
     * 监听玩家进入村庄事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveForVillage(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 进入村庄获得不祥之兆
        if (ConfigManager.getCombatConfig().has("bad_omen_on_village_enter") && 
            ConfigManager.getCombatConfig().get("bad_omen_on_village_enter").getAsBoolean()) {
            if (Math.random() < 0.001) { // 0.1%概率
                Player player = event.getPlayer();
                if (player.getLocation().getBlock().getBiome().toString().contains("village")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 6000, 0, false, false));
                }
            }
        }
    }
    
    /**
     * 监听物品使用事件 - 烈焰棒/岩浆桶烧伤
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        
        // 腐肉给予更多饥饿值
        if (event.getItem().getType() == Material.ROTTEN_FLESH) {
            if (ConfigManager.getItemsConfig().has("rotten_flesh_more_hunger") && 
                ConfigManager.getItemsConfig().get("rotten_flesh_more_hunger").getAsBoolean()) {
                event.getPlayer().setFoodLevel(Math.min(20, event.getPlayer().getFoodLevel() + 4));
            }
        }
        
        // 生食给予饥饿和反胃
        if (isRawFood(event.getItem().getType())) {
            if (ConfigManager.getItemsConfig().has("raw_food_hunger_and_nausea") && 
                ConfigManager.getItemsConfig().get("raw_food_hunger_and_nausea").getAsBoolean()) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0, false, false));
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0, false, false));
            }
        }
        
        // 肉类变质
        if (isMeat(event.getItem().getType())) {
            if (ConfigManager.getItemsConfig().has("meat_spoil_chance")) {
                double chance = ConfigManager.getItemsConfig().get("meat_spoil_chance").getAsDouble();
                if (Math.random() < chance) {
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1, false, false));
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false));
                }
            }
        }
        
        // 食物中毒
        if (ConfigManager.getItemsConfig().has("food_poisoning_chance")) {
            double chance = ConfigManager.getItemsConfig().get("food_poisoning_chance").getAsDouble();
            if (Math.random() < chance) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0, false, false));
            }
        }
    }
    
    /**
     * 检查是否是生食
     */
    private boolean isRawFood(Material material) {
        return material == Material.BEEF || material == Material.PORKCHOP || 
               material == Material.CHICKEN || material == Material.MUTTON ||
               material == Material.RABBIT || material == Material.COD ||
               material == Material.SALMON;
    }
    
    /**
     * 检查是否是肉类
     */
    private boolean isMeat(Material material) {
        return material == Material.COOKED_BEEF || material == Material.COOKED_PORKCHOP ||
               material == Material.COOKED_CHICKEN || material == Material.COOKED_MUTTON ||
               material == Material.COOKED_RABBIT || material == Material.COOKED_COD ||
               material == Material.COOKED_SALMON;
    }
    
    /**
     * 监听玩家使用物品事件 - 烈焰棒/岩浆桶烧伤
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractForBurn(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getItem() == null) return;
        
        // 烈焰棒烧伤
        if (event.getItem().getType() == Material.BLAZE_ROD) {
            if (ConfigManager.getItemsConfig().has("blaze_rod_burns") && 
                ConfigManager.getItemsConfig().get("blaze_rod_burns").getAsBoolean()) {
                event.getPlayer().setFireTicks(60); // 3秒着火
            }
        }
        
        // 岩浆桶烧伤
        if (event.getItem().getType() == Material.LAVA_BUCKET) {
            if (ConfigManager.getItemsConfig().has("lava_bucket_burns") && 
                ConfigManager.getItemsConfig().get("lava_bucket_burns").getAsBoolean()) {
                event.getPlayer().setFireTicks(100); // 5秒着火
            }
        }
    }
    
    /**
     * 监听方块放置事件 - 门在水中损坏
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        // 门在水中损坏
        if (ConfigManager.isDoorsBreakInWater()) {
            if (isDoor(event.getBlock().getType())) {
                // 检查是否在水中
                if (event.getBlock().getType() == Material.WATER) {
                    event.getBlock().breakNaturally();
                }
            }
        }
    }
    
    /**
     * 检查是否是门
     */
    private boolean isDoor(Material material) {
        return material == Material.OAK_DOOR || material == Material.SPRUCE_DOOR ||
               material == Material.BIRCH_DOOR || material == Material.JUNGLE_DOOR ||
               material == Material.ACACIA_DOOR || material == Material.DARK_OAK_DOOR ||
               material == Material.CRIMSON_DOOR || material == Material.WARPED_DOOR ||
               material == Material.IRON_DOOR;
    }
    
    /**
     * 监听玩家游泳事件 - 溺水效果
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForDrowning(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 溺水效果
        if (ConfigManager.isDrowningEffects()) {
            Player player = event.getPlayer();
            if (player.isUnderWater()) {
                if (player.getRemainingAir() < player.getMaximumAir() / 2) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false));
                }
            }
        }
    }
    
    /**
     * 监听玩家传送事件 - 传送门损坏
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        
        // 传送门损坏
        if (ConfigManager.isPortalBreakChance()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                if (Math.random() < 0.1) { // 10%概率
                    // 延迟破坏传送门
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Player player = event.getPlayer();
                            if (player.isOnline()) {
                                // 在玩家周围寻找并破坏黑曜石
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
    
    /**
     * 监听玩家睡觉事件 - 睡觉跳过部分夜晚
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnterForSleep(PlayerBedEnterEvent event) {
        if (event.isCancelled()) return;
        
        // 睡觉跳过1/3夜晚
        if (ConfigManager.isSleepSkipsPartOfNight()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = event.getPlayer();
                    if (player.isOnline() && player.isSleeping()) {
                        org.bukkit.World world = player.getWorld();
                        long currentTime = world.getTime();
                        long newTime = currentTime + 2400; // 跳过1/3夜晚
                        world.setTime(newTime);
                    }
                }
            }.runTaskLater(this, 60L); // 3秒后
        }
    }
    
    /**
     * 监听实体生成事件 - 猪灵敌对判断
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForPiglin(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 猪灵敌对判断 - 使用目标设置
        if (event.getEntity() instanceof Piglin piglin) {
            if (ConfigManager.getMobConfig("piglin").has("always_angry_unless_full_gold") && 
                ConfigManager.getMobConfig("piglin").get("always_angry_unless_full_gold").getAsBoolean()) {
                // 查找最近的玩家并设置为目标
                Player nearestPlayer = null;
                double closestDistance = 16.0;
                for (Player player : piglin.getWorld().getPlayers()) {
                    double distance = player.getLocation().distance(piglin.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        nearestPlayer = player;
                    }
                }
                if (nearestPlayer != null) {
                    piglin.setTarget(nearestPlayer);
                }
            }
        }
    }
    
    /**
     * 监听玩家装备事件 - 重甲减速
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForArmor(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 重甲减速
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
    
    /**
     * 检查是否是重甲
     */
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
    
    /**
     * 监听玩家使用物品事件 - 盾牌耐久度
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.isCancelled()) return;
        
        // 盾牌耐久度消耗加倍
        if (event.getItem().getType() == Material.SHIELD) {
            if (ConfigManager.getItemsConfig().has("shield_durability_multiplier")) {
                double multiplier = ConfigManager.getItemsConfig().get("shield_durability_multiplier").getAsDouble();
                event.setDamage((int) (event.getDamage() * multiplier));
            }
        }
    }
    
    /**
     * 监听实体伤害事件 - 岩浆伤害
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForLava(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        
        // 岩浆伤害
        if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            if (ConfigManager.isLavaHeatDamage()) {
                if (event.getEntity() instanceof Player player) {
                    // 额外伤害
                    player.damage(2.0);
                    player.setFireTicks(100); // 5秒着火
                }
            }
        }
        
        // 仙人掌中毒
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
            if (event.getEntity() instanceof Player player) {
                if (ConfigManager.isCactusPoison()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, false, false));
                }
            }
        }
    }
    
    /**
     * 监听实体生成事件 - 溺尸三叉戟
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForDrowned(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 所有溺尸都有三叉戟
        if (event.getEntity() instanceof Drowned drowned) {
            if (ConfigManager.getMobConfig("zombie").has("drowned_always_trident") && 
                ConfigManager.getMobConfig("zombie").get("drowned_always_trident").getAsBoolean()) {
                drowned.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
            }
        }
    }
    
    /**
     * 监听实体伤害事件 - 尸壳给予饥饿
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForHusk(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 尸壳给予饥饿
        if (event.getDamager() instanceof Husk && event.getEntity() instanceof Player player) {
            if (ConfigManager.getMobConfig("zombie").has("husk_hunger") && 
                ConfigManager.getMobConfig("zombie").get("husk_hunger").getAsBoolean()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1, false, false));
            }
        }
        
        // 流浪者给予缓慢
        if (event.getDamager() instanceof Stray && event.getEntity() instanceof Player player) {
            if (ConfigManager.getMobConfig("skeleton").has("stray_slowness") && 
                ConfigManager.getMobConfig("skeleton").get("stray_slowness").getAsBoolean()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 1, false, false));
            }
        }
        
        // 蜜蜂更快中毒
        if (event.getDamager() instanceof Bee && event.getEntity() instanceof Player player) {
            if (ConfigManager.getMobConfig("bee") != null && 
                ConfigManager.getMobConfig("bee").has("faster_poison") && 
                ConfigManager.getMobConfig("bee").get("faster_poison").getAsBoolean()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, false, false));
            }
        }
    }
    
    /**
     * 监听方块破坏事件 - 干草块/矿石掉落
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForDrops(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        // 干草块没有精准采集不掉落
        if (event.getBlock().getType() == Material.HAY_BLOCK) {
            if (ConfigManager.getBlocksConfig().has("hay_block_no_drop_without_silk_touch") && 
                ConfigManager.getBlocksConfig().get("hay_block_no_drop_without_silk_touch").getAsBoolean()) {
                if (!event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(org.bukkit.enchantments.Enchantment.SILK_TOUCH)) {
                    event.setDropItems(false);
                }
            }
        }
        
        // 主世界矿石不总是掉落
        if (isOverworldOre(event.getBlock().getType())) {
            if (ConfigManager.getBlocksConfig().has("ore_drop_chance")) {
                double chance = ConfigManager.getBlocksConfig().get("ore_drop_chance").getAsDouble();
                if (Math.random() > chance) {
                    event.setDropItems(false);
                }
            }
        }
        
        // 挖掘黑曜石给予重度饥饿
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            if (ConfigManager.getBlocksConfig().has("obsidian_heavy_hunger") && 
                ConfigManager.getBlocksConfig().get("obsidian_heavy_hunger").getAsBoolean()) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 2, false, false));
            }
        }
        
        // 挖末地石生成末影螨
        if (event.getBlock().getType() == Material.END_STONE) {
            if (ConfigManager.getMobConfig("endermite").has("spawn_from_end_stone") && 
                ConfigManager.getMobConfig("endermite").get("spawn_from_end_stone").getAsBoolean()) {
                if (Math.random() < 0.1) { // 10%概率
                    event.getBlock().getWorld().spawnEntity(
                        event.getBlock().getLocation().add(0.5, 0, 0.5),
                        EntityType.ENDERMITE
                    );
                }
            }
        }
    }
    
    /**
     * 检查是否是主世界矿石
     */
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
    
    /**
     * 监听实体死亡事件 - 末影人死亡生成末影螨
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeathForEndermite(EntityDeathEvent event) {
        if (event.getEntity() instanceof Enderman enderman) {
            if (ConfigManager.getMobConfig("endermite").has("spawn_from_dead_endermen") && 
                ConfigManager.getMobConfig("endermite").get("spawn_from_dead_endermen").getAsBoolean()) {
                // 生成末影螨
                enderman.getWorld().spawnEntity(enderman.getLocation(), EntityType.ENDERMITE);
            }
        }
    }
    
    /**
     * 监听实体攻击事件 - 末影人攻击后传送
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForEnderman(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 末影人攻击后传送
        if (event.getDamager() instanceof Enderman enderman && event.getEntity() instanceof Player) {
            if (ConfigManager.getMobConfig("enderman").has("teleport_after_hit") && 
                ConfigManager.getMobConfig("enderman").get("teleport_after_hit").getAsBoolean()) {
                // 延迟传送
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (enderman.isValid()) {
                            // 随机传送
                            double x = enderman.getLocation().getX() + (random.nextInt(20) - 10);
                            double z = enderman.getLocation().getZ() + (random.nextInt(20) - 10);
                            double y = enderman.getWorld().getHighestBlockYAt((int) x, (int) z);
                            enderman.teleport(new org.bukkit.Location(enderman.getWorld(), x, y, z));
                        }
                    }
                }.runTaskLater(this, 5L);
            }
        }
    }
    
    /**
     * 监听玩家死亡事件 - 死亡生成强大僵尸
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathForZombie(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // 死亡生成强大僵尸
        if (ConfigManager.getPlayerConfig().has("death_spawn_zombie") && 
            ConfigManager.getPlayerConfig().get("death_spawn_zombie").getAsBoolean()) {
            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
            zombie.setCustomName("§c§l死亡僵尸");
            zombie.setCustomNameVisible(true);
            // 设置强大属性
            PaperEntityWrapper wrapper = new PaperEntityWrapper(zombie);
            wrapper.setMaxHealth(50.0);
            wrapper.setAttackDamage(10.0);
            wrapper.setMovementSpeed(0.3);
            // 给予装备
            zombie.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            zombie.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            zombie.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            zombie.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        }
    }
    
    /**
     * 监听实体爆炸事件 - 末地爆炸辐射
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplodeForRadiation(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        
        // 末地爆炸辐射
        if (event.getEntity().getWorld().getEnvironment() == org.bukkit.World.Environment.THE_END) {
            if (ConfigManager.getCombatConfig().has("explosion_radiation_in_end") && 
                ConfigManager.getCombatConfig().get("explosion_radiation_in_end").getAsBoolean()) {
                // 对附近的玩家造成伤害
                event.getEntity().getNearbyEntities(10, 10, 10).stream()
                    .filter(e -> e instanceof Player)
                    .forEach(e -> {
                        Player player = (Player) e;
                        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0, false, false));
                    });
            }
        }
    }
    
    /**
     * 监听药水效果事件 - 药水有副作用
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.isCancelled()) return;
        
        // 药水有副作用
        if (ConfigManager.getCombatConfig().has("potion_side_effects") && 
            ConfigManager.getCombatConfig().get("potion_side_effects").getAsBoolean()) {
            if (Math.random() < 0.2) { // 20%概率
                // 给予随机负面效果
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
    
    /**
     * 监听实体交互事件 - 马踢下玩家
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityInteractForHorse(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 马踢下玩家
        if (event.getRightClicked() instanceof AbstractHorse horse) {
            if (ConfigManager.getMobConfig("horse") != null && 
                ConfigManager.getMobConfig("horse").has("kick_chance")) {
                double chance = ConfigManager.getMobConfig("horse").get("kick_chance").getAsDouble();
                if (Math.random() < chance) {
                    horse.eject();
                    event.getPlayer().sendMessage("§c马把你踢下来了！");
                }
            }
        }
    }
    
    /**
     * 监听实体生成事件 - 鸡变成骷髅骑士
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForChicken(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 鸡变成骷髅骑士
        if (event.getEntity() instanceof Chicken chicken) {
            if (ConfigManager.getMobConfig("chicken") != null && 
                ConfigManager.getMobConfig("chicken").has("turn_into_jockey")) {
                // 查找附近的玩家
                Player nearestPlayer = null;
                double closestDistance = 16.0;
                for (Player player : chicken.getWorld().getPlayers()) {
                    double distance = player.getLocation().distance(chicken.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        nearestPlayer = player;
                    }
                }
                if (nearestPlayer != null) {
                    // 生成骷髅骑士
                    Skeleton skeleton = chicken.getWorld().spawn(chicken.getLocation(), Skeleton.class);
                    skeleton.addPassenger(chicken);
                }
            }
        }
        
        // 猪变成疣猪兽
        if (event.getEntity() instanceof Pig pig) {
            if (ConfigManager.getMobConfig("pig") != null && 
                ConfigManager.getMobConfig("pig").has("turn_into_hoglin")) {
                // 查找附近的玩家
                Player nearestPlayer = null;
                double closestDistance = 16.0;
                for (Player player : pig.getWorld().getPlayers()) {
                    double distance = player.getLocation().distance(pig.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        nearestPlayer = player;
                    }
                }
                if (nearestPlayer != null) {
                    // 变成疣猪兽
                    pig.getWorld().spawn(pig.getLocation(), Hoglin.class);
                    pig.remove();
                }
            }
        }
        
        // 海豚变成鳕鱼
        if (event.getEntity() instanceof Dolphin dolphin) {
            if (ConfigManager.getMobConfig("dolphin") != null && 
                ConfigManager.getMobConfig("dolphin").has("turn_into_cod")) {
                // 查找附近的玩家
                Player nearestPlayer = null;
                double closestDistance = 16.0;
                for (Player player : dolphin.getWorld().getPlayers()) {
                    double distance = player.getLocation().distance(dolphin.getLocation());
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        nearestPlayer = player;
                    }
                }
                if (nearestPlayer != null) {
                    // 变成鳕鱼
                    dolphin.getWorld().spawn(dolphin.getLocation(), Cod.class);
                    dolphin.remove();
                }
            }
        }
    }
    
    /**
     * 监听方块放置事件 - 踩被虫蚀石头召唤唤魔者尖牙
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveForInfested(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 踩被虫蚀石头召唤唤魔者尖牙
        if (ConfigManager.getBlocksConfig().has("infested_stone_spawns_evoker_fangs") && 
            ConfigManager.getBlocksConfig().get("infested_stone_spawns_evoker_fangs").getAsBoolean()) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                
                org.bukkit.block.Block blockBelow = event.getTo().getBlock().getRelative(0, -1, 0);
                if (blockBelow.getType() == Material.INFESTED_STONE || 
                    blockBelow.getType() == Material.INFESTED_COBBLESTONE ||
                    blockBelow.getType() == Material.INFESTED_DEEPSLATE) {
                    // 生成唤魔者尖牙
                    event.getPlayer().getWorld().spawnEntity(
                        event.getPlayer().getLocation(),
                        EntityType.EVOKER_FANGS
                    );
                }
            }
        }
    }
    
    /**
     * 监听船移动事件 - 船沉没
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleMoveForSink(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Boat boat) {
            // 检查是否有玩家乘客
            if (boat.getPassengers().size() > 0 && boat.getPassengers().get(0) instanceof Player) {
                // 船沉没
                if (ConfigManager.getBoatConfig().has("sink_after_ticks")) {
                    int sinkTicks = ConfigManager.getBoatConfig().get("sink_after_ticks").getAsInt();
                    // 这里需要实现船沉没逻辑
                    // 由于Paper API限制，可能需要更复杂的实现
                }
            }
        }
    }
    
    /**
     * 监听玩家使用物品事件 - 划船消耗饥饿
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForBoatHunger(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 划船消耗饥饿
        if (ConfigManager.getBoatConfig().has("hunger_from_rowing") && 
            ConfigManager.getBoatConfig().get("hunger_from_rowing").getAsBoolean()) {
            if (event.getPlayer().isInsideVehicle() && event.getPlayer().getVehicle() instanceof Boat) {
                if (Math.random() < 0.01) { // 1%概率
                    event.getPlayer().setFoodLevel(Math.max(0, event.getPlayer().getFoodLevel() - 1));
                }
            }
        }
    }
    
    /**
     * 监听实体生成事件 - 恶魂三只生成
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForGhast(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 恶魂三只生成
        if (event.getEntity() instanceof Ghast ghast) {
            if (ConfigManager.getMobConfig("ghast").has("spawn_in_triples") && 
                ConfigManager.getMobConfig("ghast").get("spawn_in_triples").getAsBoolean()) {
                // 生成额外两只恶魂
                ghast.getWorld().spawn(ghast.getLocation().add(5, 0, 0), Ghast.class);
                ghast.getWorld().spawn(ghast.getLocation().add(0, 0, 5), Ghast.class);
            }
        }
    }
    
    /**
     * 监听实体生成事件 - 守卫者与鱼一起生成
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForGuardian(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 守卫者与鱼一起生成
        if (event.getEntity() instanceof Guardian guardian) {
            if (ConfigManager.getMobConfig("guardian") != null && 
                ConfigManager.getMobConfig("guardian").has("spawn_with_fish")) {
                // 生成鱼
                for (int i = 0; i < 3; i++) {
                    guardian.getWorld().spawnEntity(
                        guardian.getLocation().add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2),
                        EntityType.COD
                    );
                }
            }
        }
    }
}
