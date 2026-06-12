package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * 实体行为管理器
 * 负责处理各种实体的特殊行为
 */
public class EntityBehaviorManager implements Listener {

    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();

    public EntityBehaviorManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    // ==================== 流浪商人替换为掠夺者小队 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForWanderingTrader(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof WanderingTrader trader) {
            if (plugin.getConfigManager().getMobConfig("wandering_trader") != null &&
                plugin.getConfigManager().getMobConfig("wandering_trader").has("replace_with_pillagers")) {

                trader.remove();

                Location loc = trader.getLocation();
                // 生成劫掠兽+掠夺者+卫道士
                Ravager ravager = loc.getWorld().spawn(loc, Ravager.class);

                Pillager pillager = loc.getWorld().spawn(loc, Pillager.class);
                pillager.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW));
                ravager.addPassenger(pillager);

                Vindicator vindicator = loc.getWorld().spawn(loc, Vindicator.class);
                vindicator.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
                ravager.addPassenger(vindicator);
            }
        }
    }

    // ==================== 掠夺者行为 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForPillager(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Pillager pillager) {
            if (plugin.getConfigManager().getMobConfig("pillager") != null) {
                // 快速装填5+多重射击
                if (plugin.getConfigManager().getMobConfig("pillager").has("quick_charge_5") &&
                    plugin.getConfigManager().getMobConfig("pillager").get("quick_charge_5").getAsBoolean()) {
                    ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                    org.bukkit.inventory.meta.CrossbowMeta meta = (org.bukkit.inventory.meta.CrossbowMeta) crossbow.getItemMeta();
                    if (meta != null) {
                        meta.addEnchant(org.bukkit.enchantments.Enchantment.MULTISHOT, 1, true);
                        meta.addEnchant(org.bukkit.enchantments.Enchantment.QUICK_CHARGE, 5, true);
                        crossbow.setItemMeta(meta);
                    }
                    pillager.getEquipment().setItemInMainHand(crossbow);
                }

                // 劫掠兽伴随
                if (plugin.getConfigManager().getMobConfig("pillager").has("ravager_with_pillager") &&
                    plugin.getConfigManager().getMobConfig("pillager").get("ravager_with_pillager").getAsBoolean()) {
                    pillager.getWorld().spawn(pillager.getLocation(), Ravager.class);
                }
            }
        }
    }

    // ==================== 女巫行为 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForWitch(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Witch witch) {
            if (plugin.getConfigManager().getMobConfig("witch") != null &&
                plugin.getConfigManager().getMobConfig("witch").has("spawn_with_wither_skeletons")) {
                witch.getWorld().spawn(witch.getLocation(), WitherSkeleton.class);
            }
        }
    }

    // ==================== 女巫受伤喷药水 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForWitch(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Witch witch && event.getDamager() instanceof Player) {
            if (plugin.getConfigManager().getMobConfig("witch") != null &&
                plugin.getConfigManager().getMobConfig("witch").has("splash_potions_on_damage")) {
                // 在4个方向生成喷溅药水
                for (int i = 0; i < 4; i++) {
                    if (random.nextDouble() < 0.5) {
                        double offsetX = (i % 2 == 0 ? 0.75 : -0.75);
                        double offsetZ = (i < 2 ? 0.75 : -0.75);
                        Location potionLoc = witch.getLocation().add(offsetX, 1.5, offsetZ);

                        // 生成药水弹射物
                        ThrownPotion potion = witch.getWorld().spawn(potionLoc, ThrownPotion.class);
                        potion.setVelocity(new Vector(offsetX * 0.3, 0.3, offsetZ * 0.3));
                    }
                }
            }
        }
    }

    // ==================== 幻术师行为 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForIllusioner(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Illusioner illusioner) {
            if (plugin.getConfigManager().getMobConfig("illusioner") != null) {
                if (plugin.getConfigManager().getMobConfig("illusioner").has("power_bow") &&
                    plugin.getConfigManager().getMobConfig("illusioner").get("power_bow").getAsBoolean()) {
                    illusioner.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                }
            }
        }
    }

    // ==================== 末影人破坏方块 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForEndermanBlock(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof Enderman enderman && event.getEntity() instanceof Player) {
            if (plugin.getConfigManager().getMobConfig("enderman").has("destroy_blocks") &&
                plugin.getConfigManager().getMobConfig("enderman").get("destroy_blocks").getAsBoolean()) {
                Location headLoc = enderman.getLocation().add(0, 1, 0);
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            org.bukkit.block.Block block = headLoc.getBlock().getRelative(x, y, z);
                            if (block.getType() != Material.AIR &&
                                block.getType() != Material.BEDROCK &&
                                block.getType() != Material.OBSIDIAN) {
                                block.breakNaturally();
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== 凋灵骷髅破坏方块 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForWitherSkeleton(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof WitherSkeleton witherSkeleton && event.getEntity() instanceof Player player) {
            if (plugin.getConfigManager().getMobConfig("wither_skeleton") != null) {
                if (plugin.getConfigManager().getMobConfig("wither_skeleton").has("wither_effect") &&
                    plugin.getConfigManager().getMobConfig("wither_skeleton").get("wither_effect").getAsBoolean()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1, false, false));
                }

                if (plugin.getConfigManager().getMobConfig("wither_skeleton").has("destroy_blocks_near_skulls") &&
                    plugin.getConfigManager().getMobConfig("wither_skeleton").get("destroy_blocks_near_skulls").getAsBoolean()) {
                    Location skullLoc = witherSkeleton.getLocation().add(0, 1, 0);
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {
                                org.bukkit.block.Block block = skullLoc.getBlock().getRelative(x, y, z);
                                if (block.getType() != Material.AIR &&
                                    block.getType() != Material.BEDROCK &&
                                    block.getType() != Material.OBSIDIAN) {
                                    block.breakNaturally();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== 着火火焰轨迹 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamageForFireTrail(EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            if (player.getFireTicks() > 0) {
                if (plugin.getConfigManager().getPlayerConfig().has("fire_trail_on_fire") &&
                    plugin.getConfigManager().getPlayerConfig().get("fire_trail_on_fire").getAsBoolean()) {
                    if (Math.random() < 0.1) {
                        Location loc = player.getLocation();
                        if (loc.getBlock().getType() == Material.AIR) {
                            loc.getBlock().setType(Material.FIRE);
                        }
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 0, false, false));
                }
            }
        }
    }

    // ==================== 桶漏水 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractForBucketLeak(PlayerInteractEvent event) {
        if (event.isCancelled()) return;

        if (event.getItem() != null &&
            (event.getItem().getType() == Material.WATER_BUCKET ||
             event.getItem().getType() == Material.LAVA_BUCKET)) {
            if (plugin.getConfigManager().getItemsConfig().has("buckets_leak") &&
                plugin.getConfigManager().getItemsConfig().get("buckets_leak").getAsBoolean()) {
                if (Math.random() < 0.1) {
                    event.getItem().setType(Material.BUCKET);
                    event.getPlayer().sendMessage("§c你的桶漏水了！");
                }
            }
        }
    }

    // ==================== 下界炼药锅干涸 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlaceForCauldron(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.CAULDRON ||
            event.getBlock().getType() == Material.WATER_CAULDRON ||
            event.getBlock().getType() == Material.LAVA_CAULDRON) {
            if (event.getBlock().getWorld().getEnvironment() == World.Environment.NETHER) {
                if (plugin.getConfigManager().getBlocksConfig().has("cauldron_dries_in_nether") &&
                    plugin.getConfigManager().getBlocksConfig().get("cauldron_dries_in_nether").getAsBoolean()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (event.getBlock().getType() == Material.WATER_CAULDRON ||
                                event.getBlock().getType() == Material.LAVA_CAULDRON) {
                                event.getBlock().setType(Material.CAULDRON);
                            }
                        }
                    }.runTaskLater(plugin, 100L);
                }
            }
        }
    }

    // ==================== 恶魂火球更大爆炸 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplodeForGhastFireball(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Fireball fireball) {
            if (plugin.getConfigManager().getMobConfig("ghast") != null &&
                plugin.getConfigManager().getMobConfig("ghast").has("larger_explosions") &&
                plugin.getConfigManager().getMobConfig("ghast").get("larger_explosions").getAsBoolean()) {
                event.setYield(event.getYield() * 2);
            }
        }
    }

    // ==================== 岩浆气泡流更强大 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForMagma(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        if (plugin.getConfigManager().getBlocksConfig().has("magma_bubble_streams_more_powerful") &&
            plugin.getConfigManager().getBlocksConfig().get("magma_bubble_streams_more_powerful").getAsBoolean()) {
            if (event.getPlayer().getLocation().getBlock().getType() == Material.MAGMA_BLOCK) {
                if (Math.random() < 0.1) {
                    event.getPlayer().damage(1.0);
                }
            }
        }
    }
}
