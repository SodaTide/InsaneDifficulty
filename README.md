# Insane Difficulty - Paper Plugin

基于 [Insane Difficulty 1.3 数据包](https://github.com/RoarkCats/Insane-Difficulty) 的 Paper 插件 1:1 复刻版。

将原版数据包的全部 95 项特性从 mcfunction/JSON 迁移为 Java 插件代码，可在 Paper 1.21.4 服务端直接运行，无需数据包。

## 特性总览

### 怪物强化（17种）

| 怪物 | 特性 |
|------|------|
| 僵尸 | 追踪64, 伤害5, 不燃烧, 火焰抗性, 增援0.15, 50%随机装备 |
| 溺尸 | 所有溺尸持三叉戟 |
| 尸壳 | 攻击给予饥饿 |
| 骷髅 | 力量V弓, 冲击II弓, 下界合金头盔 |
| 流浪者 | 攻击给予缓慢V |
| 苦力怕 | 充能, 引信1tick, 爆炸半径10, 速度0.3, 隐形 |
| 蜘蛛 | 隐形, 速度0.5, 中毒咬击 |
| 末影人 | 攻击后传送, 死亡生成4只末影螨, 破坏头部方块 |
| 幻翼 | 火焰抗性, 伤害3 |
| 恶魂 | 隐形, 三只生成, 爆炸威力6 |
| 猪灵 | 永久敌对（除非全套金甲） |
| 铁傀儡 | HP150, 主动攻击玩家, 跳跃爆炸 |
| 幻术师 | 力量V弓 |
| 女巫 | 1.5%概率发射凋灵骷髅头, 受伤时喷溅药水 |
| 掠夺者 | 快速装填V+多重射击, 成对生成, 劫掠兽伴随 |
| 烈焰人 | 无重力, 高速, 火焰轨迹 |
| 凋灵骷髅 | HP30, 伤害5, 凋灵效果, 破坏头部方块, 20%概率生成女巫 |

### 被动生物变形（6种）

| 变形 | 条件 |
|------|------|
| 牛/羊 → 兔子 | 8格内有玩家 |
| 海豚 → 鳕鱼 | 10格内有玩家 |
| 猪 → 疣猪兽 | 8格内有玩家 |
| 鸡 → 僵尸鸡骑士 | 16格内有玩家 |
| 鱼 → 守卫者 | 33%概率 |
| 末影人死亡 → 末影螨 | 4只 |

### 中立生物敌对化

狼、蜜蜂、僵尸猪灵（32格）、猪灵（除非全套金甲）、铁傀儡、北极熊、末影人 均在一定范围内主动攻击玩家。

### 玩家机制（15项）

- 无自然生命恢复
- 渐进式摔落伤害（4级debuff: 缓慢/虚弱/失明/反胃/夜视）
- 渐进式溺水（5级: 挖掘疲劳/虚弱/反胃/失明/下沉）
- 饥饿三级阈值（≤10虚弱 / ≤7+疲劳 / ≤5+饥饿80）
- 重甲减速（铁/金/钻石/下界合金）
- 受伤掉落物品（4次50%概率）
- 移动掉落物品（0.083%概率）
- 死亡生成墓碑僵尸（50%概率）
- 睡觉跳过1/3夜晚 + 醒来生成幻翼+饥饿127+缓慢
- 挖掘疲劳（不使用工具时）
- 着火火焰轨迹+虚弱
- 马匹随机踢人（0.166%概率）
- 气泡柱水流增强
- 黑曜石/哭泣黑曜石挖掘给予饥饿255
- 船划行消耗饥饿

### 食物系统（4项）

- 生肉惩罚：80%反胃20秒 + 67%饥饿5秒
- 肉类变质：背包中肉类随机变为腐肉
- 腐肉加强：67%概率饥饿40级6秒
- 食物中毒：10%概率中毒+饥饿+反胃

### 战斗机制（10项）

- 箭矢误射：20%概率偏转
- 怪物箭矢自动追踪（32格内）
- 末影珍珠落地生成末影螨
- 末影水晶反射投射物
- 图腾削弱（再生效果降低）
- 进入村庄33%概率获得不祥之兆
- 药水50%概率有随机副作用
- 末地爆炸辐射（毒+虚弱+反胃10格范围）
- 盾牌格挡箭矢偏转
- 女巫受伤喷溅药水（health_boost + hero_of_the_village）

### 结构守卫（5种）

| 结构 | 守卫 |
|------|------|
| 沙漠神殿 | 12只尸壳 + 宝箱TNT陷阱 |
| 沉船 | 8只溺尸 |
| 埋藏宝藏 | 4只恼鬼 |
| 废弃传送门 | 4只幻术师 |
| 丛林神庙 | 4只幻术师 |

### 环境机制

- 岩浆热浪：附近1格放置火焰
- 仙人掌中毒
- 门在水中损坏
- 传送门50%概率破坏
- 下界炼药锅蒸发
- 低亮度随机音效（16种）
- 随机闪电风暴（集束/群组/精准/递归）
- 桶漏水（10%概率）
- 烈焰棒/粉/岩浆桶持有燃烧
- 烈焰棒物品不可破坏

### 配方修改

- 原木 → 2木板（原版4）
- 烈焰棒 → 1烈焰粉（原版2）
- 金胡萝卜/闪烁西瓜需金锭（原版金粒）
- 干草块 → 4小麦（原版9）
- 干草块不掉落（需精准采集）
- 矿石/食物普通熔炉时间翻倍，高炉/烟熏炉正常

### 其他特性

- 流浪商人替换为劫掠兽+掠夺者
- 村民交易更贵（负面声誉）
- 猪灵以物易物削弱
- 唤魔者不死图腾66%掉落
- 末影之眼81次使用后击杀玩家
- TNT破坏20%概率爆炸
- 石头/矿石概率不掉落
- 末影龙持续回血 + 火球范围增大
- 末影人攻击后传送

## 构建

### 环境要求

- JDK 21（推荐 Zulu JDK 21）
- 网络连接（首次构建需下载依赖）

### 构建命令

```powershell
# 设置 JAVA_HOME（如系统有多个 Java 版本）
$env:JAVA_HOME = "C:\Program Files\Zulu\zulu-21"

# 构建
.\gradlew build --no-daemon

# 输出文件
# paper/build/libs/InsaneDifficulty-Paper-2.0.0.jar
```

### 安装

将 `InsaneDifficulty-Paper-2.0.0.jar` 放入服务器 `plugins` 文件夹，重启服务器即可。

## 配置

配置文件自动生成于 `plugins/InsaneDifficulty/config.yml`，所有特性均可通过配置开关或调整参数。

重载配置：`/insane reload`

## 项目结构

```
paper/src/main/java/com/bravesurvival/insanedifficulty/
├── InsaneDifficultyPlugin.java    # 插件入口
├── config/
│   └── ConfigManager.java         # 配置管理
├── listener/
│   ├── MobSpawnListener.java      # 生物生成事件
│   ├── MobDamageListener.java     # 生物伤害事件 + 猪灵以物易物
│   ├── PlayerListener.java        # 玩家事件（摔伤/食物/传送/睡觉）
│   ├── BlockListener.java         # 方块破坏事件
│   ├── ItemListener.java          # 物品耐久事件
│   ├── ProjectileListener.java    # 投射物事件
│   └── EntityDeathListener.java   # 实体死亡事件
├── manager/
│   ├── MobManager.java            # 生物行为管理
│   ├── PlayerManager.java         # 玩家机制管理
│   ├── CombatManager.java         # 战斗机制管理
│   ├── WorldManager.java          # 世界/环境管理
│   ├── ItemManager.java           # 物品/配方管理
│   ├── StructureManager.java      # 结构守卫管理
│   └── VillagerManager.java       # 村民交易管理
├── task/
│   ├── MainTickTask.java          # 主循环任务
│   ├── BlazeTask.java             # 烈焰人任务（4tick）
│   ├── BucketTask.java            # 桶漏水任务（10tick）
│   └── WeatherTask.java           # 天气任务（10tick）
└── util/
    ├── EntityUtil.java            # 实体工具
    ├── BlockUtil.java             # 方块工具
    └── RNG.java                   # 随机数工具
```

## 技术栈

- **Paper API**: 1.21.4-R0.1-SNAPSHOT
- **Java**: 21
- **Gradle**: 8.14

## 许可证

GNU GENERAL PUBLIC LICENSE

## 致谢

- [RoarkCats](https://github.com/RoarkCats) - 原版 Insane Difficulty 数据包作者
- [MCMocoder](https://github.com/MCMocoder) - 原始 BraveSurvival 项目
