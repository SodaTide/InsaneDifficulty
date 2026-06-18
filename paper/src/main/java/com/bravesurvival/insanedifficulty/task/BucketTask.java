package com.bravesurvival.insanedifficulty.task;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BucketTask extends BukkitRunnable {

    private final InsaneDifficultyPlugin plugin;

    public BucketTask(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            plugin.getWorldManager().tickBuckets();
        } catch (Exception e) {
            plugin.getLogger().warning("桶漏水任务处理错误: " + e.getMessage());
        }
    }
}
