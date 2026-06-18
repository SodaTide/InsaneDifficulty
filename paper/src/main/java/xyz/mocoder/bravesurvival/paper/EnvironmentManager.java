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
import xyz.mocoder.bravesurvival.core.config.ConfigManager;

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
        startDoorBreakInWaterTask();
    }
    
    // ==================== 水中门破碎 ====================
    
    private void startDoorBreakInWaterTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!ConfigManager.getWorldConfig().has("doors_break_in_water") ||
                    !ConfigManager.getWorldConfig().get("doors_break_in_water").getAsBoolean()) {
                    return;
                }
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location loc = player.getLocation();
                    
                    // 检查脚下和周围的门方块
                    for (int x = -1; x <= 1; x++) {
                        for (int y = 0; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {
                                if (x == 0 && y == 0 && z == 0) continue;
                                
                                Block block = loc.getBlock().getRelative(x, y, z);
                                if (isDoor(block.getType()) && isBlockAdjacentToWater(block)) {
                                    // 播放破坏音效
                                    block.getWorld().playSound(block.getLocation(), 
                                        org.bukkit.Sound.BLOCK_WOOD_BREAK, 1.0f, 1.0f);
                                    // 破坏门
                                    block.breakNaturally();
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 10L); // 每10tick检查一次
    }
    
    private boolean isDoor(Material type) {
        return type.name().contains("DOOR") && !type.name().equals("IRON_DOOR") && !type.name().equals("IRON_TRAPDOOR");
    }
    
    private boolean isBlockAdjacentToWater(Block block) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Material adjType = block.getRelative(x, y, z).getType();
                    if (adjType.name().contains("WATER")) {
                        return true;
                    }
                }
            }
        }
        return false;
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
        // 数据包: piglin_bartering.json 使用权重系统
        // 简化版本，使用替代物品代替自定义效果药水
        double roll = random.nextDouble() * 100;

        // 权重计算基于数据包 (total weight: 401)
        if (roll < 1.25) return new ItemStack(Material.BOOK);  // weight 5 (enchanted book)
        if (roll < 3.25) return new ItemStack(Material.IRON_BOOTS);  // weight 8
        if (roll < 5.25) return new ItemStack(Material.COBWEB);  // weight 8 (替代fire resistance药水效果 - 阻止移动)
        if (roll < 7.25) return new ItemStack(Material.COBWEB);  // weight 8 (替代splash fire resistance)
        if (roll < 9.75) return new ItemStack(Material.POTION);  // weight 10 (水瓶)
        if (roll < 14.25) return new ItemStack(Material.IRON_NUGGET, 10 + random.nextInt(27));  // weight 10
        if (roll < 17.75) return new ItemStack(Material.ENDER_PEARL, 2 + random.nextInt(3));  // weight 8
        if (roll < 22.75) return new ItemStack(Material.STRING, 3 + random.nextInt(7));  // weight 10
        if (roll < 27.75) return new ItemStack(Material.QUARTZ, 5 + random.nextInt(8));  // weight 10
        if (roll < 37.75) return new ItemStack(Material.OBSIDIAN);  // weight 40
        if (roll < 47.75) return new ItemStack(Material.CRYING_OBSIDIAN, 1 + random.nextInt(3));  // weight 40
        if (roll < 57.75) return new ItemStack(Material.FIRE_CHARGE);  // weight 40
        if (roll < 67.75) return new ItemStack(Material.LEATHER, 2 + random.nextInt(3));  // weight 40
        if (roll < 77.75) return new ItemStack(Material.SOUL_SAND, 2 + random.nextInt(7));  // weight 40
        if (roll < 87.75) return new ItemStack(Material.NETHER_BRICK, 2 + random.nextInt(7));  // weight 40
        if (roll < 97.75) return new ItemStack(Material.SPECTRAL_ARROW, 6 + random.nextInt(7));  // weight 40
        if (roll < 107.75) return new ItemStack(Material.GRAVEL, 8 + random.nextInt(9));  // weight 40
        return new ItemStack(Material.BLACKSTONE, 8 + random.nextInt(9));  // weight 40
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

    // ==================== 正常熔炉熔炼时间加倍（数据包: smelting cookingtime 200→400）====================
    // 数据包逻辑: 所有普通熔炉(FURNACE)的cookingtime从200翻倍到400
    // 而高炉(BLAST_FURNACE)和烟熏炉(SMOKER)保持200不变

    private static final int NORMAL_FURNACE_COOK_TIME = 400; // 普通熔炉: 200 → 400
    private static final int SLOWED_RECIPES = 400;           // 用于对比的值

    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.FURNACE) {
            org.bukkit.block.Furnace furnace = (org.bukkit.block.Furnace) event.getBlock().getState();
            // 将CookTimeTotal设为400 (原版是200)
            furnace.setCookTimeTotal(NORMAL_FURNACE_COOK_TIME);
            furnace.update();
        }
        // 高炉和烟熏炉: 保持200不变，不做修改
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

        // 数据包逻辑: 睡眠跳过1/3夜晚
        // 正常MC跳过整夜(约10000刻), 我们改为只跳过约3333刻(1/3)
        // 通过设置游戏规则来延迟唤醒
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !player.isSleeping()) return;
                
                // 数据包逻辑: 起床后50%概率每只幻翼
                for (int i = 0; i < 4; i++) {
                    if (random.nextDouble() < 0.5) {
                        Location spawnLoc = player.getLocation().clone().add(
                            random.nextInt(10) - 5,
                            20,
                            random.nextInt(10) - 5
                        );
                        player.getWorld().spawnEntity(spawnLoc, EntityType.PHANTOM);
                    }
                }

                // 数据包逻辑: hunger 5 127 (极度饥饿, 127级, 5秒), slowness 20 0 (缓慢, 20秒)
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 127, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 0, false, false));
            }
        }.runTaskLater(plugin, 125L);
    }
    
    // ==================== 睡眠跳过1/3夜晚 ====================
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBedEnterForThirdNight(PlayerBedEnterEvent event) {
        if (event.isCancelled()) return;
        if (!ConfigManager.getWorldConfig().has("sleep_skips_third_of_night") ||
            !ConfigManager.getWorldConfig().get("sleep_skips_third_of_night").getAsBoolean()) {
            return;
        }
        
        // 数据包逻辑: 睡眠只跳过1/3夜晚
        // 正常MC会跳到白天(1000刻), 我们改为只前进约3333刻
        // 设置时间增速为1/3，通过在玩家睡醒后调整时间实现
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) return;
                World world = player.getWorld();
                long time = world.getTime();
                // 夜晚范围: 12000-24000
                // 只前进1/3的时间(3600刻)
                if (time >= 12000 && time < 24000) {
                    long newTime = time + 3600;
                    if (newTime >= 24000) newTime = 24000;
                    world.setTime(newTime);
                }
            }
        }.runTaskLater(plugin, 95L); // 在幻翼生成之前执行
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

            // 数据包: 20%几率生成witch
            if (random.nextDouble() < 0.20) {
                Location spawnLoc = witherSkeleton.getLocation().add(
                    random.nextDouble() * 4 - 2,
                    0,
                    random.nextDouble() * 4 - 2
                );
                witherSkeleton.getWorld().spawn(spawnLoc, Witch.class);
            }
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
    // 数据包: obsidian.mcfunction - hunger 1 255 (1 tick, 255级)

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForObsidian(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (event.getBlock().getType() == Material.OBSIDIAN ||
            event.getBlock().getType() == Material.CRYING_OBSIDIAN) {
            // 数据包: hunger 1 255 (1 tick = 1秒, 255级)
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 1, 255, false, false));
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

            // 数据包: starving.mcfunction (lines 899-909)
            // foodLevel ≤ 10: weakness 5 0 (5秒, 0级)
            if (foodLevel <= 10) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false));
            }
            // foodLevel ≤ 7: weakness 5 1, mining_fatigue 5 0
            if (foodLevel <= 7) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 0, false, false));
            }
            // foodLevel ≤ 5: weakness 5 2, mining_fatigue 5 1, hunger 2 80
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
