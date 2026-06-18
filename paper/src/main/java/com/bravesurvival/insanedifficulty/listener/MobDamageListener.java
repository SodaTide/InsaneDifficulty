package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class MobDamageListener implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public MobDamageListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPiglinBarter(PiglinBarterEvent event) {
        if (event.isCancelled()) return;
        if (!config.isMobEnabled("piglin_bartering")) return;

        // 替换以物易物结果为削弱版战利品
        List<ItemStack> outcome = event.getOutcome();
        outcome.clear();
        outcome.add(getRandomBarterItem());
    }

    private ItemStack getRandomBarterItem() {
        // 权重表完全匹配数据包 piglin_bartering.json
        // 总权重 = 441
        int roll = RNG.range(0, 440);
        int cumulative = 0;

        cumulative += 5;   if (roll < cumulative) return new ItemStack(Material.BOOK);
        cumulative += 8;   if (roll < cumulative) return new ItemStack(Material.IRON_BOOTS);
        cumulative += 8;   if (roll < cumulative) return makePotion(Material.POTION, org.bukkit.potion.PotionType.FIRE_RESISTANCE);
        cumulative += 8;   if (roll < cumulative) return makePotion(Material.SPLASH_POTION, org.bukkit.potion.PotionType.FIRE_RESISTANCE);
        cumulative += 10;  if (roll < cumulative) return makePotion(Material.POTION, org.bukkit.potion.PotionType.WATER);
        cumulative += 10;  if (roll < cumulative) return new ItemStack(Material.IRON_NUGGET, RNG.range(10, 36));
        cumulative += 10;  if (roll < cumulative) return new ItemStack(Material.ENDER_PEARL, RNG.range(2, 4));
        cumulative += 20;  if (roll < cumulative) return new ItemStack(Material.STRING, RNG.range(3, 9));
        cumulative += 20;  if (roll < cumulative) return new ItemStack(Material.QUARTZ, RNG.range(5, 12));
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.OBSIDIAN);
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.CRYING_OBSIDIAN, RNG.range(1, 3));
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.FIRE_CHARGE);
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.LEATHER, RNG.range(2, 4));
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.SOUL_SAND, RNG.range(2, 8));
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.NETHER_BRICK, RNG.range(2, 8));
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.SPECTRAL_ARROW, RNG.range(6, 12));
        cumulative += 40;  if (roll < cumulative) return new ItemStack(Material.GRAVEL, RNG.range(8, 16));
        return new ItemStack(Material.BLACKSTONE, RNG.range(8, 16));
    }

    private ItemStack makePotion(Material material, org.bukkit.potion.PotionType type) {
        ItemStack item = new ItemStack(material);
        org.bukkit.inventory.meta.PotionMeta meta = (org.bukkit.inventory.meta.PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setBasePotionType(type);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        // 蜘蛛咬伤
        if (event.getDamager() instanceof Spider) {
            plugin.getCombatManager().onSpiderBite(player);
        }

        // 蜜蜂蜇伤
        if (event.getDamager() instanceof Bee) {
            plugin.getCombatManager().onBeeSting(player);
        }

        // 流浪者攻击
        if (event.getDamager() instanceof Stray) {
            plugin.getCombatManager().onStrayHit(player);
        }

        // 尸壳攻击
        if (event.getDamager() instanceof Husk) {
            plugin.getCombatManager().onHuskHit(player);
        }

        // 凋零骷髅攻击
        if (event.getDamager() instanceof WitherSkeleton) {
            plugin.getCombatManager().onWitherSkeletonHit(player);
        }

        // 末影人攻击后传送
        if (event.getDamager() instanceof Enderman enderman) {
            plugin.getCombatManager().onEndermanHitPlayer(enderman);
        }

        // 铁傀儡猛击
        if (event.getDamager() instanceof IronGolem golem) {
            if (Math.random() < config.getIronGolemSlamChance()) {
                plugin.getCombatManager().onIronGolemSlam(golem, player);
            }
        }

        // 女巫被攻击时喷出药水
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Witch witch) {
            if (config.isWitchSplashPotions() && Math.random() < 0.5) {
                org.bukkit.Location loc = witch.getLocation();
                for (int i = 0; i < 4; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double x = loc.getX() + Math.cos(angle) * 0.75;
                    double z = loc.getZ() + Math.sin(angle) * 0.75;
                    org.bukkit.Location spawnLoc = new org.bukkit.Location(loc.getWorld(), x, loc.getY() + 1.5, z);
                    
                    ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
                    org.bukkit.inventory.meta.PotionMeta potionMeta = (org.bukkit.inventory.meta.PotionMeta) potionItem.getItemMeta();
                    if (potionMeta != null) {
                        // 数据包效果: health_boost + hero_of_the_village
                        potionMeta.addCustomEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.HEALTH_BOOST, 100, 1), true);
                        potionMeta.addCustomEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.HERO_OF_THE_VILLAGE, 640, 0), true);
                        potionItem.setItemMeta(potionMeta);
                    }
                    
                    org.bukkit.entity.ThrownPotion potion = witch.launchProjectile(org.bukkit.entity.ThrownPotion.class);
                    potion.setItem(potionItem);
                    potion.setVelocity(new org.bukkit.util.Vector(
                        Math.cos(angle) * 0.5, 0.3, Math.sin(angle) * 0.5));
                }
            }
        }
    }
}
