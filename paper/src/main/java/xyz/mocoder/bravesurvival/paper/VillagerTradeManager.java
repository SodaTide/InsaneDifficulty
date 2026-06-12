package xyz.mocoder.bravesurvival.paper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 村民交易管理器
 * 负责修改村民交易（材料x2、结果/2 + gossip敌意）
 */
public class VillagerTradeManager implements Listener {

    private final BraveSurvivalPlugin plugin;

    public VillagerTradeManager(BraveSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 监听村民获取交易事件 - 材料x10、结果/10（模拟gossip敌意）
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        if (event.isCancelled()) return;

        if (plugin.getConfigManager().getVillagerConfig().has("trades_more_expensive") &&
            plugin.getConfigManager().getVillagerConfig().get("trades_more_expensive").getAsBoolean()) {

            MerchantRecipe recipe = event.getRecipe();
            List<ItemStack> ingredients = recipe.getIngredients();

            // 增加原材料数量（x10，模拟gossip minor_negative=10000）
            for (int i = 0; i < ingredients.size(); i++) {
                ItemStack ingredient = ingredients.get(i);
                int newAmount = Math.min(64, ingredient.getAmount() * 10);
                ingredient.setAmount(newAmount);
            }

            // 减少结果数量（/10）
            ItemStack result = recipe.getResult();
            if (result.getAmount() > 1) {
                result.setAmount(Math.max(1, result.getAmount() / 10));
            }
        }
    }

    /**
     * 监听村民补充交易事件 - 削弱交易战利品
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onVillagerReplenishTrade(VillagerReplenishTradeEvent event) {
        if (event.isCancelled()) return;

        if (plugin.getConfigManager().getCombatConfig().has("nerfed_bartering_loot") &&
            plugin.getConfigManager().getCombatConfig().get("nerfed_bartering_loot").getAsBoolean()) {

            MerchantRecipe recipe = event.getRecipe();
            ItemStack result = recipe.getResult();

            if (result.getAmount() > 1) {
                result.setAmount(Math.max(1, result.getAmount() - 1));
            }
        }
    }
}
