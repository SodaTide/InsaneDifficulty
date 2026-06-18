package com.bravesurvival.insanedifficulty;

import com.bravesurvival.insanedifficulty.config.ConfigManager;
import com.bravesurvival.insanedifficulty.listener.*;
import com.bravesurvival.insanedifficulty.manager.*;
import com.bravesurvival.insanedifficulty.task.*;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class InsaneDifficultyPlugin extends JavaPlugin {

    private static InsaneDifficultyPlugin instance;
    private ConfigManager configManager;
    private MobManager mobManager;
    private PlayerManager playerManager;
    private CombatManager combatManager;
    private WorldManager worldManager;
    private ItemManager itemManager;
    private StructureManager structureManager;
    private VillagerManager villagerManager;
    private final List<BukkitTask> tasks = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        // 初始化配置
        configManager = new ConfigManager(this);
        configManager.load();

        // 设置游戏规则
        setupGameRules();

        // 初始化管理器
        mobManager = new MobManager(this);
        playerManager = new PlayerManager(this);
        combatManager = new CombatManager(this);
        worldManager = new WorldManager(this);
        itemManager = new ItemManager(this);
        structureManager = new StructureManager(this);
        villagerManager = new VillagerManager(this);

        // 注册事件监听器
        registerListeners();

        // 启动定时任务
        startTasks();

        // 注册命令
        registerCommands();

        getLogger().info("Insane Difficulty v2.0 已加载！");
    }

    @Override
    public void onDisable() {
        // 取消所有任务
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();

        // 清理管理器
        if (itemManager != null) itemManager.cleanup();
        if (villagerManager != null) villagerManager.cleanup();
        if (combatManager != null) combatManager.cleanup();

        getLogger().info("Insane Difficulty 已卸载。");
    }

    private void setupGameRules() {
        getServer().getWorlds().forEach(world -> {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, false);
            world.setGameRule(GameRule.UNIVERSAL_ANGER, true);
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
            world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            world.setGameRule(GameRule.MAX_COMMAND_CHAIN_LENGTH, 1000000);
        });
    }

    private void registerListeners() {
        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MobSpawnListener(this), this);
        pluginManager.registerEvents(new MobDamageListener(this), this);
        pluginManager.registerEvents(new PlayerListener(this), this);
        pluginManager.registerEvents(new BlockListener(this), this);
        pluginManager.registerEvents(new ItemListener(this), this);
        pluginManager.registerEvents(new ProjectileListener(this), this);
        pluginManager.registerEvents(new EntityDeathListener(this), this);
    }

    private void startTasks() {
        // 主tick任务（每tick执行）
        tasks.add(new MainTickTask(this).runTaskTimer(this, 1L, 1L));

        // 烈焰人任务（每4 ticks执行）
        tasks.add(new BlazeTask(this).runTaskTimer(this, 4L, 4L));

        // 桶漏水任务（每10 ticks执行）
        tasks.add(new BucketTask(this).runTaskTimer(this, 10L, 10L));

        // 天气任务（每10 ticks执行）
        tasks.add(new WeatherTask(this).runTaskTimer(this, 10L, 10L));
    }

    private void registerCommands() {
        var command = getCommand("insane");
        if (command != null) {
            command.setExecutor((sender, cmd, label, args) -> {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    configManager.reload();
                    sender.sendMessage("§a配置已重新加载！");
                    return true;
                }
                sender.sendMessage("§eInsane Difficulty v2.0");
                sender.sendMessage("§7使用 /insane reload 重新加载配置");
                return true;
            });
        }
    }

    public static InsaneDifficultyPlugin getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public MobManager getMobManager() { return mobManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public CombatManager getCombatManager() { return combatManager; }
    public WorldManager getWorldManager() { return worldManager; }
    public ItemManager getItemManager() { return itemManager; }
    public StructureManager getStructureManager() { return structureManager; }
    public VillagerManager getVillagerManager() { return villagerManager; }
}
