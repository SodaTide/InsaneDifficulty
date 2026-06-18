package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class EntityDeathListener implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public EntityDeathListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        // 末影人死亡生成末影螨
        if (event.getEntity() instanceof Enderman && config.isEndermanSpawnEndermites()) {
            for (int i = 0; i < 4; i++) {
                event.getEntity().getWorld().spawnEntity(
                    event.getEntity().getLocation(), EntityType.ENDERMITE);
            }
        }

        // 唤魔者不死图腾66%掉落（数据包: 34%概率不掉落）
        if (event.getEntity() instanceof Evoker) {
            if (!RNG.chance(0.66)) {
                Iterator<ItemStack> iter = event.getDrops().iterator();
                while (iter.hasNext()) {
                    ItemStack item = iter.next();
                    if (item.getType() == Material.TOTEM_OF_UNDYING) {
                        iter.remove();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.isCancelled()) return;
        Item item = event.getEntity();
        // 烈焰棒物品不可破坏（不会被岩浆/火焰销毁）
        if (item.getItemStack().getType() == Material.BLAZE_ROD) {
            item.setInvulnerable(true);
        }
    }
}
