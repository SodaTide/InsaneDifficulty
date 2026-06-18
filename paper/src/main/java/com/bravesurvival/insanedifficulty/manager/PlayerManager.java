package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.EntityUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;
    private final Map<UUID, Double> previousHealth = new HashMap<>();
    private final Map<UUID, Boolean> inBed = new HashMap<>();
    private final Map<UUID, Integer> sleepTimer = new HashMap<>();

    public PlayerManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public void tick() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player == null || !player.isOnline()) continue;
            try {
                processHunger(player);
                processDrowning(player);
                processHeavyArmor(player);
                processMiningFatigue(player);
                processLavaHeat(player);
                processBoat(player);
                processHorse(player);
                processItemDrop(player);
                processSleep(player);
            } catch (Exception e) {
                plugin.getLogger().warning("玩家处理错误: " + e.getMessage());
            }
        }
    }

    private void processHunger(Player player) {
        if (!config.isHungerEffects()) return;
        int food = player.getFoodLevel();

        if (food <= config.getHungerWeakness1Threshold()) {
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 0);
        }
        if (food <= config.getHungerWeakness2Threshold()) {
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 1);
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 100, 0);
        }
        if (food <= config.getHungerWeakness3Threshold()) {
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 2);
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 100, 1);
            EntityUtil.addEffect(player, PotionEffectType.HUNGER, 40, 80);
        }
    }

    private void processDrowning(Player player) {
        if (!config.isDrowningEffects()) return;
        int air = player.getRemainingAir();
        int maxAir = player.getMaximumAir();

        // 225/300 = 75%
        if (air <= maxAir * 0.75) {
            if (config.isDrowningSink() && isInWater(player)) {
                player.setVelocity(player.getVelocity().add(new Vector(0, -0.1, 0)));
            }
        }
        // 200/300 = 66.7%
        if (air <= maxAir * 0.667) {
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 100, 0);
            if (config.isDrowningSink() && isInWater(player)) {
                player.setVelocity(player.getVelocity().add(new Vector(0, -0.1, 0)));
            }
        }
        // 100/300 = 33.3%
        if (air <= maxAir * 0.333) {
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 100, 1);
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 0);
            if (config.isDrowningSink() && isInWater(player)) {
                player.setVelocity(player.getVelocity().add(new Vector(0, -0.1, 0)));
            }
        }
        // 60/300 = 20%
        if (air <= maxAir * 0.2) {
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 100, 2);
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 1);
            EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 100, 0);
            if (config.isDrowningSink() && isInWater(player)) {
                player.setVelocity(player.getVelocity().add(new Vector(0, -0.1, 0)));
            }
        }
        // 30/300 = 10%
        if (air <= maxAir * 0.1) {
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 100, 3);
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 2);
            EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 100, 1);
            EntityUtil.addEffect(player, PotionEffectType.BLINDNESS, 100, 0);
            if (config.isDrowningSink() && isInWater(player)) {
                player.setVelocity(player.getVelocity().add(new Vector(0, -0.1, 0)));
            }
        }
    }

    private boolean isInWater(Player player) {
        Block block = player.getLocation().getBlock();
        return block.getType() == Material.WATER || block.getType() == Material.BUBBLE_COLUMN;
    }

    private void processHeavyArmor(Player player) {
        if (!config.isHeavyArmorSlowness()) return;
        int armorCount = 0;
        if (config.isHeavyArmor(player.getInventory().getHelmet() != null ? player.getInventory().getHelmet().getType() : Material.AIR)) armorCount++;
        if (config.isHeavyArmor(player.getInventory().getChestplate() != null ? player.getInventory().getChestplate().getType() : Material.AIR)) armorCount++;
        if (config.isHeavyArmor(player.getInventory().getLeggings() != null ? player.getInventory().getLeggings().getType() : Material.AIR)) armorCount++;
        if (config.isHeavyArmor(player.getInventory().getBoots() != null ? player.getInventory().getBoots().getType() : Material.AIR)) armorCount++;

        if (armorCount >= 1 && armorCount <= 2) {
            EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 40, 0);
        } else if (armorCount >= 3) {
            EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 40, 1);
        }
    }

    private void processMiningFatigue(Player player) {
        if (!config.isMiningFatigueWithoutTools()) return;
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!config.isTool(mainHand.getType())) {
            EntityUtil.addEffect(player, PotionEffectType.MINING_FATIGUE, 40, 0);
        }
    }

    private void processLavaHeat(Player player) {
        if (!config.isLavaHeatDamage()) return;
        Block block = player.getLocation().getBlock();
        if (com.bravesurvival.insanedifficulty.util.BlockUtil.isNearLava(block, 1)) {
            player.setFireTicks(config.getLavaFireTicks());
            if (config.isLavaWeaknessOnFire() && player.getFireTicks() > 0) {
                EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 200, 0);
            }
            // 点燃脚下空气
            com.bravesurvival.insanedifficulty.util.BlockUtil.spreadFire(block);
        }
    }

    private void processBoat(Player player) {
        if (!(player.getVehicle() instanceof Boat boat)) return;
        if (RNG.chance(0.00166)) { // 约每分钟一次
            boat.setVelocity(boat.getVelocity().add(new Vector(0, -0.2, 0)));
            EntityUtil.addEffect(player, PotionEffectType.HUNGER, 20, 4);
        }
    }

    private void processHorse(Player player) {
        if (!EntityUtil.isHorse(player.getVehicle())) return;
        if (RNG.chance(0.00166)) { // 约每分钟一次
            player.getVehicle().eject();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0f, 1.0f);
        }
    }

    private void processItemDrop(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;
        if (!config.isDropItemsOnMove()) return;
        if (RNG.chance(config.getDropItemsOnMoveChance())) {
            dropRandomItem(player);
        }
    }

    private void processSleep(Player player) {
        if (player.isSleeping()) {
            inBed.put(player.getUniqueId(), true);
            sleepTimer.merge(player.getUniqueId(), 1, Integer::sum);
        } else {
            if (inBed.getOrDefault(player.getUniqueId(), false)) {
                int sleepTime = sleepTimer.getOrDefault(player.getUniqueId(), 0);
                if (sleepTime > 0 && config.isSleepSkipTime()) {
                    // 只跳过1/3夜晚
                    long currentTime = player.getWorld().getTime();
                    player.getWorld().setTime(currentTime + 3600);
                }

                // 生成幻翼
                if (config.isSleepPhantomSpawn()) {
                    for (int i = 0; i < config.getSleepPhantomCount(); i++) {
                        if (RNG.chance(config.getSleepPhantomSpawnChance())) {
                            player.getWorld().spawnEntity(player.getLocation(), EntityType.PHANTOM);
                        }
                    }
                }

                // 生成恼鬼（数据包中睡眠后会生成恼鬼）
                if (config.isSleepSpawnVex()) {
                    for (int i = 0; i < 4; i++) {
                        if (RNG.chance(0.5)) {
                            player.getWorld().spawnEntity(player.getLocation(), EntityType.VEX);
                        }
                    }
                }

                // 给予debuff
                EntityUtil.addEffect(player, PotionEffectType.HUNGER, 100, 127);
                EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 400, 0);

                inBed.put(player.getUniqueId(), false);
                sleepTimer.put(player.getUniqueId(), 0);
            }
        }
    }

    public void onFallDamage(Player player, double damage) {
        if (!config.isFallDamageDebuff()) return;
        int level = (int) Math.min(damage / 2, 4); // 每2点伤害一个等级

        // 数据包行为：先清除抗性，再根据等级给抗性+瞬间伤害（模拟致命摔落）
        player.removePotionEffect(PotionEffectType.RESISTANCE);

        if (level >= 1) {
            EntityUtil.addEffect(player, PotionEffectType.RESISTANCE, 20, 1, true);
            EntityUtil.addEffect(player, PotionEffectType.INSTANT_DAMAGE, 20, 0, true);
            EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, config.getFallDamageSlownessDuration(), 1);
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, config.getFallDamageWeaknessDuration(), 0);
            EntityUtil.addEffect(player, PotionEffectType.BLINDNESS, config.getFallDamageBlindnessDuration(), 0);
        }
        if (level >= 2) {
            EntityUtil.addEffect(player, PotionEffectType.RESISTANCE, 20, 0, true);
            EntityUtil.addEffect(player, PotionEffectType.INSTANT_DAMAGE, 20, 1, true);
            EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 200, 2);
            EntityUtil.addEffect(player, PotionEffectType.BLINDNESS, 100, 0);
            EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 100, 0);
        }
        if (level >= 3) {
            EntityUtil.addEffect(player, PotionEffectType.RESISTANCE, 20, 1, true);
            EntityUtil.addEffect(player, PotionEffectType.INSTANT_DAMAGE, 20, 2, true);
            EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 100, 4);
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 2);
            EntityUtil.addEffect(player, PotionEffectType.NIGHT_VISION, 40, 0);
            EntityUtil.addEffect(player, PotionEffectType.BLINDNESS, 200, 0);
            EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 200, 0);
        }
        if (level >= 4) {
            EntityUtil.addEffect(player, PotionEffectType.RESISTANCE, 20, 0, true);
            EntityUtil.addEffect(player, PotionEffectType.INSTANT_DAMAGE, 20, 2, true);
            EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 60, 10);
            EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 100, 4);
            EntityUtil.addEffect(player, PotionEffectType.NIGHT_VISION, 100, 0);
            EntityUtil.addEffect(player, PotionEffectType.BLINDNESS, 300, 0);
            EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 240, 0);
        }

        // 2 tick后清除抗性（数据包 behavior: schedule resets 2t 后 clear resistance）
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.removePotionEffect(PotionEffectType.RESISTANCE);
            }
        }, 2L);
    }

    public void onPlayerDamage(Player player) {
        if (!config.isDropItemsOnHit()) return;
        for (int i = 0; i < config.getDropItemsOnHitCount(); i++) {
            if (RNG.chance(config.getDropItemsOnHitChance())) {
                dropRandomItem(player);
            }
        }
    }

    public void onPlayerDeath(Player player) {
        // 生成墓碑僵尸
        if (config.isDeathSpawnZombie() && RNG.chance(config.getDeathSpawnZombieChance())) {
            org.bukkit.Location loc = player.getLocation();
            boolean hasNearby = loc.getWorld().getNearbyEntities(loc, 4, 4, 4).stream()
                .anyMatch(e -> e instanceof Zombie && "Grave Zombie".equals(e.getCustomName()));
            if (!hasNearby) {
                Zombie zombie = loc.getWorld().spawn(loc, Zombie.class);
                zombie.setCustomName("Grave Zombie");
                zombie.setCustomNameVisible(true);
                EntityUtil.setAttribute(zombie, org.bukkit.attribute.Attribute.MAX_HEALTH, 40.0);
                EntityUtil.setAttribute(zombie, org.bukkit.attribute.Attribute.ATTACK_DAMAGE, 5.0);
                EntityUtil.setAttribute(zombie, org.bukkit.attribute.Attribute.MOVEMENT_SPEED, 0.4);
                EntityUtil.setAttribute(zombie, org.bukkit.attribute.Attribute.KNOCKBACK_RESISTANCE, 0.5);
                ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
                EntityUtil.setItemUnbreakable(helmet);
                zombie.getEquipment().setHelmet(helmet);
                zombie.setPersistent(true);
            }
        }
    }

    private void dropRandomItem(Player player) {
        int slot = RNG.range(0, 40);
        ItemStack item = null;
        if (slot < 36) item = player.getInventory().getItem(slot);
        else if (slot == 36) item = player.getInventory().getHelmet();
        else if (slot == 37) item = player.getInventory().getChestplate();
        else if (slot == 38) item = player.getInventory().getLeggings();
        else if (slot == 39) item = player.getInventory().getBoots();
        else if (slot == 40) item = player.getInventory().getItemInOffHand();

        if (item != null && item.getType() != Material.AIR) {
            player.getWorld().dropItemNaturally(player.getLocation().add(0, 1, 0), item.clone());
            if (slot < 36) player.getInventory().setItem(slot, null);
            else if (slot == 36) player.getInventory().setHelmet(null);
            else if (slot == 37) player.getInventory().setChestplate(null);
            else if (slot == 38) player.getInventory().setLeggings(null);
            else if (slot == 39) player.getInventory().setBoots(null);
            else if (slot == 40) player.getInventory().setItemInOffHand(null);
        }
    }

    public void cleanup() {
        previousHealth.clear();
        inBed.clear();
        sleepTimer.clear();
    }
}
