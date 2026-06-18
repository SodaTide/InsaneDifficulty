package com.bravesurvival.insanedifficulty.task;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeatherTask extends BukkitRunnable {

    private final InsaneDifficultyPlugin plugin;

    public WeatherTask(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        try {
            plugin.getWorldManager().tickWeather();
        } catch (Exception e) {
            plugin.getLogger().warning("天气任务处理错误: " + e.getMessage());
        }
    }
}
