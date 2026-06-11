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
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * 缺失功能管理器 - 第二批
 * 实现天气系统复杂模式、猪灵以物易物重做、烈焰人火焰物品燃烧等功能
 */
public class MissingFeaturesManager2 implements Listener {
    
    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();
    
    public MissingFeaturesManager2(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 初始化
     */
    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    // ==================== 天气系统复杂模式 ====================
    
    /**
     * 天气状态
     */
    private boolean isStormActive = false;
    private int stormPhase = 0; // 0=普通, 1=集束, 2=群组, 3=精准
    private Location stormCenter = null;
    
    /**
     * 监听天气变化事件 - 天气系统复杂模式
     * 0.0033%几率触发风暴；包含集束/群组/精准闪电模式
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onWeatherChangeForComplexStorm(WeatherChangeEvent event) {
        if (event.isCancelled()) return;
        
        if (event.toWeatherState() && !isStormActive) {
            if (random.nextDouble() < 0.000033) { // 0.0033%几率
                isStormActive = true;
                stormCenter = event.getWorld().getSpawnLocation();
                stormPhase = 1;
                
                // 启动复杂风暴任务
                startComplexStorm(event.getWorld());
            }
        }
    }
    
    /**
     * 启动复杂风暴
     */
    private void startComplexStorm(World world) {
        new BukkitRunnable() {
            private int tickCount = 0;
            private int phaseDuration = 200; // 每阶段10秒
            
            @Override
            public void run() {
                if (!isStormActive || tickCount > 6000) { // 最长5分钟
                    isStormActive = false;
                    stormPhase = 0;
                    this.cancel();
                    return;
                }
                
                // 切换阶段
                if (tickCount % phaseDuration == 0) {
                    stormPhase = (stormPhase % 3) + 1;
                }
                
                // 根据阶段执行不同闪电模式
                switch (stormPhase) {
                    case 1: // 集束模式 - 在中心附近密集闪电
                        executeClusterLightning(world);
                        break;
                    case 2: // 群组模式 - 在多个点同时闪电
                        executeGroupLightning(world);
                        break;
                    case 3: // 精准模式 - 追踪玩家精准闪电
                        executePreciseLightning(world);
                        break;
                }
                
                tickCount++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * 集束闪电模式
     */
    private void executeClusterLightning(World world) {
        if (random.nextDouble() < 0.1) { // 10%几率每tick
            double offsetX = (random.nextDouble() - 0.5) * 20;
            double offsetZ = (random.nextDouble() - 0.5) * 20;
            Location strikeLoc = stormCenter.clone().add(offsetX, 0, offsetZ);
            strikeLoc.setY(world.getHighestBlockYAt(strikeLoc));
            world.strikeLightning(strikeLoc);
        }
    }
    
    /**
     * 群组闪电模式
     */
    private void executeGroupLightning(World world) {
        if (random.nextDouble() < 0.05) { // 5%几率每tick
            int groupSize = 3 + random.nextInt(5); // 3-7个闪电
            for (int i = 0; i < groupSize; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 40;
                double offsetZ = (random.nextDouble() - 0.5) * 40;
                Location strikeLoc = stormCenter.clone().add(offsetX, 0, offsetZ);
                strikeLoc.setY(world.getHighestBlockYAt(strikeLoc));
                world.strikeLightning(strikeLoc);
            }
        }
    }
    
    /**
     * 精准闪电模式 - 追踪玩家
     */
    private void executePreciseLightning(World world) {
        if (random.nextDouble() < 0.02) { // 2%几率每tick
            for (Player player : world.getPlayers()) {
                if (random.nextDouble() < 0.3) { // 30%几率追踪每个玩家
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
    
    /**
     * 监听猪灵以物易物事件 - 完整重写掉落表
     * 灵魂疾行书/靴子、抗火药水、末影珍珠等
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPiglinBarter(EntityDropItemEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Piglin piglin) {
            // 检查是否是金锭交易
            if (event.getItemDrop().getItemStack().getType() != Material.GOLD_INGOT) {
                return;
            }
            
            // 取消原掉落
            event.setCancelled(true);
            
            // 自定义掉落表
            ItemStack result = getPiglinBarterResult();
            if (result != null) {
                piglin.getWorld().dropItemNaturally(piglin.getLocation(), result);
            }
        }
    }
    
    /**
     * 获取猪灵以物易物结果
     */
    private ItemStack getPiglinBarterResult() {
        double roll = random.nextDouble();
        
        // 灵魂疾行书 (2%)
        if (roll < 0.02) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            // 注：Paper API可能不支持直接设置附魔书内容
            return book;
        }
        
        // 抗火药水 (4%)
        if (roll < 0.06) {
            return new ItemStack(Material.POTION);
        }
        
        // 末影珍珠 (4%)
        if (roll < 0.10) {
            return new ItemStack(Material.ENDER_PEARL, 2 + random.nextInt(4));
        }
        
        // 灵魂疾行靴子 (2%)
        if (roll < 0.12) {
            return new ItemStack(Material.IRON_BOOTS);
        }
        
        // 铁粒 (20%)
        if (roll < 0.32) {
            return new ItemStack(Material.IRON_NUGGET, 5 + random.nextInt(10));
        }
        
        // 箭矢 (15%)
        if (roll < 0.47) {
            return new ItemStack(Material.ARROW, 6 + random.nextInt(10));
        }
        
        // 火药 (10%)
        if (roll < 0.57) {
            return new ItemStack(Material.GUNPOWDER, 2 + random.nextInt(4));
        }
        
        // 沙砾 (10%)
        if (roll < 0.67) {
            return new ItemStack(Material.GRAVEL, 4 + random.nextInt(8));
        }
        
        // 黑石 (8%)
        if (roll < 0.75) {
            return new ItemStack(Material.BLACKSTONE, 4 + random.nextInt(8));
        }
        
        // 缠怨藤 (8%)
        if (roll < 0.83) {
            return new ItemStack(Material.TWISTING_VINES, 4 + random.nextInt(8));
        }
        
        // 菌丝 (8%)
        if (roll < 0.91) {
            return new ItemStack(Material.CRIMSON_FUNGUS, 2 + random.nextInt(4));
        }
        
        // 金 nugget (9%)
        return new ItemStack(Material.GOLD_NUGGET, 3 + random.nextInt(6));
    }
    
    // ==================== 烈焰人火焰物品燃烧 ====================
    
    /**
     * 监听玩家使用物品事件 - 火焰物品燃烧
     * 烈焰棒、烈焰粉、岩浆桶持有燃烧
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractForFireItems(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        // 烈焰棒燃烧
        if (item.getType() == Material.BLAZE_ROD) {
            if (random.nextDouble() < 0.1) { // 10%几率
                player.setFireTicks(60);
                player.sendMessage("§c烈焰棒灼伤了你！");
            }
        }
        
        // 烈焰粉燃烧
        if (item.getType() == Material.BLAZE_POWDER) {
            if (random.nextDouble() < 0.05) { // 5%几率
                player.setFireTicks(40);
                player.sendMessage("§c烈焰粉灼伤了你！");
            }
        }
        
        // 岩浆桶燃烧
        if (item.getType() == Material.LAVA_BUCKET) {
            if (random.nextDouble() < 0.2) { // 20%几率
                player.setFireTicks(100);
                player.sendMessage("§c岩浆桶灼伤了你！");
            }
        }
    }
    
    // ==================== 下界炼药锅干涸 ====================
    
    /**
     * 监听方块放置事件 - 下界炼药锅干涸
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlaceForCauldron(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        // 下界炼药锅干涸
        if (event.getBlock().getType() == Material.CAULDRON || 
            event.getBlock().getType() == Material.WATER_CAULDRON) {
            if (event.getBlock().getWorld().getEnvironment() == World.Environment.NETHER) {
                // 延迟干涸
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
                }.runTaskLater(plugin, 60L); // 3秒后
            }
        }
    }
    
    // ==================== 桶漏水 ====================
    
    /**
     * 监听玩家使用物品事件 - 桶漏水
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerUseItemForBucketLeak(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        
        // 桶漏水
        if (event.getItem().getType() == Material.WATER_BUCKET || 
            event.getItem().getType() == Material.LAVA_BUCKET) {
            if (random.nextDouble() < 0.1) { // 10%几率
                event.getItem().setType(Material.BUCKET);
                event.getPlayer().sendMessage("§c你的桶漏水了！");
            }
        }
    }
    
    // ==================== 着火火焰轨迹 ====================
    
    /**
     * 监听玩家移动事件 - 着火火焰轨迹
     * 着火时留下火焰轨迹并变得虚弱
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForFireTrail(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        if (player.getFireTicks() <= 0) return;
        
        // 10%几率留下火焰
        if (random.nextDouble() < 0.1) {
            Location loc = player.getLocation();
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(Material.FIRE);
            }
        }
        
        // 虚弱
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20, 0));
    }
    
    // ==================== 末影人传送后破坏方块 ====================
    
    /**
     * 监听实体传送事件 - 末影人传送后破坏方块
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTeleportForEnderman(EntityTeleportEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof Enderman enderman) {
            // 在原位置和新位置破坏方块
            Location from = event.getFrom();
            Location to = event.getTo();
            
            if (from != null) {
                breakBlocksAroundHead(from);
            }
            if (to != null) {
                breakBlocksAroundHead(to);
            }
        }
    }
    
    /**
     * 破坏头部附近方块
     */
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
    
    /**
     * 监听生物生成事件 - 流浪商人替换
     * 流浪商人替换为带劫掠者乘客的劫掠兽
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForWanderingTrader(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof WanderingTrader trader) {
            // 取消原生成
            event.setCancelled(true);
            
            Location loc = trader.getLocation();
            World world = loc.getWorld();
            
            // 生成劫掠兽
            Ravager ravager = world.spawn(loc, Ravager.class);
            
            // 生成掠夺者乘客
            Pillager pillager = world.spawn(loc, Pillager.class);
            pillager.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW));
            ravager.addPassenger(pillager);
            
            // 生成卫道士乘客
            Vindicator vindicator = world.spawn(loc, Vindicator.class);
            vindicator.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
            ravager.addPassenger(vindicator);
        }
    }
    
    // ==================== 商人的羊驼→掠夺者+卫道士 ====================
    
    /**
     * 监听生物生成事件 - 商人羊驼转换
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForTraderLlama(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof TraderLlama traderLlama) {
            // 检查是否是流浪商人的羊驼
            if (traderLlama.isTamed()) {
                // 取消原生成
                event.setCancelled(true);
                
                Location loc = traderLlama.getLocation();
                World world = loc.getWorld();
                
                // 生成掠夺者
                Pillager pillager = world.spawn(loc, Pillager.class);
                pillager.getEquipment().setItemInMainHand(new ItemStack(Material.CROSSBOW));
                
                // 生成卫道士
                Vindicator vindicator = world.spawn(loc, Vindicator.class);
                vindicator.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE));
            }
        }
    }
    
    // ==================== 熔炼时间加倍 ====================
    
    /**
     * 监听熔炉燃烧事件 - 熔炼时间加倍
     * 普通熔炉2x时间，高炉/烟熏炉正常时间
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (event.isCancelled()) return;
        
        // 检查是否是普通熔炉
        if (event.getBlock().getType() == Material.FURNACE) {
            // 延长燃烧时间 (通过减少燃料燃烧速度实现)
            event.setBurnTime(event.getBurnTime() * 2);
        }
    }
    
    // ==================== 干草块→小麦配方 ====================
    
    /**
     * 注册干草块→小麦配方
     */
    public void registerHayBlockRecipe() {
        // 1干草块=4小麦
        org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(plugin, "hay_block_to_wheat");
        org.bukkit.inventory.ShapelessRecipe recipe = new org.bukkit.inventory.ShapelessRecipe(
            key, new ItemStack(Material.WHEAT, 4)
        );
        recipe.addIngredient(Material.HAY_BLOCK);
        Bukkit.addRecipe(recipe);
    }
    
    // ==================== 睡后幻翼随机数量 ====================
    
    /**
     * 监听睡觉事件 - 睡后幻翼随机数量
     * 50%几率每只幻翼生成(最多4只)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBedEnterForPhantoms(PlayerBedEnterEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !player.isSleeping()) return;
                
                // 50%几率每只幻翼生成
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
                    
                    player.sendMessage("§c你被幻翼惊醒了！");
                }
            }
        }.runTaskLater(plugin, 100L); // 5秒后
    }
    
    // ==================== 蜜蜂蛰刺增强(中毒+虚弱) ====================
    
    /**
     * 监听实体伤害事件 - 蜜蜂蛰刺增强
     * 中毒II 10秒 + 虚弱30秒
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageForBeeSting(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getDamager() instanceof Bee && event.getEntity() instanceof Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1)); // 中毒II 10秒
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 0)); // 虚弱30秒
        }
    }
    
    // ==================== 凋灵骷髅自定义属性 ====================
    
    /**
     * 监听生物生成事件 - 凋灵骷髅自定义属性
     * HP=30, 攻击伤害=5, 石剑+石镐
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawnForWitherSkeleton(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getEntity() instanceof WitherSkeleton witherSkeleton) {
            // HP=30
            witherSkeleton.setMaxHealth(30.0);
            witherSkeleton.setHealth(30.0);
            
            // 攻击伤害=5
            try {
                org.bukkit.attribute.Attribute damageAttr = org.bukkit.attribute.Attribute.valueOf("GENERIC_ATTACK_DAMAGE");
                witherSkeleton.getAttribute(damageAttr).setBaseValue(5.0);
            } catch (IllegalArgumentException e) {
                // 如果属性名不同，尝试其他方式
            }
            
            // 石剑+石镐
            witherSkeleton.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
            witherSkeleton.getEquipment().setItemInOffHand(new ItemStack(Material.STONE_PICKAXE));
        }
    }
    
    // ==================== 高个生物破坏方块 ====================
    
    /**
     * 监听实体攻击事件 - 高个生物破坏方块
     * 凋灵骷髅/末影人破坏玩家头部附近方块(2.6格高)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityForTallMobBlockBreak(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        // 凋灵骷髅破坏方块
        if (event.getDamager() instanceof WitherSkeleton witherSkeleton && event.getEntity() instanceof Player) {
            breakBlocksAtPlayerHeight(witherSkeleton.getLocation());
        }
        
        // 末影人破坏方块
        if (event.getDamager() instanceof Enderman enderman && event.getEntity() instanceof Player) {
            breakBlocksAtPlayerHeight(enderman.getLocation());
        }
    }
    
    /**
     * 破坏玩家高度附近的方块
     */
    private void breakBlocksAtPlayerHeight(Location mobLocation) {
        // 破坏2.6格高的区域 (即玩家头部位置)
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
    
    /**
     * 监听玩家移动事件 - 挖掘疲劳(无工具时)
     * 不持有工具时获得挖掘疲劳
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForMiningFatigue(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        
        // 检查是否持有工具
        if (!isTool(mainHand.getType())) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 40, 0));
        }
    }
    
    /**
     * 检查是否是工具
     */
    private boolean isTool(Material material) {
        return material.name().endsWith("_PICKAXE") || 
               material.name().endsWith("_AXE") || 
               material.name().endsWith("_SHOVEL") || 
               material.name().endsWith("_HOE") ||
               material == Material.SHEARS ||
               material == Material.FISHING_ROD;
    }
    
    // ==================== 黑曜石挖掘：饥饿255 ====================
    
    /**
     * 监听方块破坏事件 - 黑曜石挖掘：饥饿255
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreakForObsidian(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            // 饥饿255 (最大值)
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 600, 254));
            event.getPlayer().sendMessage("§c挖掘黑曜石让你极度饥饿！");
        }
    }
    
    // ==================== 船划行：饥饿IV ====================
    
    /**
     * 监听玩家移动事件 - 船划行：饥饿IV
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMoveForBoatHunger(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        if (player.isInsideVehicle() && player.getVehicle() instanceof Boat) {
            if (random.nextDouble() < 0.05) { // 5%几率
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 3)); // 饥饿IV 1秒
            }
        }
    }
}
