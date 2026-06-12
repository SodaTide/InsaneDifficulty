package xyz.mocoder.bravesurvival.core.logic.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import xyz.mocoder.bravesurvival.core.config.ConfigManager;
import xyz.mocoder.bravesurvival.core.entity.EntityWrapper;
import xyz.mocoder.bravesurvival.core.utils.FunctionalRandom;

import java.util.List;
import java.util.Random;

/**
 * 世界逻辑类
 * 处理世界相关的游戏逻辑
 */
public class WorldLogic {
    
    /**
     * 处理雷电生成
     * 在玩家附近正态分布生成雷电
     */
    public static void handleThunderGeneration(List<EntityWrapper> players, Random random, double[] distances) {
        if (!ConfigManager.isThunderDensityEnabled()) {
            return;
        }
        
        if (players.isEmpty()) {
            return;
        }
        
        // 随机选择一个玩家
        EntityWrapper randomPlayer = players.get(random.nextInt(players.size()));
        
        // 在不同距离生成雷电
        for (double dist : distances) {
            // 生成随机位置
            double[] position = FunctionalRandom.generateNearbyPosition(
                random, randomPlayer.getX(), randomPlayer.getZ(), dist
            );
            
            // 生成雷电
            // 具体实现在平台适配器中
        }
    }
    
    /**
     * 检查是否应该生成骷髅马
     */
    public static boolean shouldSpawnSkeletonHorse(Random random, double localDifficulty) {
        double chance = ConfigManager.getSkeletonHorseChance();
        return random.nextDouble() < localDifficulty * chance;
    }
    
    /**
     * 获取生成倍数
     */
    public static int getSpawnMultiplier() {
        return ConfigManager.getSpawnMultiplier();
    }
    
    /**
     * 检查方块是否会产生蠹虫
     */
    public static boolean shouldSpawnSilverfish(String blockId) {
        if (!ConfigManager.getBlocksConfig().has("silverfish_chance")) {
            return false;
        }
        
        // 检查方块是否在列表中
        if (ConfigManager.getBlocksConfig().has("silverfish_blocks")) {
            com.google.gson.JsonArray silverfishBlocks = ConfigManager.getBlocksConfig().getAsJsonArray("silverfish_blocks");
            String blockIdLower = blockId.toLowerCase();
            for (com.google.gson.JsonElement element : silverfishBlocks) {
                if (element.getAsString().toLowerCase().equals(blockIdLower)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 获取蠹虫生成概率
     */
    public static double getSilverfishChance() {
        return ConfigManager.getBlocksConfig().has("silverfish_chance") ? 
               ConfigManager.getBlocksConfig().get("silverfish_chance").getAsDouble() : 0.0625;
    }
    
    /**
     * 获取船下沉时间（ticks）
     */
    public static int getBoatSinkTicks() {
        return ConfigManager.getBoatConfig().has("sink_after_ticks") ? 
               ConfigManager.getBoatConfig().get("sink_after_ticks").getAsInt() : 600;
    }
    
    /**
     * 获取船下沉速度倍数
     */
    public static double getBoatSinkSpeedMultiplier() {
        return ConfigManager.getBoatConfig().has("sink_speed_multiplier") ? 
               ConfigManager.getBoatConfig().get("sink_speed_multiplier").getAsDouble() : 0.05;
    }
    
    /**
     * 获取每个原木产出的木板数量
     */
    public static int getPlanksPerLog() {
        return ConfigManager.getRecipesConfig().has("planks_per_log") ? 
               ConfigManager.getRecipesConfig().get("planks_per_log").getAsInt() : 2;
    }
}
