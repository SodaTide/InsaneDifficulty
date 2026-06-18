package com.bravesurvival.insanedifficulty.task;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BlazeTask extends BukkitRunnable {

    private final InsaneDifficultyPlugin plugin;

    public BlazeTask(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            plugin.getWorldManager().tickBlazes();
        } catch (Exception e) {
            plugin.getLogger().warning("烈焰人任务处理错误: " + e.getMessage());
        }
    }
}
