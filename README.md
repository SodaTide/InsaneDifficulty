# 猛男生存 (BraveSurvival)

一个通过Mixin修改Minecraft游戏机制的模组/插件，大幅增加游戏难度，让生存挑战更加刺激。

**支持双平台部署：Fabric客户端模组 + Paper服务端插件**

**基于 Insane Difficulty 数据包改编**

## 🎮 功能特性

### 怪物强化

| 怪物 | 特性 | 状态 |
|------|------|------|
| 僵尸 | 追踪范围64，攻击伤害5，火焰抗性，不燃烧，50%概率生成随机装备 | ✅ |
| 溺尸 | 所有溺尸都有三叉戟 | ✅ |
| 尸壳 | 攻击给予饥饿效果 | ✅ |
| 骷髅 | 攻击伤害10，不燃烧，下界合金头盔 | ✅ |
| 流浪者 | 攻击给予缓慢效果 | ✅ |
| 苦力怕 | 高压状态，爆炸半径10，瞬间爆炸，隐形，速度0.3 | ✅ |
| 蜘蛛 | 隐形，速度0.4，命中附带中毒 | ✅ |
| 末影人 | 生命值40，攻击伤害7，速度0.3，追踪范围64，攻击后传送 | ✅ |
| 幻翼 | 不燃烧，伤害增加 | ✅ |
| 烈焰人 | 火焰抗性 | ✅ |
| 恶魂 | 隐形，三只一起生成 | ✅ |
| 铁傀儡 | 生命值200 | ✅ |
| 猪灵 | 除非穿全套金甲否则敌对 | ✅ |
| 守卫者 | 与鱼一起生成 | ✅ |
| 蜜蜂 | 更快中毒 | ✅ |
| 末影螨 | 挖末地石生成，末影人死亡生成 | ✅ |
| 蠹虫 | 挖石头生成 | ✅ |

### 游戏机制

| 特性 | 描述 | 状态 |
|------|------|------|
| 无自然再生 | 必须靠食物回血 | ✅ |
| 通用愤怒 | 怪物对所有玩家敌对 | ✅ |
| 摔落伤害 | 摔落后获得虚弱和缓慢 | ✅ |
| 受伤掉落物品 | 被打时30%概率掉落物品 | ✅ |
| 移动掉落物品 | 移动时0.1%概率掉落物品 | ✅ |
| 随机雷暴 | 随机发生高强度雷暴 | ✅ |
| 睡醒遇到幻翼 | 睡觉后醒来会遇到幻翼、饥饿、缓慢 | ✅ |
| 睡觉跳过部分夜晚 | 睡觉跳过1/3夜晚 | ✅ |
| 饥饿效果 | 饥饿时虚弱和挖掘疲劳 | ✅ |
| 重甲减速 | 穿铁/钻石/下界合金盔甲减速 | ✅ |
| 岩浆伤害 | 岩浆附近快速扣血 | ✅ |
| 仙人掌中毒 | 接触仙人掌附带中毒 | ✅ |
| 门在水中损坏 | 门在水中会损坏 | ✅ |
| 溺水效果 | 溺水时获得挖掘疲劳、虚弱、反胃、失明 | ✅ |
| 传送门损坏 | 传送时10%概率损坏传送门 | ✅ |
| 死亡生成僵尸 | 死亡时在死亡位置生成强大僵尸 | ✅ |
| 划船消耗饥饿 | 划船消耗饥饿值 | ✅ |

### 方块/物品

| 特性 | 描述 | 状态 |
|------|------|------|
| 挖石头爆蠹虫 | 挖石头有概率生成蠹虫 | ✅ |
| TNT破坏爆炸 | 破坏TNT有概率爆炸 | ✅ |
| 干草块 | 没有精准采集不掉落 | ✅ |
| 矿石掉落 | 主世界矿石不总是掉落 | ✅ |
| 黑曜石 | 挖掘时给予重度饥饿 | ✅ |
| 被虫蚀石头 | 踩上去召唤唤魔者尖牙 | ✅ |
| 盾牌 | 耐久度消耗速度x3 | ✅ |
| 烈焰棒/岩浆桶 | 会烧伤你 | ✅ |
| 腐肉 | 给予更多饥饿值 | ✅ |
| 生食 | 给予饥饿和反胃 | ✅ |
| 肉类变质 | 有1.66%概率变质 | ✅ |
| 食物中毒 | 有1.66%概率食物中毒 | ✅ |

### 战斗

| 特性 | 描述 | 状态 |
|------|------|------|
| 箭矢误射 | 10%概率改变方向 | ✅ |
| 末影珍珠 | 总是生成末影螨 | ✅ |
| 末地水晶 | 反射弹射物 | ✅ |
| 图腾削弱 | 移除再生效果 | ✅ |
| 不祥之兆 | 进入村庄时有概率获得 | ✅ |
| 药水副作用 | 20%概率给予负面效果 | ✅ |
| 末地爆炸辐射 | 末地爆炸对玩家造成中毒和凋灵 | ✅ |

### 配方修改

| 特性 | 描述 | 状态 |
|------|------|------|
| 木板配方 | 1个木头 → 2个木板 | ✅ |
| 烈焰粉配方 | 1个烈焰棒 → 1个烈焰粉 | ✅ |
| 金萝卜配方 | 需要金锭而非金粒 | ✅ |
| 闪烁西瓜配方 | 需要金锭而非金粒 | ✅ |

### 村民交易

| 特性 | 描述 | 状态 |
|------|------|------|
| 交易更贵 | 村民交易价格翻倍 | ✅ |
| 削弱交易 | 减少交易结果数量 | ✅ |
| 流浪商人替换 | 流浪商人替换为掠夺者小队 | ✅ |

### 实体行为

| 特性 | 描述 | 状态 |
|------|------|------|
| 鸡变成骷髅骑士 | 鸡在玩家靠近时变成骷髅骑士 | ✅ |
| 猪变成疣猪兽 | 猪在玩家靠近时变成疣猪兽 | ✅ |
| 海豚变成鳕鱼 | 海豚在玩家靠近时变成鳕鱼 | ✅ |
| 马踢下玩家 | 马有概率随机踢下玩家 | ✅ |
| 末影人破坏方块 | 末影人破坏头部附近方块 | ✅ |
| 凋灵骷髅破坏方块 | 凋灵骷髅破坏骷髅附近方块 | ✅ |
| 着火火焰轨迹 | 着火时留下火焰轨迹并变得虚弱 | ✅ |
| 桶漏水 | 桶有10%概率漏水 | ✅ |
| 下界炼药锅干涸 | 下界炼药锅会干涸 | ✅ |
| 恶魂火球更大爆炸 | 恶魂火球爆炸范围翻倍 | ✅ |
| 岩浆气泡流更强大 | 岩浆块伤害增加 | ✅ |

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
│           ├── BraveSurvivalPlugin.java # Paper入口
│           ├── entity/                  # Paper实体实现
│           └── listeners/               # 事件监听器
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

配置文件位于 `config/bravesurvival/config.json`，支持以下配置：

```json
{
  "mobs": {
    "zombie": {
      "enabled": true,
      "health": 20.0,
      "damage": 5.0,
      "speed": 0.23,
      "follow_range": 64.0,
      "burn_in_daylight": false,
      "fire_resistance": true,
      "enhanced_armor": true,
      "drowned_always_trident": true,
      "husk_hunger": true
    },
    "creeper": {
      "enabled": true,
      "always_charged": true,
      "instant_fuse": true,
      "invisible": true,
      "explosion_radius": 10,
      "speed": 0.3
    }
  },
  "player": {
    "fall_damage_debuff": true,
    "weakness_duration": 10,
    "slowness_duration": 10,
    "no_natural_regeneration": true,
    "hunger_effects": true,
    "heavy_armor_slowness": true,
    "drop_items_on_hit": true,
    "drop_items_on_move": true,
    "death_spawn_zombie": true
  },
  "world": {
    "universal_anger": true,
    "random_lightning_storms": true,
    "lava_heat_damage": true,
    "cactus_poison": true,
    "doors_break_in_water": true,
    "drowning_effects": true,
    "sleep_skips_third_of_night": true,
    "wake_up_to_phantoms": true,
    "portal_break_chance": true
  },
  "blocks": {
    "silverfish_chance": 0.0625,
    "ore_drop_chance": 0.5,
    "obsidian_heavy_hunger": true,
    "infested_stone_spawns_evoker_fangs": true,
    "hay_block_no_drop_without_silk_touch": true
  },
  "items": {
    "shield_durability_multiplier": 3.0,
    "blaze_rod_burns": true,
    "lava_bucket_burns": true,
    "rotten_flesh_more_hunger": true,
    "raw_food_hunger_and_nausea": true,
    "meat_spoil_chance": 0.0166,
    "food_poisoning_chance": 0.0166
  },
  "combat": {
    "arrows_misfire_chance": 0.1,
    "ender_pearls_always_spawn_endermites": true,
    "ender_crystals_reflect_projectiles": true,
    "totem_nerfed_drop": true,
    "bad_omen_on_village_enter": true,
    "potion_side_effects": true,
    "explosion_radiation_in_end": true
  },
  "boat": {
    "sink_after_ticks": 600,
    "hunger_from_rowing": true
  }
}
```

## 📝 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/bravesurvival` | `bravesurvival.command` | 显示插件信息 |
| `/bravesurvival reload` | `bravesurvival.command` | 重新加载配置 |

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
- 使用JSON配置文件
- 添加命令支持
- 实现全部特性
- 优化性能
