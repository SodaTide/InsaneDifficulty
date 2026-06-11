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
    
    @Override
    public void onEnable() {
        getLogger().info("正在启用 BraveSurvival 插件...");
        
        // 初始化配置
        ConfigManager.initialize(getDataFolder());
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        
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
}
