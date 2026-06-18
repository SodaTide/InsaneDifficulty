package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.destroystokyo.paper.entity.villager.Reputation;
import com.destroystokyo.paper.entity.villager.ReputationType;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;

public class VillagerManager implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public VillagerManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (!config.isVillagerTradesExpensive()) return;

        // 使用Paper API的Reputation系统来增加交易价格
        AbstractVillager entity = event.getEntity();
        if (entity instanceof Villager villager) {
            applyNegativeReputation(villager);
        }
    }

    @EventHandler
    public void onVillagerReplenishTrade(VillagerReplenishTradeEvent event) {
        if (!config.isVillagerTradesExpensive()) return;

        // 使用Paper API的Reputation系统来增加交易价格
        AbstractVillager entity = event.getEntity();
        if (entity instanceof Villager villager) {
            applyNegativeReputation(villager);
        }
    }

    private void applyNegativeReputation(Villager villager) {
        // 为所有在线玩家设置负面声誉
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Reputation reputation = villager.getReputation(player.getUniqueId());
            if (reputation == null) {
                reputation = new Reputation();
            }
            // 设置minor_negative声誉，使交易更贵
            reputation.setReputation(ReputationType.MINOR_NEGATIVE, 10000);
            villager.setReputation(player.getUniqueId(), reputation);
        }
    }

    public void cleanup() {
        // 清理资源
    }
}
