package com.bravesurvival.insanedifficulty.manager;

import com.bravesurvival.insanedifficulty.InsaneDifficultyPlugin;
import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.util.EntityUtil;
import com.bravesurvival.insanedifficulty.util.RNG;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.BlastingRecipe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ItemManager {

    private final InsaneDifficultyPlugin plugin;
    private final ConfigManager config;
    private final Set<NamespacedKey> registeredRecipes = new HashSet<>();

    public ItemManager(InsaneDifficultyPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        registerRecipes();
    }

    public void tick() {
        tickFoodSpoil();
    }

    private void registerRecipes() {
        removeVanillaRecipes();
        registerPlanksRecipes();
        registerBlazePowderRecipe();
        registerCookingRecipes();
        registerGoldenRecipes();
        registerHayBlockRecipe();
    }

    private void removeVanillaRecipes() {
        // 移除原版木板配方（4木板→2木板）
        String[] plankNames = {"oak_planks", "spruce_planks", "birch_planks", "jungle_planks",
                               "dark_oak_planks", "acacia_planks", "crimson_planks", "warped_planks"};
        for (String name : plankNames) {
            plugin.getServer().removeRecipe(NamespacedKey.minecraft(name));
        }
        // 移除原版烈焰粉配方（2→1）
        plugin.getServer().removeRecipe(NamespacedKey.minecraft("blaze_powder"));
        // 移除原版金胡萝卜配方（金粒→金锭）
        plugin.getServer().removeRecipe(NamespacedKey.minecraft("golden_carrot"));
        // 移除原版闪烁西瓜配方（金粒→金锭）
        plugin.getServer().removeRecipe(NamespacedKey.minecraft("glistering_melon_slice"));
        // 移除原版干草块配方（9小麦→4小麦）
        plugin.getServer().removeRecipe(NamespacedKey.minecraft("hay_block"));
        // 移除原版小麦配方（干草块→9小麦→4小麦）
        plugin.getServer().removeRecipe(NamespacedKey.minecraft("wheat"));
    }

    private void registerPlanksRecipes() {
        Material[] logs = {Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG,
                          Material.DARK_OAK_LOG, Material.ACACIA_LOG, Material.CRIMSON_STEM, Material.WARPED_STEM,
                          Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG, Material.STRIPPED_BIRCH_LOG,
                          Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_ACACIA_LOG,
                          Material.STRIPPED_CRIMSON_STEM, Material.STRIPPED_WARPED_STEM};
        Material[] planks = {Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS, Material.JUNGLE_PLANKS,
                            Material.DARK_OAK_PLANKS, Material.ACACIA_PLANKS, Material.CRIMSON_PLANKS, Material.WARPED_PLANKS,
                            Material.OAK_PLANKS, Material.SPRUCE_PLANKS, Material.BIRCH_PLANKS, Material.JUNGLE_PLANKS,
                            Material.DARK_OAK_PLANKS, Material.ACACIA_PLANKS, Material.CRIMSON_PLANKS, Material.WARPED_PLANKS};

        for (int i = 0; i < logs.length; i++) {
            NamespacedKey key = new NamespacedKey(plugin, logs[i].name().toLowerCase() + "_to_2planks");
            ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(planks[i], 2));
            recipe.addIngredient(logs[i]);
            plugin.getServer().addRecipe(recipe);
            registeredRecipes.add(key);
        }
    }

    private void registerBlazePowderRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "blaze_powder_1");
        ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(Material.BLAZE_POWDER, 1));
        recipe.addIngredient(Material.BLAZE_ROD);
        plugin.getServer().addRecipe(recipe);
        registeredRecipes.add(key);
    }

    private void registerCookingRecipes() {
        // 矿石熔炼（普通熔炉时间翻倍，高炉正常）
        registerOreSmelting(Material.COAL_ORE, Material.COAL, 0.1f);
        registerOreSmelting(Material.IRON_ORE, Material.IRON_INGOT, 0.7f);
        registerOreSmelting(Material.GOLD_ORE, Material.GOLD_INGOT, 1.0f);
        registerOreSmelting(Material.DIAMOND_ORE, Material.DIAMOND, 1.0f);
        registerOreSmelting(Material.EMERALD_ORE, Material.EMERALD, 1.0f);
        registerOreSmelting(Material.LAPIS_ORE, Material.LAPIS_LAZULI, 0.2f);
        registerOreSmelting(Material.REDSTONE_ORE, Material.REDSTONE, 0.7f);
        registerOreSmelting(Material.NETHER_QUARTZ_ORE, Material.QUARTZ, 0.2f);
        registerOreSmelting(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP, 2.0f);

        // 食物烹饪（普通熔炉时间翻倍，烟熏炉正常）
        registerFoodCooking(Material.BEEF, Material.COOKED_BEEF);
        registerFoodCooking(Material.PORKCHOP, Material.COOKED_PORKCHOP);
        registerFoodCooking(Material.CHICKEN, Material.COOKED_CHICKEN);
        registerFoodCooking(Material.MUTTON, Material.COOKED_MUTTON);
        registerFoodCooking(Material.RABBIT, Material.COOKED_RABBIT);
        registerFoodCooking(Material.COD, Material.COOKED_COD);
        registerFoodCooking(Material.SALMON, Material.COOKED_SALMON);
        registerFoodCooking(Material.POTATO, Material.BAKED_POTATO);
        registerFoodCooking(Material.KELP, Material.DRIED_KELP);
    }

    private void registerOreSmelting(Material input, Material result, float exp) {
        // 普通熔炉（400 ticks = 20秒）
        NamespacedKey sk = new NamespacedKey(plugin, result.name().toLowerCase() + "_smelt");
        plugin.getServer().addRecipe(new FurnaceRecipe(sk, new ItemStack(result), input, exp, 400));
        registeredRecipes.add(sk);

        // 高炉（200 ticks = 10秒）
        NamespacedKey bk = new NamespacedKey(plugin, result.name().toLowerCase() + "_blast");
        plugin.getServer().addRecipe(new BlastingRecipe(bk, new ItemStack(result), input, exp, 200));
        registeredRecipes.add(bk);
    }

    private void registerFoodCooking(Material input, Material result) {
        // 普通熔炉（400 ticks = 20秒）
        NamespacedKey sk = new NamespacedKey(plugin, result.name().toLowerCase() + "_smelt_f");
        plugin.getServer().addRecipe(new FurnaceRecipe(sk, new ItemStack(result), input, 0.35f, 400));
        registeredRecipes.add(sk);

        // 烟熏炉（200 ticks = 10秒）
        NamespacedKey ok = new NamespacedKey(plugin, result.name().toLowerCase() + "_smoke");
        plugin.getServer().addRecipe(new SmokingRecipe(ok, new ItemStack(result), input, 0.35f, 200));
        registeredRecipes.add(ok);
    }

    private void registerGoldenRecipes() {
        // 金胡萝卜（需要金锭）
        NamespacedKey ck = new NamespacedKey(plugin, "golden_carrot_ingot");
        ShapedRecipe cr = new ShapedRecipe(ck, new ItemStack(Material.GOLDEN_CARROT));
        cr.shape("###", "#X#", "###");
        cr.setIngredient('#', Material.GOLD_INGOT);
        cr.setIngredient('X', Material.CARROT);
        plugin.getServer().addRecipe(cr);
        registeredRecipes.add(ck);

        // 闪烁西瓜（需要金锭）
        NamespacedKey mk = new NamespacedKey(plugin, "glistering_melon_ingot");
        ShapedRecipe mr = new ShapedRecipe(mk, new ItemStack(Material.GLISTERING_MELON_SLICE));
        mr.shape("###", "#X#", "###");
        mr.setIngredient('#', Material.GOLD_INGOT);
        mr.setIngredient('X', Material.MELON_SLICE);
        plugin.getServer().addRecipe(mr);
        registeredRecipes.add(mk);
    }

    private void registerHayBlockRecipe() {
        // 干草块 → 4小麦
        NamespacedKey key = new NamespacedKey(plugin, "wheat_from_hay");
        ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(Material.WHEAT, 4));
        recipe.addIngredient(Material.HAY_BLOCK);
        plugin.getServer().addRecipe(recipe);
        registeredRecipes.add(key);
    }

    private void tickFoodSpoil() {
        if (!config.isFoodSpoil()) return;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (RNG.chance(config.getFoodSpoilChance())) {
                spoilMeat(player);
            }
        }
    }

    private void spoilMeat(Player player) {
        Material[] meats = {Material.COOKED_BEEF, Material.COOKED_PORKCHOP, Material.COOKED_CHICKEN,
                           Material.COOKED_MUTTON, Material.COOKED_RABBIT, Material.COOKED_COD, Material.COOKED_SALMON,
                           Material.BEEF, Material.PORKCHOP, Material.CHICKEN, Material.MUTTON,
                           Material.RABBIT, Material.COD, Material.SALMON};

        for (Material meat : meats) {
            for (int slot = 0; slot < 36; slot++) {
                ItemStack item = player.getInventory().getItem(slot);
                if (item != null && item.getType() == meat) {
                    player.getInventory().setItem(slot, new ItemStack(Material.ROTTEN_FLESH, item.getAmount()));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                    return;
                }
            }
        }
    }

    public void cleanup() {
        // 移除注册的配方
        for (NamespacedKey key : registeredRecipes) {
            plugin.getServer().removeRecipe(key);
        }
        registeredRecipes.clear();
    }
}
