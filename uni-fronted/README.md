# 零工记账小程序 - uni-fronted前端项目

这是一个基于uni-app的微信小程序前端项目，为打零工人员提供便捷的记账功能。

## 项目结构

```
uni-fronted/
├── api/                    # API 服务层
│   ├── user.js            # 用户相关接口
│   ├── income.js          # 收入相关接口
│   └── expense.js         # 支出相关接口
├── pages/                  # 页面文件
│   ├── login/             # 登录页面
│   │   └── index.vue
│   ├── income/            # 收入管理页面
│   │   ├── index.vue      # 收入列表
│   │   └── add.vue        # 新增收入
│   ├── expense/           # 支出管理页面
│   │   ├── index.vue      # 支出列表
│   │   └── add.vue        # 新增支出
│   ├── statistics/        # 数据统计页面
│   │   └── index.vue
│   └── mine/              # 个人中心页面
│       └── index.vue
├── utils/                  # 工具函数
│   ├── request.js         # HTTP请求
│   ├── dateUtil.js        # 日期处理
│   └── format.js          # 格式化函数
├── static/                # 静态资源
├── App.vue                # 应用入口
├── main.js                # 应用启动文件
├── pages.json             # 路由配置
└── manifest.json          # 小程序配置
```

## 核心功能

### 1. 用户认证 (login/index.vue)
- 微信一键登录
- Token自动存储
- 登录状态检查

### 2. 收入管理 (income/*)
- **收入列表**: 查看所有收入记录
- **新增收入**: 支持三种记账方式
  - 记时: 按工作小时数计算
  - 记件: 按数量计算
  - 记平方: 按面积计算

### 3. 支出管理 (expense/*)
- **支出列表**: 查看所有支出记录
- **新增支出**: 灵活的支出原因和金额记录

### 4. 数据统计 (statistics/index.vue)
- 多维度统计分析
- 支持按天/周/月统计
- 收入类型分析
- 支出原因分析
- 日均统计对比

### 5. 个人中心 (mine/index.vue)
- 用户信息展示
- 快速数据预览
- 功能导航
- 登出功能

## API 配置

### 请求配置 (utils/request.js)

```javascript
// 后端API地址
const BASE_URL = 'http://localhost:8080/api'
```

**需要修改为实际的后端地址**

### 请求头

所有需要认证的API请求会自动添加Token:
```
Authorization: Bearer <token>
```

### 错误处理

- 401错误: 自动跳转到登录页
- 其他错误: 显示错误提示

## 工具函数

### 日期工具 (utils/dateUtil.js)
- `formatDate()` - 格式化日期
- `getFirstDayOfMonth()` - 获取月份第一天
- `getLastDayOfMonth()` - 获取月份最后一天
- `getToday()` - 获取今天日期
- `isSameDay()` - 判断是否为同一天
- `getDaysDiff()` - 获取日期间隔天数

### 格式化工具 (utils/format.js)
- `formatCurrency()` - 格式化货币
- `incomeTypeToText()` - 收入类型转文本
- `getIncomeTypeColor()` - 获取收入类型颜色
- `getIncomeUnit()` - 获取收入单位
- `truncateText()` - 截断文本
- `formatPercent()` - 格式化百分比

## 开发指南

### 环境要求
- Node.js 14+
- HBuilderX 或其他uni-app开发工具

### 安装依赖
```bash
# 如果有package.json，运行以下命令
npm install
```

### 开发模式
```bash
# 在HBuilderX中运行项目或使用CLI工具
```

### 编译发布
```bash
# 编译为微信小程序
# 在HBuilderX中选择发行 > 小程序 > 微信小程序
```

## 关键配置

### 后端API地址
编辑 `utils/request.js` 中的 `BASE_URL`:
```javascript
const BASE_URL = 'http://your-server:8080/api'
```

### 小程序配置
编辑 `manifest.json`:
- AppID: 微信小程序的AppID
- 应用名称
- 版本号等

## 业务流程

### 登录流程
1. 用户点击微信登录
2. 调用 `uni.login()` 获取code
3. 发送code到后端，换取openid和token
4. 本地存储token和用户信息
5. 跳转到首页

### 记账流程
1. 选择收入/支出类型
2. 填写相关信息
3. 提交到后端
4. 返回列表页面
5. 自动刷新数据

### 统计流程
1. 选择统计时间段（天/周/月）
2. 从后端获取统计数据
3. 计算百分比和日均数据
4. 展示数据分析图表

## 常见问题

### Q: 如何修改后端API地址?
A: 修改 `utils/request.js` 中的 `BASE_URL`

### Q: 如何处理Token过期?
A: 系统会自动检测401错误并跳转到登录页

### Q: 如何添加新页面?
A: 1. 在pages文件夹下创建对应页面
   2. 在pages.json中添加路由配置
   3. 编写页面代码

### Q: 如何修改样式?
A: 在各页面的`<style scoped>`中编辑样式

## 性能优化

1. **数据缓存**: 使用uni.setStorageSync存储用户信息
2. **分页加载**: 列表页使用分页加载更多
3. **请求合并**: 统计页同时请求多个API
4. **防止重复提交**: 提交按钮禁用状态管理

## 安全建议

1. 不存储敏感信息在localStorage中
2. 使用HTTPS加密传输
3. 定期更新依赖包
4. 验证所有用户输入
5. 实施CSRF防护

## 技术栈

- uni-app - 跨平台框架
- Vue.js - 前端框架
- uni-ui - UI组件库（可选）
- JavaScript ES6+ - 编程语言

## 后续开发

- [ ] 添加图表展示功能
- [ ] 实现数据导出功能
- [ ] 添加预算提醒功能
- [ ] 支持多语言
- [ ] 离线数据缓存
- [ ] 数据同步功能
- [ ] 云备份功能

## 许可证

MIT

## 联系方式

如有问题或建议，请联系项目维护者。

---

**最后更新**: 2024年
**作者**: 零工记账开发团队
