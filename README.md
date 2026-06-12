# Douyin Experience App

一个参考抖音经验栏实现的 Android 双列图文流 Demo。项目包含多栏目瀑布流、真实远端数据、图片缓存加载、详情页预览、共享元素转场，以及围绕 OkHttp / Glide / RecyclerView 的性能优化。

## 功能特性

- `TabLayout + ViewPager2 + Fragment` 实现“推荐 / 经验 / 同城”三栏结构
- `RecyclerView + StaggeredGridLayoutManager` 实现双列瀑布流
- `SwipeRefreshLayout` 支持下拉刷新
- OkHttp 请求 `picsum.photos` 真实 JSON 数据源
- Glide 加载列表缩略图与详情页高清图
- 点击卡片进入详情页，支持共享元素转场
- 详情页点赞状态切换
- 启动遮罩与首屏数据加载并行，降低启动等待感

## 技术栈

- Kotlin
- AndroidX / AppCompat
- RecyclerView / CardView
- ViewPager2 / Fragment
- Material TabLayout
- SwipeRefreshLayout
- OkHttp 4.12.0
- Glide 4.16.0
- Glide OkHttp3 Integration

## 核心设计

### 页面架构

```text
MainActivity
  ├─ TabLayout
  ├─ ViewPager2
  │   ├─ FeedFragment("推荐")
  │   ├─ FeedFragment("经验")
  │   └─ FeedFragment("同城")
  └─ splashOverlay

FeedFragment
  └─ SwipeRefreshLayout
      └─ RecyclerView
          └─ FeedAdapter

DetailActivity
  ├─ 缩略图转场占位
  └─ 高清图延后加载
```

### 图片加载优化

项目将图片 URL 拆分为列表缩略图和详情高清图：

- 列表图：`thumbnailUrl`，使用 `Priority.NORMAL + DiskCacheStrategy.RESOURCE`
- 详情图：`detailUrl`，转场完成后使用 `Priority.IMMEDIATE + DiskCacheStrategy.DATA`

详情页采用两阶段加载：

1. 转场期只尝试从缓存读取列表缩略图，最多等待 120ms。
2. 转场放行后再加载详情高清图，避免远端网络阻塞动画。

### OkHttp 优化

项目使用全局 `NetworkManager.sharedClient`，并配置：

- `Dispatcher(maxRequests = 12, maxRequestsPerHost = 6)`
- `ConnectionPool(5, 5 min)`
- JSON 响应使用 `response.use {}` 确保连接归还连接池
- Glide 通过 `AppGlideModule` 接入同一套 OkHttp 网络底座

## 性能调优记录

项目通过日志做过跳转链路分析：

- 普通跳转首帧约 `63-89ms`
- 共享元素初版在远端图未命中缓存时出现过 `4-6s` 的 REMOTE 等待
- 修复 ResponseBody 关闭后，OkHttp connection leaked 计数降为 0

最终策略：

- 缩略图已加载：启用共享元素转场
- 缩略图未加载：普通跳转降级
- 列表图保持 `Priority.NORMAL`
- 详情图使用 `Priority.IMMEDIATE`

## 运行方式

1. 使用 Android Studio 打开项目。
2. 确认本地 JDK / Android SDK 配置正常。
3. 运行：

```powershell
.\gradlew.bat :app:compileDebugKotlin
```

4. 在模拟器或真机中运行 `app`。

## 项目材料

- `技术方案设计文档.md`：完整技术方案与性能优化说明
- `工作汇报.md`：每周工作进展及汇报
- `效果演示.mkv`：项目演示录屏

## 说明

本项目为训练营工程实践项目，重点展示 Android 图文流链路、网络请求、图片缓存、转场体验和性能调优思路。
