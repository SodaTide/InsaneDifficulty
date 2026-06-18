package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.mocoder.bravesurvival.core.config.ConfigManager;

import java.util.Random;

/**
 * 生物变形与敌对化管理器
 * 处理被动生物变形、中立生物敌对化、末影龙增强、烈焰人增强等
 */
public class MobTransformManager implements Listener {

    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();

    private int enderDragonTaskId = -1;

    public MobTransformManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startTransformationTask();
        startArrowTrackingTask();
        startWitherSkullTask();
        startWitherSkeletonBlockBreakTask();
        startEndCrystalProjectileReflectionTask();
        startBlazeFireballTrailTask();
        startPlayerOnFireTrailTask();
        startAngryMobTask();
    }

    // ==================== 凋灵骷髅破坏方块 ====================
    
    private void startWitherSkeletonBlockBreakTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof WitherSkeleton skeleton) {
                            if (!skeleton.isValid() || skeleton.isDead()) continue;
                            
                            // 查找5格内的玩家
                            Player nearest = getNearestPlayer(skeleton.getLocation(), 5.0);
                            if (nearest == null) continue;
                            
                            // 计算头部上方2.6格的位置
                            Location headPos = skeleton.getLocation().add(0, 2.6, 0);
                            // 往前进0.75格（根据朝向）
                            org.bukkit.util.Vector direction = skeleton.getLocation().getDirection().normalize();
                            headPos.add(direction.multiply(0.75));
                            
                            // 检查是否是保护方块
                            Material blockType = headPos.getBlock().getType();
                            if (isProtectedBlock(blockType)) continue;
                            
                            // 破坏方块并掉落物品
                            headPos.getBlock().breakNaturally();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 5L); // 每5tick检查一次
    }
    
    private boolean isProtectedBlock(Material type) {
        // 保护方块列表（与数据包一致）
        return type == Material.BEDROCK || 
               type == Material.BARRIER ||
               type == Material.COMMAND_BLOCK ||
               type == Material.REPEATING_COMMAND_BLOCK ||
               type == Material.CHAIN_COMMAND_BLOCK ||
               type == Material.STRUCTURE_BLOCK ||
               type == Material.JIGSAW ||
               type == Material.END_PORTAL ||
               type == Material.END_GATEWAY ||
               type == Material.SPAWNER ||
               type == Material.DRAGON_EGG ||
               type == Material.NETHERITE_BLOCK ||
               type == Material.ANCIENT_DEBRIS ||
               type == Material.OBSIDIAN ||
               type == Material.CRYING_OBSIDIAN ||
               type == Material.ENDER_CHEST ||
               type == Material.BEACON ||
               type == Material.ENCHANTING_TABLE ||
               type == Material.END_PORTAL_FRAME;
    }

    // ==================== 变形定时任务 ====================

    private void startTransformationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location playerLoc = player.getLocation();

                    for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                        if (entity instanceof Cow cow) {
                            transformEntity(cow, Rabbit.class, playerLoc);
                        } else if (entity instanceof Sheep sheep) {
                            transformEntity(sheep, Rabbit.class, playerLoc);
                        } else if (entity instanceof Pig pig) {
                            transformEntity(pig, Hoglin.class, playerLoc);
                        } else if (entity instanceof Chicken chicken) {
                            // 检查鸡是否已经是jockey（避免重复生成骷髅）
                            if (!chicken.isInsideVehicle() && chicken.getPassengers().isEmpty()) {
                                transformToJockey(chicken, playerLoc);
                            }
                        } else if (entity instanceof Dolphin dolphin) {
                            if (player.getLocation().distance(dolphin.getLocation()) <= 10) {
                                transformEntity(dolphin, Cod.class, playerLoc);
                            }
                        } else if (isFish(entity)) {
                            if (player.getLocation().distance(entity.getLocation()) <= 16) {
                                if (random.nextDouble() < 0.33) {
                                    transformEntity(entity, Guardian.class, playerLoc);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 10L);
    }

    // ==================== 怪物箭矢追踪 ====================

    private void startArrowTrackingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        // 检查是否是箭矢弹射物
                        if (entity instanceof AbstractArrow arrow && !(arrow.getShooter() instanceof Player)) {
                            if (arrow.isInBlock()) continue;

                            // 查找32格内最近的玩家
                            Player nearest = null;
                            double nearestDist = 32.0;
                            for (Player player : world.getPlayers()) {
                                if (player.getGameMode() == org.bukkit.GameMode.SPECTATOR ||
                                    player.getGameMode() == org.bukkit.GameMode.CREATIVE) continue;
                                double dist = arrow.getLocation().distance(player.getLocation());
                                if (dist < nearestDist && dist > 0.5) {
                                    nearestDist = dist;
                                    nearest = player;
                                }
                            }

                            if (nearest != null) {
                                // 追踪玩家
                                Vector direction = nearest.getEyeLocation().toVector()
                                    .subtract(arrow.getLocation().toVector()).normalize();
                                Vector velocity = arrow.getVelocity();
                                double speed = velocity.length();
                                if (speed < 0.5) speed = 0.5;
                                arrow.setVelocity(direction.multiply(speed));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 2L); // 每2tick执行一次
    }

    // ==================== 女巫凋灵骷髅头弹射物 ====================

    private void startWitherSkullTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Witch witch && !witch.isDead() && witch.isValid()) {
                            // 1.5% 概率发射凋灵骷髅头
                            if (random.nextDouble() < 0.015) {
                                Player nearest = getNearestPlayer(witch.getLocation(), 12.0);
                                if (nearest != null) {
                                    shootWitherSkull(witch, nearest);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 5L); // 每5tick检查一次
    }

    private void shootWitherSkull(Witch witch, Player target) {
        Location witchHead = witch.getLocation().add(0, 1.5, 0);
        Vector direction = target.getEyeLocation().toVector().subtract(witchHead.toVector()).normalize();

        WitherSkull skull = witch.getWorld().spawn(
            witchHead.add(direction.multiply(0.5)),
            WitherSkull.class
        );
        skull.setVelocity(direction.multiply(0.3));
        skull.setShooter(witch);
    }

    // ==================== 被动生物敌对化 + 追踪范围提升 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForHostile(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        Location loc = event.getLocation();
        LivingEntity entity = event.getEntity();

        // 数据包: increased_follow_range - 所有怪物追踪范围设为32
        if (hasIncreasedFollowRange(entity)) {
            try {
                Attribute attr = org.bukkit.Registry.ATTRIBUTE.get(
                    org.bukkit.NamespacedKey.minecraft("generic.follow_range")
                );
                if (attr != null) {
                    entity.getAttribute(attr).setBaseValue(32.0);
                }
            } catch (Exception e) {}
        }

        if (event.getEntity() instanceof Wolf wolf) {
            if (hasPlayerNearby(loc, 16.0)) {
                wolf.setAngry(true);
            }
        }

        if (event.getEntity() instanceof Bee bee) {
            if (hasPlayerNearby(loc, 16.0)) {
                bee.setAnger(Integer.MAX_VALUE);
            }
        }

        if (event.getEntity() instanceof PolarBear polarBear) {
            if (hasPlayerNearby(loc, 16.0)) {
                Player nearest = getNearestPlayer(loc, 16.0);
                if (nearest != null) {
                    final Player target = nearest;
                    // 延迟设置目标，避免AI未初始化导致NPE
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        try {
                            if (polarBear.isValid() && !polarBear.isDead()) {
                                polarBear.setTarget(target);
                            }
                        } catch (Exception e) {}
                    }, 5L);
                }
            }
        }

        if (event.getEntity() instanceof PigZombie pigZombie) {
            if (hasPlayerNearby(loc, 32.0)) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    try {
                        if (pigZombie.isValid() && !pigZombie.isDead()) {
                            pigZombie.setAngry(true);
                            pigZombie.setAnger(Integer.MAX_VALUE);
                        }
                    } catch (Exception e) {}
                }, 5L);
            }
        }
    }

    // ==================== 幻翼火焰抗性 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForPhantom(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Phantom phantom) {
            // 免疫火焰
            phantom.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
            phantom.setShouldBurnInDay(false);
            // 设置攻击力为3
            try {
                phantom.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(3.0);
            } catch (Exception e) {}
        }
    }

    // ==================== 恶魂爆炸威力6 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForGhast(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Ghast ghast) {
            // 爆炸威力设为6
            ghast.setExplosionPower(6);
            // 隐身
            ghast.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

            // 数据包逻辑: new_ghast.mcfunction - 额外召唤2只恶魂（带隐身+explosion_power:6）
            // 使用spawn reason检查避免递归（额外召唤的恶魂不再触发额外召唤）
            if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
                Location loc = ghast.getLocation();
                for (int i = 0; i < 2; i++) {
                    Ghast extra = loc.getWorld().spawn(loc, Ghast.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
                    extra.setExplosionPower(6);
                    extra.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                }
            }
        }
    }

    // ==================== 马匹随机踢人 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveForHorseKick(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Player player = event.getPlayer();
        if (player.isInsideVehicle() && player.getVehicle() instanceof AbstractHorse) {
            // 16.6% 概率被踢下马
            if (random.nextDouble() < 0.166) {
                player.leaveVehicle();
                player.playSound(player.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0f, 1.0f);
            }
        }
    }

    // ==================== 末影龙增强 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawnForEnderDragon(EntitySpawnEvent event) {
        if (event.getEntity() instanceof EnderDragon dragon) {
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
            // 数据包: 检测 dragon_breath 粒子 + radius_per_tick:0.006666667
            // Paper API 没有 getSource()，改用粒子类型检测
            try {
                if (cloud.getParticle() == org.bukkit.Particle.DRAGON_BREATH) {
                    cloud.setRadius(4.0f);
                    cloud.setDuration(400);
                }
            } catch (Exception e) {
                // API不支持getParticle()，使用回退方案
            }
        }
    }

    // ==================== 烈焰人增强 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForBlaze(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Blaze blaze) {
            blaze.setGravity(false);

            try {
                // 数据包: generic.movement_speed base set 1 (line 1213)
                blaze.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(1.0);
            } catch (Exception e) {}

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

            if (damage >= 8.0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
            } else if (damage >= 6.0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 150, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
            } else if (damage >= 4.0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 150, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 150, 0));
            } else if (damage >= 2.0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0));
            }
        }
    }

    // ==================== 渐进式溺水debuff + 水流拖拽 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForProgressiveDrowning(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!player.isUnderWater()) return;

        int remainingAir = player.getRemainingAir();

        // 水流拖拽 - 气泡柱下拉
        if (player.getLocation().getBlock().getType() == Material.BUBBLE_COLUMN) {
            org.bukkit.block.BlockState state = player.getLocation().getBlock().getState();
            if (state instanceof org.bukkit.block.data.Waterlogged) {
                // 检查是否是向下拖拽的气泡柱
                player.setVelocity(player.getVelocity().add(new Vector(0, -0.5, 0)));
            }
        }

        // 溺水渐进惩罚
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 1));
        }
        if (remainingAir <= 30) {
            // 数据包: mining_fatigue 5 3, weakness 5 2, nausea 5 1, blindness 5 0
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 1));
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
            // 数据包: poison 10 0 (10秒 amplifier 0) + weakness 30 0
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 0));
        }
    }

    // ==================== 辅助方法 ====================

    private void transformEntity(Entity from, Class<? extends Entity> toClass, Location playerLoc) {
        Location loc = from.getLocation();
        World world = loc.getWorld();
        from.remove();
        world.spawn(loc, toClass);
        world.playSound(loc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.5f, 1.2f);
    }

    private void transformToJockey(Chicken chicken, Location playerLoc) {
        Location loc = chicken.getLocation();
        World world = loc.getWorld();
        chicken.remove();
        Skeleton skeleton = world.spawn(loc, Skeleton.class);
        Chicken newChicken = world.spawn(loc, Chicken.class);
        newChicken.addPassenger(skeleton);
        world.playSound(loc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 0.5f, 1.2f);
    }

    private boolean isFish(Entity entity) {
        return entity instanceof Cod || entity instanceof Salmon || entity instanceof TropicalFish;
    }

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

    // ==================== 末影水晶反射弹射物（tick级别）====================
    // 数据包: end_crystal_proj_prot.mcfunction
    // 末影水晶5格内反射arrows, snowball, egg, trident, ender_pearl

    private void startEndCrystalProjectileReflectionTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!ConfigManager.getCombatConfig().has("ender_crystals_reflect_projectiles") ||
                    !ConfigManager.getCombatConfig().get("ender_crystals_reflect_projectiles").getAsBoolean()) {
                    return;
                }
                double range = ConfigManager.getCombatConfig().has("ender_crystals_reflect_range") ?
                    ConfigManager.getCombatConfig().get("ender_crystals_reflect_range").getAsDouble() : 5.0;
                for (World world : Bukkit.getWorlds()) {
                    for (Entity crystal : world.getEntitiesByClass(EnderCrystal.class)) {
                        for (Entity nearby : crystal.getNearbyEntities(range, range, range)) {
                            if (nearby instanceof org.bukkit.entity.Projectile projectile &&
                                !(nearby instanceof org.bukkit.entity.Fireball) && // 排除火球
                                !projectile.getScoreboardTags().contains("end_crystal_proj_prot")) {
                                // 反转速度方向 (multiply by -1)
                                Vector velocity = projectile.getVelocity();
                                projectile.setVelocity(new Vector(-velocity.getX(), -velocity.getY(), -velocity.getZ()));
                                // 标记防止重复反射
                                projectile.addScoreboardTag("end_crystal_proj_prot");
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 2L); // 每2tick检查一次
    }

    // ==================== 烈焰人小火球地面生火 ====================
    // 数据包: blazes/main4.mcfunction - 小火球落地留下火迹，递归向下

    private void startBlazeFireballTrailTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity smallFireball : world.getEntitiesByClass(org.bukkit.entity.SmallFireball.class)) {
                        Location loc = smallFireball.getLocation();
                        Block block = loc.getBlock();
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.FIRE);
                        }
                        // 递归向下检查并生火 (数据包: blazes/fire.mcfunction)
                        // fire_air tag: 包含air, grass, dead_bush等，简单起见只检查air
                        Block below = block.getRelative(0, -1, 0);
                        if (below.getType() == Material.AIR) {
                            below.setType(Material.FIRE);
                        }
                    }
                    // 烈焰人脚下生火（4tick循环）
                    for (Entity blaze : world.getEntitiesByClass(Blaze.class)) {
                        Block block = blaze.getLocation().getBlock();
                        if (block.getType() == Material.AIR) {
                            block.setType(Material.FIRE);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 4L); // 数据包: 每4tick
    }

    // ==================== 玩家着火时脚下生火 ====================
    // 数据包: blazes/player_fire.mcfunction
    // 玩家着火时脚下生成火并给weakness

    private void startPlayerOnFireTrailTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Player player : world.getPlayers()) {
                        if (player.getFireTicks() > 0) {
                            Block block = player.getLocation().getBlock();
                            if (block.getType() == Material.AIR) {
                                block.setType(Material.FIRE);
                            }
                            // 玩家着火时weakness 10 0 (10秒, 0级)
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0, false, false));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L); // 每10tick（0.5秒）
    }

    // ==================== 中立生物敌对化 ====================
    // 数据包: angry.mcfunction
    // 狼、蜜蜂、铁傀儡、北极熊、末影人16格内持续anger
    // 僵尸猪灵32格内持续anger
    // 猪灵对未穿金甲的玩家angry

    private void startAngryMobTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    // 狼
                    for (Entity e : world.getEntitiesByClass(Wolf.class)) {
                        Wolf wolf = (Wolf) e;
                        if (!wolf.isAngry()) {
                            Player nearest = getNearestPlayer(wolf.getLocation(), 16.0);
                            if (nearest != null) wolf.setAngry(true);
                        }
                    }
                    // 蜜蜂
                    for (Entity e : world.getEntitiesByClass(Bee.class)) {
                        Bee bee = (Bee) e;
                        if (bee.getAnger() <= 0) {
                            Player nearest = getNearestPlayer(bee.getLocation(), 16.0);
                            if (nearest != null) bee.setAnger(200);
                        }
                    }
                    // 北极熊
                    for (Entity e : world.getEntitiesByClass(PolarBear.class)) {
                        PolarBear bear = (PolarBear) e;
                        if (bear.getTarget() == null) {
                            Player nearest = getNearestPlayer(bear.getLocation(), 16.0);
                            if (nearest != null) bear.setTarget(nearest);
                        }
                    }
                    // 末影人
                    for (Entity e : world.getEntitiesByClass(Enderman.class)) {
                        Enderman enderman = (Enderman) e;
                        if (enderman.getTarget() == null) {
                            Player nearest = getNearestPlayer(enderman.getLocation(), 16.0);
                            if (nearest != null) enderman.setTarget(nearest);
                        }
                    }
                    // 僵尸猪灵
                    for (Entity e : world.getEntitiesByClass(PigZombie.class)) {
                        PigZombie pigZombie = (PigZombie) e;
                        if (!pigZombie.isAngry()) {
                            Player nearest = getNearestPlayer(pigZombie.getLocation(), 32.0);
                            if (nearest != null) {
                                pigZombie.setAngry(true);
                                pigZombie.setAnger(200);
                            }
                        }
                    }
                    // 猪灵 - 对未穿金甲的玩家aggressive
                    for (Entity e : world.getEntitiesByClass(Piglin.class)) {
                        Piglin piglin = (Piglin) e;
                        for (Player player : piglin.getLocation().getWorld().getPlayers()) {
                            if (player.getLocation().distance(piglin.getLocation()) <= 16.0) {
                                if (!isWearingFullGold(player)) {
                                    // 猪灵使用setTarget来攻击玩家
                                    piglin.setTarget(player);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 10L); // 每10tick
    }

    private boolean isWearingFullGold(Player player) {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        return inv.getHelmet() != null && inv.getHelmet().getType() == Material.GOLDEN_HELMET
            && inv.getChestplate() != null && inv.getChestplate().getType() == Material.GOLDEN_CHESTPLATE
            && inv.getLeggings() != null && inv.getLeggings().getType() == Material.GOLDEN_LEGGINGS
            && inv.getBoots() != null && inv.getBoots().getType() == Material.GOLDEN_BOOTS;
    }

    // 数据包: increased_follow_range tag中的所有生物
    private boolean hasIncreasedFollowRange(LivingEntity entity) {
        if (entity instanceof Bee || entity instanceof Blaze || entity instanceof CaveSpider ||
            entity instanceof Creeper || entity instanceof ElderGuardian ||
            entity instanceof EnderDragon || entity instanceof Enderman ||
            entity instanceof Endermite || entity instanceof Evoker ||
            entity instanceof Ghast || entity instanceof Guardian ||
            entity instanceof Hoglin || entity instanceof Illusioner ||
            entity instanceof IronGolem || entity instanceof MagmaCube ||
            entity instanceof Panda || entity instanceof Phantom ||
            entity instanceof Piglin || entity instanceof Pillager ||
            entity instanceof PolarBear || entity instanceof Ravager ||
            entity instanceof Shulker || entity instanceof Silverfish ||
            entity instanceof Skeleton || entity instanceof Slime ||
            entity instanceof Snowman || entity instanceof Spider ||
            entity instanceof Stray || entity instanceof Vex ||
            entity instanceof Vindicator || entity instanceof Witch ||
            entity instanceof Wither || entity instanceof WitherSkeleton ||
            entity instanceof Wolf || entity instanceof Zoglin) {
            return true;
        }
        return false;
    }
}
