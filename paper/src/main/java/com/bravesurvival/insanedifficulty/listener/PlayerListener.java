package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.EntityUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public PlayerListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // 摔落伤害
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            plugin.getPlayerManager().onFallDamage(player, event.getDamage());
        }

        // 仙人掌伤害
        if (event.getCause() == EntityDamageEvent.DamageCause.CONTACT) {
            Block below = player.getLocation().getBlock().getRelative(0, -1, 0);
            if (below.getType() == Material.CACTUS || player.getLocation().getBlock().getType() == Material.CACTUS) {
                plugin.getCombatManager().onCactusDamage(player);
            }
        }

        // 岩浆伤害
        if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            event.setDamage(event.getDamage() + 2.0);
            player.setFireTicks(100);
        }

        // 受伤时掉落物品
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID && 
            event.getCause() != EntityDamageEvent.DamageCause.STARVATION) {
            plugin.getPlayerManager().onPlayerDamage(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getPlayerManager().onPlayerDeath(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player player) {
            plugin.getCombatManager().onTotemUse(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Material food = event.getItem().getType();

        // 腐肉效果
        if (food == Material.ROTTEN_FLESH && config.isRottenFleshDebuff()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.removePotionEffect(PotionEffectType.HUNGER);
                    if (RNG.chance(0.67)) { // 67%概率
                        EntityUtil.addEffect(player, PotionEffectType.HUNGER, 120, 40);
                    }
                }
            }, 1L);
        }

        // 生食效果
        if (EntityUtil.isRawFood(food) && config.isRawFoodDebuff()) {
            if (RNG.chance(0.8)) { // 80%概率
                EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 400, 0);
            }
            if (RNG.chance(0.67)) { // 67%概率
                EntityUtil.addEffect(player, PotionEffectType.HUNGER, 100, 40);
            }
        }

        // 食物中毒
        if (EntityUtil.isFood(food) && config.isFoodPoisoning()) {
            if (RNG.chance(config.getFoodPoisoningChance())) {
                EntityUtil.addEffect(player, PotionEffectType.POISON, 80, 1);
                EntityUtil.addEffect(player, PotionEffectType.HUNGER, 200, 20);
                EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 600, 0);
            }
        }

        // 药水副作用
        if (EntityUtil.isPotion(food) && config.isPotionSideEffects()) {
            if (RNG.chance(config.getPotionSideEffectsChance())) {
                PotionEffect effect = plugin.getCombatManager().getRandomPotionSideEffect();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) player.addPotionEffect(effect);
                }, 20L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockY() == event.getTo().getBlockY() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

        Player player = event.getPlayer();

        // 感染石头生成唤魔者尖牙
        if (config.isInfestedStoneSpawnsFangs()) {
            Block below = event.getTo().getBlock().getRelative(0, -1, 0);
            if (EntityUtil.isInfestedStone(below.getType())) {
                if (RNG.chance(config.getInfestedStoneFangsChance())) {
                    player.getWorld().spawnEntity(player.getLocation(), EntityType.EVOKER_FANGS);
                    EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 20, 10);
                }
            }
        }

        // 结构检测
        plugin.getStructureManager().onPlayerMove(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;

        // 末影珍珠落地（使用目的地而非出发地）
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (to != null) {
                plugin.getCombatManager().onEnderPearlLand(event.getPlayer(), to);
            }
        }

        // 传送门破坏（破坏出发地的传送门，而非目的地）
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (config.isPortalBreakChance() && RNG.chance(config.getPortalBreakChanceValue())) {
                Location from = event.getFrom();
                if (from != null) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        Block block = from.getBlock();
                        if (block.getType() == Material.NETHER_PORTAL) {
                            block.setType(Material.AIR);
                        }
                    }, 20L);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.isCancelled()) return;
        // 睡眠处理在PlayerManager.tick()中
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        // 睡眠处理在PlayerManager.tick()中
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getCombatManager().onPlayerDisconnect(event.getPlayer().getUniqueId());
    }
}
