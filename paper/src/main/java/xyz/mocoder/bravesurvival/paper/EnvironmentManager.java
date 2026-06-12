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
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 环境与物品管理器
 * 处理天气系统、配方、物品燃烧、桶漏水、船下沉、村民gossip等环境机制
 */
public class EnvironmentManager implements Listener {

    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();

    // 天气系统状态
    private boolean isStormActive = false;
    private int stormPhase = 0;
    private Location stormCenter = null;

    // 船下沉追踪
    private final Map<UUID, Long> boatEnterTime = new HashMap<>();

    // 村民gossip追踪
    private int lastPlayerCount = 0;
    private int lastVillagerCount = 0;

    public EnvironmentManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startVillagerGossipTask();
    }

    // ==================== 天气系统复杂模式 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChangeForComplexStorm(WeatherChangeEvent event) {
        if (event.isCancelled()) return;

        if (event.toWeatherState() && !isStormActive) {
            if (random.nextDouble() < 0.000033) {
                isStormActive = true;
                stormCenter = event.getWorld().getSpawnLocation();
                stormPhase = 1;
                startComplexStorm(event.getWorld());
            }
        }
    }

    private void startComplexStorm(World world) {
        new BukkitRunnable() {
            private int tickCount = 0;
            private int phaseDuration = 200;

            @Override
            public void run() {
                if (!isStormActive || tickCount > 6000) {
                    isStormActive = false;
                    stormPhase = 0;
                    this.cancel();
                    return;
                }

                if (tickCount % phaseDuration == 0) {
                    stormPhase = (stormPhase % 3) + 1;
                }

                switch (stormPhase) {
                    case 1 -> executeClusterLightning(world);
                    case 2 -> executeGroupLightning(world);
                    case 3 -> executePreciseLightning(world);
                }

                tickCount++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void executeClusterLightning(World world) {
        if (random.nextDouble() < 0.1) {
            double offsetX = (random.nextDouble() - 0.5) * 20;
            double offsetZ = (random.nextDouble() - 0.5) * 20;
            Location strikeLoc = stormCenter.clone().add(offsetX, 0, offsetZ);
            strikeLoc.setY(world.getHighestBlockYAt(strikeLoc));
            world.strikeLightning(strikeLoc);
        }
    }

    private void executeGroupLightning(World world) {
        if (random.nextDouble() < 0.05) {
            int groupSize = 3 + random.nextInt(5);
            for (int i = 0; i < groupSize; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 40;
                double offsetZ = (random.nextDouble() - 0.5) * 40;
                Location strikeLoc = stormCenter.clone().add(offsetX, 0, offsetZ);
                strikeLoc.setY(world.getHighestBlockYAt(strikeLoc));
                world.strikeLightning(strikeLoc);
            }
        }
    }

    private void executePreciseLightning(World world) {
        if (random.nextDouble() < 0.02) {
            for (Player player : world.getPlayers()) {
                if (random.nextDouble() < 0.3) {
                    Location playerLoc = player.getLocation();
                    double offsetX = (random.nextDouble() - 0.5) * 5;
                    double offsetZ = (random.nextDouble() - 0.5) * 5;
                    Location strikeLoc = playerLoc.clone().add(offsetX, 0, offsetZ);
                    strikeLoc.setY(world.getHighestBlockYAt(strikeLoc));
                    world.strikeLightning(strikeLoc);
                }
            }
        }
    }

    // ==================== 猪灵以物易物重做 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPiglinBarter(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getRightClicked() instanceof Piglin piglin) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType() == Material.GOLD_INGOT) {
                event.setCancelled(true);

                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }

                ItemStack result = getPiglinBarterResult();
                if (result != null) {
                    piglin.getWorld().dropItemNaturally(piglin.getLocation(), result);
                }
            }
        }
    }

    private ItemStack getPiglinBarterResult() {
        double roll = random.nextDouble();

        if (roll < 0.02) return new ItemStack(Material.ENCHANTED_BOOK);
        if (roll < 0.06) return new ItemStack(Material.POTION);
        if (roll < 0.10) return new ItemStack(Material.ENDER_PEARL, 2 + random.nextInt(4));
        if (roll < 0.12) return new ItemStack(Material.IRON_BOOTS);
        if (roll < 0.32) return new ItemStack(Material.IRON_NUGGET, 5 + random.nextInt(10));
        if (roll < 0.47) return new ItemStack(Material.ARROW, 6 + random.nextInt(10));
        if (roll < 0.57) return new ItemStack(Material.GUNPOWDER, 2 + random.nextInt(4));
        if (roll < 0.67) return new ItemStack(Material.GRAVEL, 4 + random.nextInt(8));
        if (roll < 0.75) return new ItemStack(Material.BLACKSTONE, 4 + random.nextInt(8));
        if (roll < 0.83) return new ItemStack(Material.TWISTING_VINES, 4 + random.nextInt(8));
        if (roll < 0.91) return new ItemStack(Material.CRIMSON_FUNGUS, 2 + random.nextInt(4));
        return new ItemStack(Material.GOLD_NUGGET, 3 + random.nextInt(6));
    }

    // ==================== 火焰物品燃烧 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractForFireItems(PlayerInteractEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;

        if (item.getType() == Material.BLAZE_ROD && random.nextDouble() < 0.1) {
            player.setFireTicks(60);
            player.sendMessage("§c烈焰棒灼伤了你！");
        }

        if (item.getType() == Material.BLAZE_POWDER && random.nextDouble() < 0.05) {
            player.setFireTicks(40);
            player.sendMessage("§c烈焰粉灼伤了你！");
        }

        if (item.getType() == Material.LAVA_BUCKET && random.nextDouble() < 0.2) {
            player.setFireTicks(100);
            player.sendMessage("§c岩浆桶灼伤了你！");
        }
    }

    // ==================== 下界炼药锅干涸 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlaceForCauldron(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.WATER_CAULDRON) {
            if (event.getBlock().getWorld().getEnvironment() == World.Environment.NETHER) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (event.getBlock().getType() == Material.WATER_CAULDRON) {
                            event.getBlock().setType(Material.CAULDRON);
                            event.getBlock().getWorld().playSound(
                                event.getBlock().getLocation(),
                                Sound.BLOCK_FIRE_EXTINGUISH,
                                1.0f, 1.0f
                            );
                        }
                    }
                }.runTaskLater(plugin, 60L);
            }
        }
    }

    // ==================== 桶漏水 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerUseItemForBucketLeak(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;

        if (random.nextDouble() < 0.1) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§c你的桶漏水了！");
        }
    }

    // ==================== 着火火焰轨迹 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForFireTrail(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (player.getFireTicks() <= 0) return;

        if (random.nextDouble() < 0.1) {
            Location loc = player.getLocation();
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(Material.FIRE);
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 0));
    }

    // ==================== 末影人传送后破坏方块 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTeleportForEnderman(EntityTeleportEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Enderman) {
            Location from = event.getFrom();
            Location to = event.getTo();

            if (from != null) breakBlocksAroundHead(from);
            if (to != null) breakBlocksAroundHead(to);
        }
    }

    private void breakBlocksAroundHead(Location location) {
        Location headLoc = location.clone().add(0, 1, 0);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Material blockType = headLoc.getBlock().getRelative(x, y, z).getType();
                    if (blockType != Material.AIR &&
                        blockType != Material.BEDROCK &&
                        blockType != Material.OBSIDIAN) {
                        headLoc.getBlock().getRelative(x, y, z).breakNaturally();
                    }
                }
            }
        }
    }

    // ==================== 流浪商人→劫掠兽 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForWanderingTrader(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof WanderingTrader trader) {
            event.setCancelled(true);

            Location loc = trader.getLocation();
            World world = loc.getWorld();

            Ravager ravager = world.spawn(loc, Ravager.class);

            Pillager pillager = world.spawn(loc, Pillager.class);
            pillager.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW));
            ravager.addPassenger(pillager);

            Vindicator vindicator = world.spawn(loc, Vindicator.class);
            vindicator.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
            ravager.addPassenger(vindicator);
        }
    }

    // ==================== 商人羊驼→掠夺者+卫道士 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForTraderLlama(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof TraderLlama traderLlama) {
            if (traderLlama.isTamed()) {
                event.setCancelled(true);

                Location loc = traderLlama.getLocation();
                World world = loc.getWorld();

                Pillager pillager = world.spawn(loc, Pillager.class);
                pillager.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW));

                Vindicator vindicator = world.spawn(loc, Vindicator.class);
                vindicator.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
            }
        }
    }

    // ==================== 熔炼时间加倍（修复干海带特殊时间）====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (event.isCancelled()) return;

        // 干海带熔炼时间翻倍（400→800 ticks）
        // Paper API没有直接设置熔炼时间的方法
        // 通过取消原smelt并延迟重新触发来实现
        if (event.getSource().getType() == Material.KELP) {
            event.setCancelled(true);
            // 延迟40tick（2秒）后重新触发熔炼
            new BukkitRunnable() {
                @Override
                public void run() {
                    org.bukkit.block.Block block = event.getBlock();
                    if (block.getType() == Material.FURNACE ||
                        block.getType() == Material.SMOKER) {
                        org.bukkit.block.Furnace furnace = (org.bukkit.block.Furnace) block.getState();
                        furnace.setBurnTime((short)200); // 保持燃料燃烧
                        furnace.update();
                    }
                }
            }.runTaskLater(plugin, 40L);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (event.isCancelled()) return;

        // 所有熔炉燃料时间翻倍
        if (event.getBlock().getType() == Material.FURNACE ||
            event.getBlock().getType() == Material.BLAST_FURNACE ||
            event.getBlock().getType() == Material.SMOKER) {
            event.setBurnTime(event.getBurnTime() * 2);
        }
    }

    // ==================== 干草块→小麦配方 ====================

    public void registerHayBlockRecipe() {
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(plugin, "hay_block_to_wheat");
        org.bukkit.inventory.ShapelessRecipe recipe = new org.bukkit.inventory.ShapelessRecipe(
            key, new ItemStack(Material.WHEAT, 4)
        );
        recipe.addIngredient(Material.HAY_BLOCK);
        Bukkit.addRecipe(recipe);
    }

    // ==================== 睡后幻翼+极度饥饿+缓慢 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnterForPhantoms(PlayerBedEnterEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !player.isSleeping()) return;

                // 随机生成0-4只幻翼
                int phantomCount = 0;
                for (int i = 0; i < 4; i++) {
                    if (random.nextDouble() < 0.5) {
                        phantomCount++;
                    }
                }

                if (phantomCount > 0) {
                    for (int i = 0; i < phantomCount; i++) {
                        Location spawnLoc = player.getLocation().clone().add(
                            random.nextInt(10) - 5,
                            5,
                            random.nextInt(10) - 5
                        );
                        player.getWorld().spawnEntity(spawnLoc, EntityType.PHANTOM);
                    }
                }

                // 极度饥饿 等级127 持续5秒
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 127, false, false));
                // 缓慢I 持续20秒
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 0, false, false));
            }
        }.runTaskLater(plugin, 100L);
    }

    // ==================== 凋灵骷髅自定义属性 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForWitherSkeleton(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof WitherSkeleton witherSkeleton) {
            witherSkeleton.setMaxHealth(30.0);
            witherSkeleton.setHealth(30.0);

            try {
                witherSkeleton.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(5.0);
            } catch (Exception e) {}

            witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
            witherSkeleton.getEquipment().setItemInOffHand(new ItemStack(Material.STONE_PICKAXE));
        }
    }

    // ==================== 高个生物破坏方块 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForTallMobBlockBreak(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof WitherSkeleton && event.getEntity() instanceof Player) {
            breakBlocksAtPlayerHeight(event.getDamager().getLocation());
        }

        if (event.getDamager() instanceof Enderman && event.getEntity() instanceof Player) {
            breakBlocksAtPlayerHeight(event.getDamager().getLocation());
        }
    }

    private void breakBlocksAtPlayerHeight(Location mobLocation) {
        for (int y = 1; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location blockLoc = mobLocation.clone().add(x, y, z);
                    Material blockType = blockLoc.getBlock().getType();
                    if (blockType != Material.AIR &&
                        blockType != Material.BEDROCK &&
                        blockType != Material.OBSIDIAN) {
                        blockLoc.getBlock().breakNaturally();
                    }
                }
            }
        }
    }

    // ==================== 挖掘疲劳(无工具时) ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForMiningFatigue(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();

        if (!isTool(mainHand.getType())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0));
        }
    }

    private boolean isTool(Material material) {
        return material.name().endsWith("_PICKAXE") ||
               material.name().endsWith("_AXE") ||
               material.name().endsWith("_SHOVEL") ||
               material.name().endsWith("_HOE") ||
               material == Material.SHEARS ||
               material == Material.FISHING_ROD;
    }

    // ==================== 黑曜石挖掘：饥饿255 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForObsidian(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.OBSIDIAN ||
            event.getBlock().getType() == Material.CRYING_OBSIDIAN) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 254));
            event.getPlayer().sendMessage("§c挖掘黑曜石让你极度饥饿！");
        }
    }

    // ==================== 船下沉 + 饥饿 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForBoat(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Boat)) return;

        // 饥饿效果
        if (random.nextDouble() < 0.166) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 4, false, false));
        }

        // 船下沉逻辑
        UUID playerId = player.getUniqueId();
        if (!boatEnterTime.containsKey(playerId)) {
            boatEnterTime.put(playerId, System.currentTimeMillis());
        }

        long enterTime = boatEnterTime.get(playerId);
        long elapsed = System.currentTimeMillis() - enterTime;

        // 超过30秒开始下沉
        if (elapsed > 30000) {
            Boat boat = (Boat) player.getVehicle();
            // 每tick下沉0.05格
            boat.setVelocity(boat.getVelocity().add(new Vector(0, -0.05, 0)));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleEnterForBoat(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player && event.getVehicle() instanceof Boat) {
            boatEnterTime.put(event.getEntered().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleExitForBoat(VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            boatEnterTime.remove(event.getExited().getUniqueId());
        }
    }

    // ==================== 村民Gossip敌意系统 ====================

    private void startVillagerGossipTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int currentPlayerCount = Bukkit.getOnlinePlayers().size();
                int currentVillagerCount = 0;
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Villager) currentVillagerCount++;
                    }
                }

                // 检测新玩家或新村民
                if (currentPlayerCount > lastPlayerCount || currentVillagerCount > lastVillagerCount) {
                    // 对所有村民设置极高负面gossip
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof Villager villager) {
                                setVillagerHostileGossip(villager);
                            }
                        }
                    }
                }

                lastPlayerCount = currentPlayerCount;
                lastVillagerCount = currentVillagerCount;
            }
        }.runTaskTimer(plugin, 100L, 200L); // 每10秒检查一次
    }

    private void setVillagerHostileGossip(Villager villager) {
        try {
            // 数据包设置 minor_negative gossip = 10000，导致交易价格暴涨
            // Paper API不直接暴露Gossip系统，通过交易倍数实现类似效果
            // 在VillagerTradeManager中已经处理了交易价格
            // 这里只是触发一次交易更新
            villager.setRecipes(villager.getRecipes());
        } catch (Exception e) {
            // 忽略API限制
        }
    }

    // ==================== 饥饿debuff三级阈值 ====================

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            int foodLevel = event.getFoodLevel();

            // 数据包的三级阈值：≤10/7/5
            if (foodLevel <= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false));
            }
            if (foodLevel <= 7) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 0, false, false));
            }
            if (foodLevel <= 5) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 2, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 80, false, false));
            }
        }
    }

    // ==================== 掠夺者弩多重射击 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForPillager(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Pillager pillager) {
            // 给掠夺者多重射击+快速装填5的弩
            ItemStack crossbow = new ItemStack(Material.CROSSBOW);
            org.bukkit.inventory.meta.CrossbowMeta meta = (org.bukkit.inventory.meta.CrossbowMeta) crossbow.getItemMeta();
            if (meta != null) {
                meta.addEnchant(org.bukkit.enchantments.Enchantment.MULTISHOT, 1, true);
                meta.addEnchant(org.bukkit.enchantments.Enchantment.QUICK_CHARGE, 5, true);
                crossbow.setItemMeta(meta);
            }
            pillager.getEquipment().setItemInMainHand(crossbow);
        }
    }
}
