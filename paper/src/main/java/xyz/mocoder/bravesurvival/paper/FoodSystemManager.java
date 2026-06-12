package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

/**
 * 食物系统管理器
 * 实现生肉惩罚、肉类变质、腐肉加强、食物中毒
 */
public class FoodSystemManager implements Listener {

    private final BraveSurvivalPlugin plugin;
    private final Random random = new Random();

    // 生肉类
    private static final Set<Material> RAW_MEATS = EnumSet.of(
            Material.BEEF, Material.PORKCHOP, Material.CHICKEN, Material.MUTTON,
            Material.RABBIT, Material.COD, Material.SALMON, Material.TROPICAL_FISH
    );

    // 熟肉类（背包中会变质的）
    private static final Set<Material> COOKED_MEATS = EnumSet.of(
            Material.COOKED_BEEF, Material.COOKED_PORKCHOP, Material.COOKED_CHICKEN,
            Material.COOKED_MUTTON, Material.COOKED_RABBIT, Material.COOKED_COD,
            Material.COOKED_SALMON
    );

    // 所有肉类（生+熟）
    private static final Set<Material> ALL_MEATS = EnumSet.copyOf(RAW_MEATS);
    static {
        ALL_MEATS.addAll(COOKED_MEATS);
    }

    public FoodSystemManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startMeatSpoilTask();
    }

    // ==================== 生肉惩罚 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerConsumeRawMeat(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (RAW_MEATS.contains(item.getType())) {
            // 80% 概率反胃 20秒
            if (random.nextDouble() < 0.8) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 0, false, false));
            }
            // 67% 概率饥饿 5秒 等级40
            if (random.nextDouble() < 0.67) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 40, false, false));
            }
        }
    }

    // ==================== 食物中毒 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerConsumeAnyFood(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // 检查是否是食物
        if (item.getType().isEdible()) {
            // 10% 概率食物中毒（中毒+饥饿+反胃）
            if (random.nextDouble() < 0.1) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 20, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 600, 0, false, false));
            }
        }
    }

    // ==================== 腐肉加强 ====================

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerConsumeRottenFlesh(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() == Material.ROTTEN_FLESH) {
            // 延迟1tick清除原版饥饿效果，然后施加加强版
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.removePotionEffect(PotionEffectType.HUNGER);
                        // 67% 概率饥饿V 持续6秒
                        if (random.nextDouble() < 0.67) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 120, 4, false, false));
                        }
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    // ==================== 肉类变质（背包中随机腐烂为腐肉）====================

    private void startMeatSpoilTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // 每tick 1/6000 概率变质一个肉类
                    if (random.nextDouble() < (1.0 / 6000.0)) {
                        spoilMeatInInventory(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 1L); // 延迟5秒启动，每tick执行
    }

    private void spoilMeatInInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && ALL_MEATS.contains(contents[i].getType())) {
                // 将肉类替换为腐肉
                int amount = contents[i].getAmount();
                player.getInventory().setItem(i, new ItemStack(Material.ROTTEN_FLESH, amount));
                return; // 只变质一个槽位
            }
        }
    }
}
