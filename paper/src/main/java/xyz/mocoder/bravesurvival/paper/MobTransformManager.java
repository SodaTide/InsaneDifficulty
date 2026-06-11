package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
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
import java.util.UUID;

/**
 * 生物变形与敌对化管理器
 * 处理被动生物变形、中立生物敌对化、末影龙增强、烈焰人增强等
 */
public class MobTransformManager implements Listener {
    
    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();
    
    // 末影龙定时器管理
    private int enderDragonTaskId = -1;
    
    public MobTransformManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    // ==================== 被动生物变形 ====================
    
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
    
    private boolean isFish(Entity entity) {
        return entity instanceof Cod || entity instanceof Salmon || 
               entity instanceof TropicalFish;
    }
    
    // ==================== 中立生物敌对化 ====================
    
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
                Player nearest = getNearestPlayer(loc, 16.0);
                if (nearest != null) {
                    polarBear.setTarget(nearest);
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawnForEnderDragon(EntitySpawnEvent event) {
        if (event.getEntity() instanceof EnderDragon dragon) {
            // 启动单一定时器
            if (enderDragonTaskId == -1) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!dragon.isValid() || dragon.isDead()) {
                            enderDragonTaskId = -1;
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
                };
                task.runTaskTimer(plugin, 1L, 1L);
                enderDragonTaskId = task.getTaskId();
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawnForDragonBreath(EntitySpawnEvent event) {
        if (event.getEntity() instanceof AreaEffectCloud cloud) {
            if (cloud.getSource() instanceof EnderDragon) {
                cloud.setRadius(4.0f);
                cloud.setDuration(400);
            }
        }
    }
    
    // ==================== 烈焰人增强 ====================
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForBlaze(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Blaze blaze) {
            // 无重力
            blaze.setGravity(false);
            
            // 速度增加
            try {
                blaze.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.4);
            } catch (Exception e) {
                // 忽略属性不存在的情况
            }
            
            // 火焰轨迹
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!blaze.isValid() || blaze.isDead()) {
                        this.cancel();
                        return;
                    }
                    Location loc = blaze.getLocation();
                    if (loc.getBlock().getType() == Material.AIR) {
                        loc.getBlock().setType(Material.FIRE);
                    }
                }
            }.runTaskTimer(plugin, 10L, 10L);
        }
    }
    
    // ==================== 渐进式摔伤debuff ====================
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForProgressiveFall(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player && 
            event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            
            double damage = event.getFinalDamage();
            
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
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForProgressiveDrowning(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        if (!player.isUnderWater()) return;
        
        int remainingAir = player.getRemainingAir();
        
        if (remainingAir <= 225) {
            player.setVelocity(player.getVelocity().add(new Vector(0, -0.05, 0)));
        }
        if (remainingAir <= 200) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0));
        }
        if (remainingAir <= 100) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0));
        }
        if (remainingAir <= 60) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0));
        }
        if (remainingAir <= 30) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
        }
    }
    
    // ==================== 末影之眼碎裂 ====================
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUseItemForEnderEye(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_EYE) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Entity entity : event.getPlayer().getNearbyEntities(50, 50, 50)) {
                        if (entity instanceof EnderSignal enderSignal) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (enderSignal.isValid()) {
                                        Location loc = enderSignal.getLocation();
                                        loc.getWorld().spawnParticle(org.bukkit.Particle.CRIT, loc, 10);
                                        loc.getWorld().playSound(loc, Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDropItemForTNT(EntityDropItemEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getItemDrop().getItemStack().getType() == Material.TNT) {
            if (random.nextDouble() < 0.1) {
                Location loc = event.getItemDrop().getLocation();
                TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
                tnt.setFuseTicks(80);
                event.getItemDrop().remove();
            }
        }
    }
    
    // ==================== 低亮度随机音效 ====================
    
    private long lastAmbientSoundTick = 0;
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForAmbientSounds(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        // 限制频率：每玩家每秒最多检查一次
        long currentTick = event.getPlayer().getWorld().getFullTime();
        if (currentTick - lastAmbientSoundTick < 20) return;
        lastAmbientSoundTick = currentTick;
        
        Player player = event.getPlayer();
        int lightLevel = player.getLocation().getBlock().getLightLevel();
        
        if (lightLevel == 0 && random.nextDouble() < 0.0166) {
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForIronGolemSmash(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getDamager() instanceof IronGolem golem && event.getEntity() instanceof Player player) {
            if (random.nextDouble() < 0.015) {
                player.setVelocity(new Vector(0, 2.0, 0));
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeathForEvokerTotem(EntityDeathEvent event) {
        if (event.getEntity() instanceof Evoker) {
            if (random.nextDouble() > 0.66) {
                event.getDrops().removeIf(item -> item.getType() == Material.TOTEM_OF_UNDYING);
            }
        }
    }
    
    // ==================== 岩浆热浪 ====================
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForLavaHeat(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (loc.getBlock().getRelative(x, y, z).getType() == Material.LAVA) {
                        if (random.nextDouble() < 0.1) {
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForShieldDeflection(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof Arrow arrow) {
            if (player.isBlocking()) {
                Vector velocity = arrow.getVelocity();
                arrow.setVelocity(new Vector(velocity.getX() * 0.5, -0.5, velocity.getZ() * 0.5));
                arrow.setShooter(player);
            }
        }
    }
    
    // ==================== 蜜蜂蛰刺增强 ====================
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForBeeSting(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getDamager() instanceof Bee && event.getEntity() instanceof Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 0));
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private boolean hasPlayerNearby(Location location, double distance) {
        for (Player player : location.getWorld().getPlayers()) {
            if (player.getLocation().distance(location) <= distance) {
                return true;
            }
        }
        return false;
    }
    
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
}
