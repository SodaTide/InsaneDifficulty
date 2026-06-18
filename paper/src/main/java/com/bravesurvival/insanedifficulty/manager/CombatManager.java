package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.EntityUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;
    private final Map<UUID, Integer> eyeOfEnderUses = new HashMap<>();

    public CombatManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public void tick() {
        tickEndCrystals();
        tickArrows();
        tickRadiation();
    }

    private void tickEndCrystals() {
        if (!config.isEnderCrystalsReflect()) return;
        for (World world : plugin.getServer().getWorlds()) {
            for (EnderCrystal crystal : world.getEntitiesByClass(EnderCrystal.class)) {
                double range = config.getEnderCrystalsReflectRange();
                for (Entity nearby : crystal.getNearbyEntities(range, range, range)) {
                    if (nearby instanceof Projectile proj && !proj.hasMetadata("reflected")) {
                        // 反转动量
                        proj.setVelocity(proj.getVelocity().multiply(-1));
                        proj.setMetadata("reflected", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
                    }
                }
            }
        }
    }

    private void tickArrows() {
        if (!config.isSkeletonAutoAimArrows()) return;
        for (World world : plugin.getServer().getWorlds()) {
            for (Arrow arrow : world.getEntitiesByClass(Arrow.class)) {
                if (arrow.isInBlock() || arrow.isOnGround()) continue;
                ProjectileSource shooter = arrow.getShooter();
                if (shooter instanceof Skeleton || shooter instanceof Illusioner) {
                    Player target = EntityUtil.getNearestPlayer((Entity) shooter, 32);
                    if (target != null) {
                        Location arrowLoc = arrow.getLocation();
                        Location targetLoc = target.getEyeLocation();
                        Vector direction = targetLoc.toVector().subtract(arrowLoc.toVector()).normalize();
                        arrow.setVelocity(direction.multiply(1.5));
                    }
                }
            }
        }
    }

    private void tickRadiation() {
        for (World world : plugin.getServer().getWorlds()) {
            if (world.getEnvironment() != World.Environment.THE_END) continue;
            for (AreaEffectCloud cloud : world.getEntitiesByClass(AreaEffectCloud.class)) {
                if (!cloud.hasMetadata("radioactive")) continue;
                for (Player player : cloud.getLocation().getNearbyPlayers(10)) {
                    EntityUtil.addEffect(player, PotionEffectType.POISON, 20, 0);
                    EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 20, 0);
                    EntityUtil.addEffect(player, PotionEffectType.NAUSEA, 80, 0);
                }
            }
        }
    }

    public void onSpiderBite(Player player) {
        if (!config.isMobEnabled("spider")) return;
        player.removePotionEffect(PotionEffectType.POISON);
        EntityUtil.addEffect(player, PotionEffectType.POISON, 100, 1);
    }

    public void onBeeSting(Player player) {
        if (!config.isMobEnabled("bee")) return;
        player.removePotionEffect(PotionEffectType.POISON);
        EntityUtil.addEffect(player, PotionEffectType.POISON, 200, 0);
        EntityUtil.addEffect(player, PotionEffectType.WEAKNESS, 600, 0);
    }

    public void onStrayHit(Player player) {
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        EntityUtil.addEffect(player, PotionEffectType.SLOWNESS, 100, 4);
    }

    public void onHuskHit(Player player) {
        player.removePotionEffect(PotionEffectType.HUNGER);
        EntityUtil.addEffect(player, PotionEffectType.HUNGER, 300, 50);
    }

    public void onWitherSkeletonHit(Player player) {
        player.removePotionEffect(PotionEffectType.WITHER);
        EntityUtil.addEffect(player, PotionEffectType.WITHER, 200, 1);
    }

    public void onEndermanHitPlayer(Enderman enderman) {
        if (!config.isEndermanTeleportOnHit()) return;
        Location loc = enderman.getLocation();
        double angle = Math.random() * Math.PI * 2;
        double dist = 8 + Math.random() * 16;
        double newX = loc.getX() + Math.cos(angle) * dist;
        double newZ = loc.getZ() + Math.sin(angle) * dist;
        int newY = loc.getWorld().getHighestBlockYAt((int) newX, (int) newZ) + 1;
        enderman.teleport(new Location(loc.getWorld(), newX, newY, newZ));
    }

    public void onIronGolemSlam(IronGolem golem, Player player) {
        golem.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 7, 8, false, false));
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (golem.isDead()) return;
            Location loc = golem.getLocation();
            for (Entity nearby : golem.getNearbyEntities(5, 5, 5)) {
                if (nearby instanceof LivingEntity living) {
                    EntityUtil.addEffect(living, PotionEffectType.RESISTANCE, 40, 4);
                }
            }
            loc.getWorld().createExplosion(loc, 4.0F, false);
        }, 22L);
    }

    public void onTotemUse(Player player) {
        if (!config.isTotemNerfed()) return;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            player.removePotionEffect(PotionEffectType.REGENERATION);
            EntityUtil.addEffect(player, PotionEffectType.REGENERATION, config.getTotemRegenDuration1(), config.getTotemRegenAmplifier1());
            EntityUtil.addEffect(player, PotionEffectType.REGENERATION, config.getTotemRegenDuration2(), config.getTotemRegenAmplifier2());
        }, 1L);
    }

    public void onPlayerArrowLaunch(Arrow arrow) {
        if (!config.isArrowsMisfire()) return;
        if (RNG.chance(config.getArrowsMisfireChance())) {
            float angle = (RNG.range(-45, 45)) * (float) (Math.PI / 180);
            arrow.setVelocity(arrow.getVelocity().rotateAroundY(angle));
        }
    }

    public void onShieldBlock(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FOX_SNIFF, 1.0f, 1.0f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FOX_SNIFF, 1.0f, 1.0f);
    }

    public void onEnderPearlLand(Player player, Location loc) {
        if (config.isEnderPearlsSpawnEndermites()) {
            loc.getWorld().spawnEntity(loc.clone().add(0.5, 0, 0.5), EntityType.ENDERMITE);
        }
    }

    public void onExplosionInEnd(Location loc) {
        if (loc.getWorld().getEnvironment() != World.Environment.THE_END) return;
        boolean hasCloud = false;
        for (AreaEffectCloud cloud : loc.getWorld().getEntitiesByClass(AreaEffectCloud.class)) {
            if (cloud.hasMetadata("radioactive") && cloud.getLocation().distance(loc) < 5) {
                hasCloud = true;
                break;
            }
        }
        if (!hasCloud) {
            AreaEffectCloud cloud = (AreaEffectCloud) loc.getWorld().spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);
            cloud.setDuration(6000);
            cloud.setMetadata("radioactive", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        }
    }

    public void onCactusDamage(Player player) {
        if (config.isCactusPoison()) {
            EntityUtil.addEffect(player, PotionEffectType.POISON, 40, 10);
        }
    }

    public void onLavaDamage(Player player) {
        EntityUtil.addEffect(player, PotionEffectType.INSTANT_DAMAGE, 1, 0);
    }

    public PotionEffect getRandomPotionSideEffect() {
        int idx = RNG.range(0, 8);
        return switch (idx) {
            case 0 -> new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false);
            case 1 -> new PotionEffect(PotionEffectType.HUNGER, 100, 100, false, false);
            case 2 -> new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 1, false, false);
            case 3 -> new PotionEffect(PotionEffectType.LEVITATION, 80, 0, false, false);
            case 4 -> new PotionEffect(PotionEffectType.NAUSEA, 200, 0, false, false);
            case 5 -> new PotionEffect(PotionEffectType.POISON, 100, 0, false, false);
            case 6 -> new PotionEffect(PotionEffectType.SLOWNESS, 200, 1, false, false);
            case 7 -> new PotionEffect(PotionEffectType.WEAKNESS, 200, 1, false, false);
            default -> new PotionEffect(PotionEffectType.WITHER, 120, 0, false, false);
        };
    }

    public void incrementEyeOfEnderUses(Player player) {
        if (!config.isEyeOfEnderExplode()) return;
        UUID uuid = player.getUniqueId();
        int uses = eyeOfEnderUses.getOrDefault(uuid, 0) + 1;
        eyeOfEnderUses.put(uuid, uses);

        if (uses >= config.getEyeOfEnderMaxUses()) {
            // 数据包行为: 音效 + 粒子 + 直接击杀
            org.bukkit.Location loc = player.getLocation();
            player.getWorld().playSound(loc, org.bukkit.Sound.ENTITY_ENDER_EYE_DEATH, 1.0f, 1.0f);
            player.getWorld().spawnParticle(org.bukkit.Particle.ITEM, loc, 4, 0.1, 0.1, 0.1, 0,
                new org.bukkit.inventory.ItemStack(org.bukkit.Material.ENDER_EYE));
            player.getWorld().spawnParticle(org.bukkit.Particle.REVERSE_PORTAL, loc, 100, 0.2, 0.2, 0.2, 2);
            player.setHealth(0);
            eyeOfEnderUses.put(uuid, 0);
        }
    }

    public int getShieldDamageIncrease() {
        return config.getShieldDurabilityPerBlock();
    }

    public void cleanup() {
        eyeOfEnderUses.clear();
    }

    public void onPlayerDisconnect(UUID uuid) {
        eyeOfEnderUses.remove(uuid);
    }
}
