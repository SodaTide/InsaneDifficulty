# 猛男生存 (BraveSurvival)

一个通过Mixin修改Minecraft游戏机制的模组/插件，大幅增加游戏难度，让生存挑战更加刺激。

**支持双平台部署：Fabric客户端模组 + Paper服务端插件**

**基于 Insane Difficulty 数据包改编**

## 🎮 功能特性

### 怪物强化
- **僵尸**：追踪范围64，攻击伤害5，增援概率15%，火焰抗性（不燃烧），50%概率生成装备，溺尸 always 三叉戟
- **骷髅**：自动瞄准，力量弓，下界合金头盔，白天不燃烧
- **苦力怕**：高压状态，爆炸半径10，瞬间爆炸，隐形，移动速度0.3
- **末影人**：攻击后传送，破坏方块
- **蜘蛛**：隐形，速度0.4，命中附带中毒
- **猪灵**：除非穿全套金甲否则敌对
- **幻翼**：白天不燃烧，伤害增加
- **烈焰人**：只飞行，速度增加，火焰轨迹，火球扩散火焰
- **恶魂**：三只一起生成，隐形，更大爆炸
- **卫道士**：快速装填5，多重射击，自动瞄准，两只一起生成，劫掠兽伴随
- **女巫**：凋灵骷髅，喷溅药水，有概率生成伴随凋灵骷髅
- **幻术师**：自动瞄准，力量弓
- **凋灵骷髅**：凋灵效果，破坏骷髅头附近方块
- **蠹虫**：击退抗性，速度0.4，挖石头生成
- **末影螨**：击退抗性，速度0.4，挖末地石生成，末影人死亡生成

### 游戏机制修改
- **无自然再生**：必须靠食物回血
- **通用愤怒**：怪物对所有玩家敌对
- **挖掘疲劳**：不拿工具时获得挖掘疲劳
- **饥饿效果**：饥饿时虚弱、疲劳，饿死更快
- **重甲减速**：穿大多数盔甲会减速
- **受伤掉落物品**：被打时有概率掉落物品
- **移动掉落物品**：移动时有概率掉落物品
- **船沉没**：玩家乘坐600tick后船开始下沉
- **船划动饥饿**：划船消耗饥饿值
- **岩浆伤害**：岩浆附近快速扣血
- **仙人掌中毒**：接触仙人掌附带中毒
- **门在水中损坏**：门在水中会损坏
- **溺水效果**：溺水时获得挖掘疲劳、虚弱、反胃、失明
- **睡觉跳过1/3夜晚**：而不是整晚
- **睡醒遇到幻翼**：睡觉后醒来会遇到幻翼、恼鬼、饥饿、缓慢
- **随机雷暴**：随机发生高强度雷暴
- **传送门损坏**：传送时有概率损坏传送门
- **黑暗中随机声音**：低光照等级时随机播放怪物/环境声音

### 方块修改
- **石头掉落概率**：用低品质镐挖石头有50%概率不掉落
- **矿石掉落概率**：矿石有50%概率不掉落
- **干草块**：没有精准采集不掉落，合成产出4个小麦
- **TNT**：破坏时有概率爆炸
- **黑曜石**：挖掘时给予重度饥饿
- **被虫蚀石头**：踩上去召唤唤魔者尖牙

### 物品修改
- **盾牌**：耐久度消耗速度x3
- **烈焰棒/粉**：会烧伤你
- **岩浆桶**：会烧伤你
- **腐肉**：给予更多饥饿值
- **生食**：给予饥饿和反胃
- **肉类变质**：有1.66%概率变质
- **食物中毒**：有1.66%概率食物中毒

### 配方修改
- **木板**：1个木头 → 2个木板
- **烈焰棒**：1个烈焰棒 → 1个烈焰粉
- **金萝卜**：需要金锭而非金粒
- **闪烁的西瓜片**：需要金锭而非金粒
- **矿石熔炼**：普通熔炉双倍时间，高炉正常时间

### 战斗修改
- **箭矢误射**：10%概率
- **末影珍珠**：总是生成末影螨
- **末地水晶**：反射弹射物
- **图腾削弱**：掉落概率和再生效果削弱
- **不祥之兆**：进入村庄时有概率获得

### 结构修改
- **沙漠神殿**：卫道士守卫
- **废弃传送门**：幻术师守卫
- **丛林神庙**：幻术师守卫
- **沉船**：溺尸守卫
- **埋藏的宝藏**：恼鬼守卫

### 玩家调整
- 摔落伤害后获得虚弱和缓慢debuff

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
      "damage": 8.0,
      "speed": 0.35,
      "follow_range": 55.0,
      "armor": 3.0,
      "burn_in_daylight": false,
      "enhanced_armor": true,
      "enhanced_enchantments": true
    },
    "creeper": {
      "enabled": true,
      "always_charged": true,
      "instant_fuse": true,
      "invisible": true
    }
  },
  "player": {
    "fall_damage_debuff": true,
    "weakness_duration": 10,
    "slowness_duration": 10
  },
  "world": {
    "spawn_multiplier": 8,
    "thunder_density": true,
    "skeleton_horse_chance": 0.01
  }
}
```

## 📝 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/bravesurvival` | `bravesurvival.command` | 显示插件信息 |
| `/bravesurvival reload` | `bravesurvival.command` | 重新加载配置 |

## 🔄 版本兼容性

| Minecraft版本 | Fabric端 | Paper端 |
|--------------|---------|---------|
| 1.21.1 | ❌ (低于1.21.4) | ✅ |
| 1.21.2 | ❌ (低于1.21.4) | ✅ |
| 1.21.3 | ❌ (低于1.21.4) | ✅ |
| 1.21.4 | ✅ | ✅ |
| 1.21.5+ | ✅ | ✅ |

## 🏗️ 架构设计

### 核心设计原则

1. **逻辑与平台分离**：核心游戏逻辑放在`core`模块，不依赖任何平台API
2. **适配器模式**：Fabric和Paper模块作为适配器，调用核心逻辑
3. **统一配置**：使用JSON配置文件，两个平台共享
4. **事件抽象**：定义统一的事件接口，各平台实现自己的事件监听

### 优势

1. **代码复用**：核心逻辑只写一次，两个平台共享
2. **独立部署**：可以单独构建Fabric模组或Paper插件
3. **易于维护**：修改核心逻辑时，两个平台自动同步
4. **灵活扩展**：未来可以轻松添加其他平台支持

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
- 优化性能
