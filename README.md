# 猛男生存 (BraveSurvival)

一个通过Mixin修改Minecraft游戏机制的模组/插件，大幅增加游戏难度，让生存挑战更加刺激。

**支持双平台部署：Fabric客户端模组 + Paper服务端插件**

**基于 Insane Difficulty 数据包改编**

> ⚠️ **注意**：Fabric 模组版尚未完善全部特性，部分功能仅在 Paper 插件版中实现。如需完整体验，请使用 Paper 插件版。

## 🎮 功能特性

### 怪物强化（17种）

| 怪物 | 特性 | 状态 |
|------|------|------|
| 僵尸 | HP20, 伤害5, 速度0.23, 追踪64, 护甲3, 不烧, 火焰抗性, 50%随机装备, 增援0.15 | ✅ |
| 溺尸 | 所有溺尸持三叉戟 | ✅ |
| 尸壳 | 攻击给予饥饿II 10秒 | ✅ |
| 骷髅 | 伤害10, 不烧, 强化装备, 下界合金头盔, 力量弓 | ✅ |
| 流浪者 | 攻击给予迟缓II 10秒 | ✅ |
| 苦力怕 | 高压, 引信1秒, 半径10, 隐形, 速度0.3 | ✅ |
| 蜘蛛 | 隐形, 速度0.5, 中毒咬击 | ✅ |
| 末影人 | HP40, 伤害14, 速度0.75, 追踪64, 死亡生成末影螨, 破坏方块 | ✅ |
| 幻翼 | 不烧, 伤害9+体型 | ✅ |
| 恶魂 | 隐形, 三只生成, 爆炸威力6 | ✅ |
| 猪灵 | HP16, 伤害9, 速度0.3, 永久敌对(除非全套金甲) | ✅ |
| 铁傀儡 | HP200, 攻击玩家, 猛击爆炸 | ✅ |
| 幻术师 | 力量弓, 自动瞄准 | ✅ |
| 女巫 | 凋灵骷髅随从, 受伤喷药水 | ✅ |
| 掠夺者 | 快速装填V+多重射击, 劫掠兽伴随 | ✅ |
| 烈焰人 | 无重力, 速度0.4, 火焰轨迹, 火焰抗性 | ✅ |
| 凋灵骷髅 | HP30, 伤害5, 石剑+石镐, 凋灵效果, 破坏方块 | ✅ |

### 被动生物变形（6种）

| 变形 | 条件 | 状态 |
|------|------|------|
| 牛/羊→兔子 | 8格内有玩家 | ✅ |
| 海豚→鳕鱼 | 10格内有玩家 | ✅ |
| 猪→疣猪兽 | 8格内有玩家 | ✅ |
| 鸡→骷髅骑手 | 8格内有玩家 | ✅ |
| 鱼→守卫者 | 33%几率, 16格内有玩家 | ✅ |
| 兔子替换牛羊 | 自然生成时 | ✅ |

### 中立生物敌对化（4种）

| 生物 | 敌对范围 | 状态 |
|------|---------|------|
| 狼 | 16格 | ✅ |
| 蜜蜂 | 16格 | ✅ |
| 北极熊 | 16格 | ✅ |
| 僵尸猪灵 | 32格 | ✅ |

### 玩家机制（12项）

| 特性 | 描述 | 状态 |
|------|------|------|
| 无自然再生 | 必须靠食物回血 | ✅ |
| 渐进式摔伤 | 4级debuff(失明/反胃/夜视/虚弱/缓慢) | ✅ |
| 渐进式溺水 | 5级精确空气值判定 | ✅ |
| 饥饿效果 | ≤6饥饿时虚弱+挖掘疲劳 | ✅ |
| 重甲减速 | 铁/钻石/下界合金按数量分级 | ✅ |
| 受伤掉落物品 | 4次独立50%几率 | ✅ |
| 移动掉落物品 | 0.083%几率 | ✅ |
| 死亡生成僵尸 | 50%几率, 皮革头盔 | ✅ |
| 睡觉跳过1/3夜晚 | ✅ | ✅ |
| 睡醒遇到幻翼 | 50%每只, 最多4只, 饥饿+迟缓 | ✅ |
| 挖掘疲劳(无工具) | 不持有工具时疲劳I | ✅ |
| 着火火焰轨迹 | 着火留下火焰+虚弱 | ✅ |

### 环境机制（7项）

| 特性 | 描述 | 状态 |
|------|------|------|
| 岩浆热浪 | 岩浆旁1格放置火焰 | ✅ |
| 仙人掌中毒 | 中毒II 2秒 | ✅ |
| 门在水中损坏 | ✅ | ✅ |
| 传送门破坏 | 50%几率 | ✅ |
| 下界炼药锅干涸 | ✅ | ✅ |
| 低亮度随机音效 | 1.66%几率, 16种音效 | ✅ |
| 天气复杂模式 | 0.0033%触发, 集束/群组/精准闪电 | ✅ |

### 战斗机制（8项）

| 特性 | 描述 | 状态 |
|------|------|------|
| 箭矢误射 | 20%几率偏转-45°~+45° | ✅ |
| 末影珍珠生成末影螨 | 落地必定生成 | ✅ |
| 末地水晶反射弹射物 | 5格内反转速度 | ✅ |
| 图腾削弱 | 再生改为23秒0级+15秒1级 | ✅ |
| 进入村庄不祥之兆 | 33%几率 | ✅ |
| 药水副作用 | 50%几率随机负面效果 | ✅ |
| 末地爆炸辐射 | 毒+虚弱10格 | ✅ |
| 盾牌格挡箭偏转 | 箭向下偏转并标记 | ✅ |

### 结构修改（5项）

| 特性 | 描述 | 状态 |
|------|------|------|
| 沙漠神殿 | 12只尸壳从Y=255生成 | ✅ |
| 沉船 | 8只溺尸从Y=255生成 | ✅ |
| 废弃传送门 | 4只幻术师 | ✅ |
| 流浪商人→劫掠兽 | 劫掠兽+掠夺者+卫道士 | ✅ |
| 商人羊驼→掠夺者+卫道士 | ✅ | ✅ |

### 配方修改（5项）

| 特性 | 描述 | 状态 |
|------|------|------|
| 木板 | 1原木→2木板 | ✅ |
| 烈焰粉 | 1烈焰棒→1粉末 | ✅ |
| 金萝卜 | 需要金锭而非金粒 | ✅ |
| 闪烁西瓜 | 需要金锭而非金粒 | ✅ |
| 干草块→小麦 | 1干草块=4小麦 | ✅ |

### 其他特性（8项）

| 特性 | 描述 | 状态 |
|------|------|------|
| 烈焰棒/岩浆桶燃烧 | 持有物品燃烧 | ✅ |
| 桶漏水 | 10%几率 | ✅ |
| 村民交易更贵 | 材料x2, 产出/2 | ✅ |
| 猪灵以物易物重做 | 完整掉落表 | ✅ |
| 凋灵骷髅自定义属性 | HP30, 伤害5, 石剑+石镐 | ✅ |
| 高个生物破坏方块 | 凋灵骷髅+末影人 | ✅ |
| 黑曜石挖掘饥饿255 | ✅ | ✅ |
| 船划行饥饿IV | ✅ | ✅ |

### 插件独有特性（数据包没有）

| 特性 | 描述 | 配置项 |
|------|------|--------|
| 铁傀儡攻击玩家 | 16格内主动攻击最近玩家 | `plugin_unique.iron_golem_attack_players` |
| 铁傀儡跳跃爆炸 | 跳跃时产生爆炸 | `plugin_unique.iron_golem_jump_explosion` |
| 铁傀儡猛击攻击 | 1.5%几率浮空+TNT爆炸 | `plugin_unique.iron_golem_smash_attack` |
| 烈焰人无重力 | setGravity(false) | `plugin_unique.blaze_no_gravity` |
| 烈焰人速度0.4 | 比数据包1.0慢但合理 | `plugin_unique.blaze_speed` |
| 恶魂三只生成 | 同数据包 | `plugin_unique.ghast_triple_spawn` |
| 流浪商人→劫掠兽+掠夺者+卫道士 | 比数据包多卫道士 | `plugin_unique.trader_llama_to_pillager` |
| 腐肉恢复+4饥饿 | 吃腐肉额外恢复 | `plugin_unique.rotten_flesh_extra_hunger` |

## 📁 项目结构（多模块架构）

```
BraveSurvival/
├── core/                        # 核心逻辑模块（平台无关）
│   └── src/main/java/
│       └── xyz/mocoder/bravesurvival/core/
│           ├── config/          # 配置管理
│           ├── logic/           # 游戏逻辑
│           │   ├── mob/         # 怪物逻辑
│           │   ├── player/      # 玩家逻辑
│           │   ├── world/       # 世界逻辑
│           │   └── item/        # 物品逻辑
│           ├── entity/          # 实体抽象接口
│           ├── events/          # 事件适配器接口
│           └── utils/           # 工具类
├── fabric/                      # Fabric模组模块
│   └── src/main/java/
│       └── xyz/mocoder/bravesurvival/fabric/
│           ├── BraveSurvivalMod.java   # Fabric入口
│           ├── entity/                 # Fabric实体实现
│           ├── events/                 # Fabric事件适配
│           └── mixin/                  # Mixin注入点
├── paper/                       # Paper插件模块
│   └── src/main/java/
│       └── xyz/mocoder/bravesurvival/paper/
│           ├── BraveSurvivalPlugin.java      # Paper入口
│           ├── RecipeManager.java            # 配方管理
│           ├── VillagerTradeManager.java     # 村民交易管理
│           ├── EntityBehaviorManager.java    # 实体行为管理
│           ├── AdvancedFeatureManager.java   # 高级功能管理
│           ├── MissingFeaturesManager1.java  # 缺失功能1
│           ├── MissingFeaturesManager2.java  # 缺失功能2
│           └── entity/                      # Paper实体实现
└── README.md
```

## 🔧 技术栈

### 版本支持
| 平台 | 最低版本 | 推荐版本 |
|------|---------|---------|
| **Fabric** | 1.21.4 | 1.21.4+ |
| **Paper** | 1.21.1 | 1.21.4+ |

### 技术细节
- **Java版本**：Java 21
- **构建工具**：Gradle 8.14 + Kotlin DSL
- **Fabric Loader**：0.16.10+
- **Fabric API**：0.112.2+
- **Paper API**：1.21.4-R0.1-SNAPSHOT

## 🚀 构建说明

### 环境要求

- **Java 21**：必须安装JDK 21或更高版本
- **网络**：首次构建需要下载Minecraft资源（约500MB）

### 构建命令

```bash
# 设置JAVA_HOME（如果系统有多个Java版本）
# Windows PowerShell
$env:JAVA_HOME = "F:\Program Files\Zulu\zulu-21"

# Windows CMD
set JAVA_HOME=F:\Program Files\Zulu\zulu-21

# Linux/Mac
export JAVA_HOME=/path/to/java21

# 构建所有模块
./gradlew build

# 只构建Core模块
./gradlew :core:build

# 只构建Fabric模组
./gradlew :fabric:build

# 只构建Paper插件
./gradlew :paper:build

# 清理并重新构建
./gradlew clean build
```

### 构建产物位置

```
BraveSurvival/
├── core/build/libs/
│   └── BraveSurvival-Core-2.0.0.jar          # 核心模块
├── fabric/build/libs/
│   ├── BraveSurvival-2.0.0.jar                # Fabric模组（已重映射）
│   └── BraveSurvival-2.0.0-sources.jar        # 源代码
└── paper/build/libs/
    └── BraveSurvival-Paper-2.0.0.jar          # Paper插件
```

### 首次构建注意事项

1. **下载Minecraft资源**：首次构建Fabric模块时，Gradle会下载Minecraft资源文件，这可能需要几分钟
2. **网络问题**：如果下载失败，请检查网络连接或配置代理
3. **Java版本**：确保使用Java 21，其他版本可能导致编译错误

### 常见问题

#### Q: 构建失败，提示"Unsupported class file major version"
A: Java版本不匹配，请使用Java 21

#### Q: 构建失败，提示找不到符号
A: 可能是API变化，请检查错误信息并更新代码

#### Q: Fabric构建很慢
A: 首次构建需要下载Minecraft资源，请耐心等待

## 📦 安装使用

### Fabric客户端

1. 安装 [Fabric Loader](https://fabricmc.net/use) (>=0.16.10)
2. 下载 [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) (>=0.112.2)
3. 下载本模组的 `BraveSurvival-2.0.0.jar`
4. 将模组和API放入 `.minecraft/mods` 文件夹
5. 启动游戏，选择Fabric配置文件

### Paper服务端

1. 下载本插件的 `BraveSurvival-Paper-2.0.0.jar`
2. 将插件放入服务器的 `plugins` 文件夹
3. 启动服务器，插件会自动生成配置文件
4. 使用 `/bravesurvival reload` 重新加载配置

## ⚙️ 配置说明

配置文件位于 `config/bravesurvival/config.json`，支持100+可配置参数。

### 完整配置示例

```json
{
  "mobs": {
    "zombie": {
      "enabled": true,
      "health": 20.0,
      "damage": 5.0,
      "speed": 0.23,
      "follow_range": 64.0,
      "armor": 3.0,
      "burn_in_daylight": false,
      "fire_resistance": true,
      "enhanced_armor": true,
      "spawn_reinforcements": 0.15,
      "drowned_always_trident": true,
      "husk_hunger": true
    },
    "creeper": {
      "enabled": true,
      "always_charged": true,
      "instant_fuse": false,
      "fuse_ticks": 20,
      "invisible": true,
      "explosion_radius": 10,
      "speed": 0.3
    },
    "enderman": {
      "enabled": true,
      "health": 40.0,
      "damage": 14.0,
      "speed": 0.75,
      "follow_range": 64.0,
      "teleport_after_hit": true,
      "destroy_blocks": true,
      "spawn_endermites_on_death": true
    }
  },
  "player": {
    "fall_damage_debuff": true,
    "progressive_fall_damage": true,
    "no_natural_regeneration": true,
    "mining_fatigue_without_tools": true,
    "hunger_effects": true,
    "heavy_armor_slowness": true,
    "drop_items_on_hit": true,
    "drop_items_on_hit_chance": 0.5,
    "drop_items_on_hit_count": 4,
    "drop_items_on_move": true,
    "drop_items_on_move_chance": 0.00083,
    "death_spawn_zombie": true,
    "death_spawn_zombie_chance": 0.5
  },
  "world": {
    "spawn_multiplier": 8,
    "universal_anger": true,
    "reduced_debug_info": true,
    "random_lightning_storms": true,
    "lightning_storm_chance": 0.000033,
    "portal_break_chance": true,
    "portal_break_chance_value": 0.5
  },
  "combat": {
    "arrows_misfire_chance": 0.2,
    "bad_omen_chance": 0.33,
    "potion_side_effects_chance": 0.5,
    "ender_crystals_reflect_range": 5.0,
    "explosion_radiation_range": 10.0
  },
  "ender_dragon": {
    "enabled": true,
    "regen_chance_per_tick": 0.01,
    "regen_amount": 1.0,
    "breath_cloud_radius": 4.0,
    "breath_cloud_duration": 400
  },
  "plugin_unique": {
    "iron_golem_attack_players": true,
    "iron_golem_jump_explosion": true,
    "iron_golem_smash_attack": true,
    "blaze_no_gravity": true,
    "blaze_speed": 0.4,
    "ghast_triple_spawn": true,
    "trader_llama_to_pillager": true,
    "rotten_flesh_extra_hunger": true
  }
}
```

## 📝 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/bravesurvival` | `bravesurvival.command` | 显示插件信息 |
| `/bravesurvival reload` | `bravesurvival.command` | 重新加载配置 |

## 📊 特性统计

| 类别 | 数量 |
|------|------|
| 怪物强化 | 17种 |
| 被动生物变形 | 6种 |
| 中立生物敌对 | 4种 |
| 玩家机制 | 12项 |
| 环境机制 | 7项 |
| 战斗机制 | 8项 |
| 结构修改 | 5项 |
| 配方修改 | 5项 |
| 其他特性 | 8项 |
| **总计** | **97项** |
| 插件独有 | 8项 |

## 📄 许可证

GNU GENERAL PUBLIC LICENSE

## 👥 作者

- **程序**：MCMocoder
- **特别感谢**：Zi__Min

## 🔗 相关链接

- [GitHub仓库](https://github.com/EinsteinZheng/BraveSurvival)
- [Fabric官网](https://fabricmc.net/)
- [PaperMC官网](https://papermc.io/)

## 📋 更新日志

### v2.0.0
- 重构为多模块架构（core + fabric + paper）
- 支持Fabric 1.21.4+
- 支持Paper 1.21.1+
- 使用JSON配置文件（100+参数）
- 添加命令支持
- 实现数据包全部97项特性
- 添加8项插件独有特性
- 优化性能
