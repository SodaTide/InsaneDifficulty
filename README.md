# 猛男生存 (BraveSurvival)

一个通过Mixin修改Minecraft游戏机制的模组/插件，大幅增加游戏难度，让生存挑战更加刺激。

**支持双平台部署：Fabric客户端模组 + Paper服务端插件**

## 🎮 功能特性

### 怪物强化
- **僵尸**：提高护甲生成率（铁甲、钻石甲、下界合金甲），增加伤害、追踪范围、速度，白天不燃烧
- **骷髅**：提高护甲生成率，增加弓箭伤害，白天不燃烧
- **苦力怕**：所有苦力怕都是高压状态，瞬间爆炸，本体隐形
- **末影人**：死亡时生成末影螨，传送时破坏方块，大幅增强属性
- **猪灵**：增加伤害和射速
- **幻翼**：增加伤害，白天不燃烧
- **守卫者**：增加伤害、速度、生命值
- **疣猪兽**：增加伤害和生命值
- **铁傀儡**：会攻击最近的玩家

### 游戏机制修改
- **生成密度**：怪物生成密度提高8倍
- **雷电系统**：高密度雷电在玩家附近正态分布生成，提高骷髅马生成率
- **船沉没**：玩家乘坐600tick后船开始下沉
- **盾牌**：耐久度改为200
- **配方**：1个木头 → 2个木板（原版是4个）
- **挖矿**：挖石头有1/16概率爆出蠹虫

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
