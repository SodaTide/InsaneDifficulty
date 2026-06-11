package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * 缺失功能管理器 - 第一批
 * 实现被动生物变形、中立生物敌对、末影龙增强等核心功能
 */
public class MissingFeaturesManager1 implements Listener {
    
    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();
    
    public MissingFeaturesManager1(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 初始化
     */
    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    // ==================== 被动生物变形 ====================
    
    /**
     * 监听生物生成事件 - 被动生物变形
     * 牛/羊→兔子(8格内有玩家), 海豚→鳕鱼(10格), 猪→疣猪兽, 鸡→骷髅骑手
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForTransformation(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        
        Location loc = event.getLocation();
        World world = loc.getWorld();
        
        // 牛/羊→兔子 (8格内有玩家)
        if (event.getEntity() instanceof Cow || event.getEntity() instanceof Sheep) {
            if (hasPlayerNearby(loc, 8.0)) {
                event.setCancelled(true);
                world.spawn(loc, Rabbit.class);
                return;
            }
        }
        
        // 海豚→鳕鱼 (10格内有玩家)
        if (event.getEntity() instanceof Dolphin) {
            if (hasPlayerNearby(loc, 10.0)) {
                event.setCancelled(true);
                world.spawn(loc, Cod.class);
                return;
            }
        }
        
        // 猪→疣猪兽 (8格内有玩家)
        if (event.getEntity() instanceof Pig) {
            if (hasPlayerNearby(loc, 8.0)) {
                event.setCancelled(true);
                world.spawn(loc, Hoglin.class);
                return;
            }
        }
        
        // 鸡→骷髅骑手 (8格内有玩家)
        if (event.getEntity() instanceof Chicken) {
            if (hasPlayerNearby(loc, 8.0)) {
                event.setCancelled(true);
                Skeleton skeleton = world.spawn(loc, Skeleton.class);
                Chicken chicken = world.spawn(loc, Chicken.class);
                chicken.addPassenger(skeleton);
                return;
            }
        }
        
        // 鱼→守卫者 (33%几率, 16格内有玩家)
        if (isFish(event.getEntity())) {
            if (hasPlayerNearby(loc, 16.0) && random.nextDouble() < 0.33) {
                event.setCancelled(true);
                world.spawn(loc, Guardian.class);
                return;
            }
        }
    }
    
    /**
     * 检查是否是鱼
     */
    private boolean isFish(Entity entity) {
        return entity instanceof Cod || entity instanceof Salmon || 
               entity instanceof TropicalFish;
    }
    
    /**
     * 检查附近是否有玩家
     */
    private boolean hasPlayerNearby(Location location, double distance) {
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distance(location) <= distance) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取最近的玩家
     */
    private Player getNearestPlayer(Location location, double distance) {
        Player nearest = null;
        double minDist = distance;
        for (Player player : location.getWorld().getPlayers()) {
            double dist = player.getLocation().distance(location);
            if (dist < minDist) {
                minDist = dist;
                nearest = player;
            }
        }
        return nearest;
    }
    
    // ==================== 中立生物敌对化 ====================
    
    /**
     * 监听生物生成事件 - 中立生物敌对化
     * 狼/蜜蜂/铁傀儡/北极熊/末影人在16格内敌对；僵尸猪灵在32格内敌对
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForHostile(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        Location loc = event.getLocation();
        
        // 狼敌对 (16格内有玩家)
        if (event.getEntity() instanceof Wolf wolf) {
            if (hasPlayerNearby(loc, 16.0)) {
                wolf.setAngry(true);
            }
        }
        
        // 蜜蜂敌对 (16格内有玩家)
        if (event.getEntity() instanceof Bee bee) {
            if (hasPlayerNearby(loc, 16.0)) {
                bee.setAnger(Integer.MAX_VALUE);
            }
        }
        
        // 北极熊敌对 (16格内有玩家)
        if (event.getEntity() instanceof PolarBear polarBear) {
            if (hasPlayerNearby(loc, 16.0)) {
                // PolarBear没有setAnger方法，使用目标设置
                Player nearestPlayer = getNearestPlayer(loc, 16.0);
                if (nearestPlayer != null) {
                    polarBear.setTarget(nearestPlayer);
                }
            }
        }
        
        // 僵尸猪灵敌对 (32格内有玩家)
        if (event.getEntity() instanceof PigZombie pigZombie) {
            if (hasPlayerNearby(loc, 32.0)) {
                pigZombie.setAngry(true);
                pigZombie.setAnger(Integer.MAX_VALUE);
            }
        }
    }
    
    // ==================== 末影龙增强 ====================
    
    /**
     * 监听实体伤害事件 - 末影龙增强
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForEnderDragon(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        
        // 末影龙每tick回血 (通过定时任务实现)
        if (event.getEntity() instanceof EnderDragon dragon) {
            // 启动回血任务
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!dragon.isValid() || dragon.isDead()) {
                        this.cancel();
                        return;
                    }
                    // 每tick 1%几率回复1HP
                    if (random.nextDouble() < 0.01) {
                        double maxHealth = dragon.getMaxHealth();
                        double currentHealth = dragon.getHealth();
                        if (currentHealth < maxHealth) {
                            dragon.setHealth(Math.min(maxHealth, currentHealth + 1.0));
                        }
                    }
                }
            }.runTaskTimer(plugin, 1L, 1L);
        }
    }
    
    /**
     * 监听实体生成事件 - 龙息增强
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawnForDragonBreath(EntitySpawnEvent event) {
        if (event.isCancelled()) return;
        
        // 龙息云团增强 (半径=4, 持续400刻)
        if (event.getEntity() instanceof AreaEffectCloud cloud) {
            if (cloud.getSource() instanceof EnderDragon) {
                cloud.setRadius(4.0f);
                cloud.setDuration(400);
            }
        }
    }
    
    // ==================== 烈焰人增强 ====================
    
    /**
     * 监听实体生成事件 - 烈焰人增强
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForBlaze(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Blaze blaze) {
            // 无重力
            blaze.setGravity(false);
            
            // 速度增加 (使用正确的Attribute名称)
            try {
                org.bukkit.attribute.Attribute speedAttr = org.bukkit.attribute.Attribute.valueOf("GENERIC_MOVEMENT_SPEED");
                blaze.getAttribute(speedAttr).setBaseValue(0.4);
            } catch (IllegalArgumentException e) {
                // 如果属性名不同，尝试其他方式
            }
            
            // 火焰轨迹 (通过定时任务实现)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!blaze.isValid() || blaze.isDead()) {
                        this.cancel();
                        return;
                    }
                    // 在烈焰人位置放置火焰
                    Location loc = blaze.getLocation();
                    if (loc.getBlock().getType() == Material.AIR) {
                        loc.getBlock().setType(Material.FIRE);
                    }
                }
            }.runTaskTimer(plugin, 10L, 10L);
        }
    }
    
    // ==================== 渐进式摔伤debuff ====================
    
    /**
     * 监听实体伤害事件 - 渐进式摔伤debuff
     * 按伤害量分4级：1心→4+心，逐步添加失明/反胃/夜视等
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForProgressiveFall(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player && 
            event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            
            double damage = event.getFinalDamage();
            
            // 4级渐进式debuff
            if (damage >= 8.0) { // 4+心
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
            } else if (damage >= 6.0) { // 3心
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 150, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
            } else if (damage >= 4.0) { // 2心
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 150, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 150, 0));
            } else if (damage >= 2.0) { // 1心
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
            }
        }
    }
    
    // ==================== 渐进式溺水debuff ====================
    
    /**
     * 监听玩家移动事件 - 渐进式溺水debuff
     * 按精确空气值：≤225拉拽, ≤200挖掘疲劳, ≤100+虚弱, ≤60+反胃, ≤30+失明
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForProgressiveDrowning(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        if (!player.isUnderWater()) return;
        
        int remainingAir = player.getRemainingAir();
        int maxAir = player.getMaximumAir();
        
        // ≤225: 向下拉拽
        if (remainingAir <= 225) {
            player.setVelocity(player.getVelocity().add(new Vector(0, -0.05, 0)));
        }
        
        // ≤200: 挖掘疲劳
        if (remainingAir <= 200) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0));
        }
        
        // ≤100: 虚弱
        if (remainingAir <= 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        }
        
        // ≤60: 反胃
        if (remainingAir <= 60) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0));
        }
        
        // ≤30: 失明
        if (remainingAir <= 30) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
        }
    }
    
    // ==================== 末影之眼碎裂 ====================
    
    /**
     * 监听玩家使用物品事件 - 末影之眼碎裂
     * 投出约4秒(81刻)后碎裂，附带粒子和音效
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUseItemForEnderEye(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_EYE) {
            // 延迟碎裂
            new BukkitRunnable() {
                @Override
                public void run() {
                    // 查找附近的末影之眼
                    for (Entity entity : event.getPlayer().getNearbyEntities(50, 50, 50)) {
                        if (entity instanceof EnderSignal enderSignal) {
                            // 延迟81刻(约4秒)后碎裂
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (enderSignal.isValid()) {
                                        Location loc = enderSignal.getLocation();
                                        // 生成粒子
                                        loc.getWorld().spawnParticle(org.bukkit.Particle.CRIT, loc, 10);
                                        // 播放音效
                                        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
                                        // 移除
                                        enderSignal.remove();
                                    }
                                }
                            }.runTaskLater(plugin, 81L);
                        }
                    }
                }
            }.runTaskLater(plugin, 5L);
        }
    }
    
    // ==================== TNT物品爆炸 ====================
    
    /**
     * 监听实体掉落事件 - TNT物品爆炸
     * 掉落的TNT物品有几率召唤TNT实体(引信80)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDropItemForTNT(EntityDropItemEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getItemDrop().getItemStack().getType() == Material.TNT) {
            if (random.nextDouble() < 0.1) { // 10%几率
                Location loc = event.getItemDrop().getLocation();
                TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
                tnt.setFuseTicks(80);
                event.getItemDrop().remove();
            }
        }
    }
    
    // ==================== 低亮度随机音效 ====================
    
    /**
     * 监听玩家移动事件 - 低亮度随机音效
     * 亮度=0时1.66%几率播放恐怖音效
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForAmbientSounds(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        int lightLevel = player.getLocation().getBlock().getLightLevel();
        
        if (lightLevel == 0 && random.nextDouble() < 0.0166) { // 1.66%几率
            Sound[] horrorSounds = {
                Sound.AMBIENT_CAVE,
                Sound.ENTITY_GHAST_AMBIENT,
                Sound.ENTITY_GHAST_SCREAM,
                Sound.ENTITY_ENDERMAN_AMBIENT,
                Sound.ENTITY_ENDERMAN_SCREAM,
                Sound.ENTITY_ZOMBIE_AMBIENT,
                Sound.ENTITY_SKELETON_AMBIENT,
                Sound.ENTITY_CREEPER_PRIMED,
                Sound.ENTITY_WITCH_AMBIENT,
                Sound.ENTITY_WITHER_AMBIENT,
                Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD,
                Sound.AMBIENT_NETHER_WASTES_MOOD,
                Sound.AMBIENT_BASALT_DELTAS_MOOD,
                Sound.AMBIENT_CRIMSON_FOREST_MOOD,
                Sound.AMBIENT_WARPED_FOREST_MOOD,
                Sound.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS
            };
            
            Sound randomSound = horrorSounds[random.nextInt(horrorSounds.length)];
            player.playSound(player.getLocation(), randomSound, 0.5f, 0.8f + random.nextFloat() * 0.4f);
        }
    }
    
    // ==================== 铁傀儡猛击攻击 ====================
    
    /**
     * 监听实体攻击事件 - 铁傀儡猛击攻击
     * 1.5%几率浮空+TNT爆炸
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForIronGolemSmash(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getDamager() instanceof IronGolem golem && event.getEntity() instanceof Player player) {
            if (random.nextDouble() < 0.015) { // 1.5%几率
                // 浮空
                player.setVelocity(new Vector(0, 2.0, 0));
                
                // 延迟爆炸
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isValid() && !player.isDead()) {
                            player.getWorld().createExplosion(player.getLocation(), 4.0f, true);
                        }
                    }
                }.runTaskLater(plugin, 20L);
            }
        }
    }
    
    // ==================== 卫道士图腾掉率降低 ====================
    
    /**
     * 监听实体死亡事件 - 卫道士图腾掉率降低
     * 掉落率降至66%（原版100%）
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeathForEvokerTotem(EntityDeathEvent event) {
        if (event.getEntity() instanceof Evoker evoker) {
            // 移除图腾 (66%几率保留)
            if (random.nextDouble() > 0.66) {
                event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
            }
        }
    }
    
    // ==================== 岩浆热浪 ====================
    
    /**
     * 监听玩家移动事件 - 岩浆热浪
     * 岩浆旁1格放置火焰方块造成伤害
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForLavaHeat(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        
        // 检查附近1格是否有岩浆
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Material blockType = loc.getBlock().getRelative(x, y, z).getType();
                    if (blockType == Material.LAVA) {
                        // 造成伤害
                        if (random.nextDouble() < 0.1) { // 10%几率
                            player.damage(1.0);
                            player.setFireTicks(40);
                        }
                        return;
                    }
                }
            }
        }
    }
    
    // ==================== 盾牌格挡箭偏转 ====================
    
    /**
     * 监听实体伤害事件 - 盾牌格挡箭偏转
     * 盾牌格挡后箭向下偏转并标记为玩家箭
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForShieldDeflection(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Arrow arrow) {
            if (player.isBlocking()) {
                // 向下偏转
                Vector velocity = arrow.getVelocity();
                arrow.setVelocity(new Vector(velocity.getX() * 0.5, -0.5, velocity.getZ() * 0.5));
                // 标记为玩家箭
                arrow.setShooter(player);
            }
        }
    }
    
    // ==================== 蜜蜂蛰刺增强 ====================
    
    /**
     * 监听实体伤害事件 - 蜜蜂蛰刺增强
     * 中毒10秒 + 虚弱30秒
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForBeeSting(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getDamager() instanceof Bee && event.getEntity() instanceof Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1)); // 中毒10秒
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 0)); // 虚弱30秒
        }
    }
}
