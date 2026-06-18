package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawnListener implements Listener {

    private final InsaneDifficultyPlugin plugin;

    public MobSpawnListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;
        plugin.getMobManager().onCreatureSpawn(event.getEntity());
    }
}
