package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemListener implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public ItemListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (event.isCancelled()) return;

        // 盾牌耐久消耗增加
        if (event.getItem().getType() == Material.SHIELD) {
            int increase = plugin.getCombatManager().getShieldDamageIncrease();
            if (increase > 0) {
                event.setDamage(increase);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_FOX_SNIFF, 1.0f, 1.0f);
            }
        }
    }
}
