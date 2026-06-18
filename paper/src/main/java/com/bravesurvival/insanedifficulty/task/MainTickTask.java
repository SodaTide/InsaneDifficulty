package com.bravesurvival.insanedifficulty.task;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MainTickTask extends BukkitRunnable {

    private final InsaneDifficultyPlugin plugin;
    private int tickCount = 0;

    public MainTickTask(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            tickCount++;

            // 每tick执行：玩家处理（饥饿、溺水、重型盔甲等）
            plugin.getPlayerManager().tick();
            // 每tick执行：世界处理（末影龙、气泡柱、门在水中）
            plugin.getWorldManager().tick();
            // 每tick执行：战斗处理（末影水晶反射、箭矢追踪、辐射）
            plugin.getCombatManager().tick();

            // 每20tick执行：生物处理（敌对生物、被动生物转化等）
            if (tickCount % 20 == 0) {
                plugin.getMobManager().tick();
            }

            // 每100tick执行：物品处理（食物腐烂）
            if (tickCount % 100 == 0) {
                plugin.getItemManager().tick();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Tick处理错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
