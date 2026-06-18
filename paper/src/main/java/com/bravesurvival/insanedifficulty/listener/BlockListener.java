package com.bravesurvival.insanedifficulty.listener;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.EntityUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class BlockListener implements Listener {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;

    public BlockListener(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();

        // 石头掉落
        if (type == Material.STONE && config.isStoneDropWithPickaxe()) {
            ItemStack tool = player.getInventory().getItemInMainHand();
            double dropChance = 0.0;
            if (tool.getType() == Material.WOODEN_PICKAXE) dropChance = config.getStoneDropChanceWooden();
            else if (tool.getType() == Material.STONE_PICKAXE) dropChance = config.getStoneDropChanceStone();
            else if (EntityUtil.isGoodPickaxe(tool.getType())) dropChance = 1.0;

            if (dropChance > 0.0 && RNG.chance(1 - dropChance)) {
                event.setDropItems(false);
            }

            // 银鱼生成
            if (config.isSilverfishSpawnFromStone() && RNG.chance(config.getSilverfishSpawnChance())) {
                block.getWorld().spawnEntity(block.getLocation().add(0.5, 0, 0.5), EntityType.SILVERFISH);
            }
        }

        // 矿石掉落率
        if (EntityUtil.isOverworldOre(type) && config.isOreDropChance()) {
            if (RNG.chance(1 - config.getOreDropChanceValue())) {
                event.setDropItems(false);
            }
        }

        // 末地石掉落末影螨
        if (type == Material.END_STONE && config.isEndermiteSpawnFromEndStone()) {
            if (RNG.chance(config.getEndermiteSpawnChance())) {
                block.getWorld().spawnEntity(block.getLocation().add(0.5, 0, 0.5), EntityType.ENDERMITE);
            }
        }

        // TNT破坏爆炸
        if (type == Material.TNT && config.isTntBreakExplodes()) {
            if (RNG.chance(config.getTntBreakExplodesChance())) {
                block.getWorld().createExplosion(block.getLocation(), 4.0F, true);
                event.setCancelled(true);
                block.setType(Material.AIR);
            }
        }

        // 干草块需要丝触
        if (type == Material.HAY_BLOCK && config.isHayBlockSilkTouch()) {
            if (!player.getInventory().getItemInMainHand().getEnchantments().containsKey(
                    org.bukkit.enchantments.Enchantment.SILK_TOUCH)) {
                event.setDropItems(false);
            }
        }

        // 黑曜石/哭泣黑曜石给予饥饿
        if (type == Material.OBSIDIAN || type == Material.CRYING_OBSIDIAN) {
            EntityUtil.addEffect(player, PotionEffectType.HUNGER, 20, 255);
        }

        // 结构交互
        plugin.getStructureManager().onPlayerInteract(player, block);
    }
}
