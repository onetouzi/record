# 零工记账小程序 - 完整项目

## 📱 项目简介

这是一个为打零工人员量身定制的记账小程序，帮助工人轻松记录收入和支出，进行财务统计分析。

**核心功能**:
- ✅ 微信一键登录
- ✅ **首页日历视图** - 每日收支一目了然，点击查看详情
- ✅ 三种收入类型记录（按时计、按件计、按平方）
- ✅ **支持加班工时单独计价（仅记时类型）**
- ✅ 灵活的支出管理
- ✅ 多维度的财务统计（按天/周/月）
- ✅ 收入类型分布分析
- ✅ 收支对比分析

## 🏗️ 项目架构

```
零工记账小程序
├── 前端 (uni-fronted)
│   ├── uni-app + Vue 3
│   ├── 微信小程序
│   └── 响应式设计
├── 后端 (backend)
│   ├── Spring Boot 3
│   ├── MyBatis Plus
│   ├── JWT 认证
│   └── RESTful API
└── 数据库 (database)
    ├── MySQL 8.0+
    ├── 三个核心表
    └── 优化索引设计
```

## 📂 项目结构

```
record/
├── uni-fronted/              # 前端项目（uni-app）
│   ├── pages/               # 页面文件
│   │   ├── income/          # 收入相关页面
│   │   ├── expense/         # 支出相关页面
│   │   ├── statistics/      # 统计页面
│   │   └── mine/            # 我的页面
│   ├── api/                 # API 接口
│   ├── utils/               # 工具函数
│   └── pages.json           # 路由配置
├── backend/                  # 后端项目（Spring Boot）
│   ├── src/main/java/       # Java 源代码
│   │   └── com/workrecord/
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── service/     # 业务逻辑
│   │       ├── mapper/      # 数据访问层
│   │       └── controller/  # 控制器
│   └── pom.xml              # Maven配置
├── database/                 # 数据库脚本
│   └── init.sql             # 初始化脚本
├── record.md                 # 项目需求文档
└── PROJECT_GUIDE.md          # 项目指南（本文件）
```

## 🚀 快速开始

### 前置条件

- **后端**: JDK 21+, Maven 3.6+, MySQL 8.0+
- **前端**: Node.js 14+

### 后端启动

1. **初始化数据库**
   ```bash
   mysql -u root -p < database/init.sql
   ```

2. **配置应用** (`backend/src/main/resources/application.yml`)
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/work_record
       username: root
       password: root

   wechat:
     appid: your-wechat-appid
     secret: your-wechat-secret
   ```

3. **启动后端**
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```
   
   后端服务运行在 `http://localhost:8080`

### 前端开发

1. **安装依赖**
   ```bash
   cd uni-fronted
   npm install
   ```

2. **配置后端 API 地址** (`utils/request.js`)
   ```javascript
   const BASE_URL = 'http://localhost:8080/api'
   ```

3. **配置前端appID**(`record\uni-fronted\manifest.json`)
   ```json
   "appid" : " ",
    "mp-weixin" : {
        "appid" : "",
    }

   ```


4. **启动开发服务**
   ```bash
   npm run dev
   ```

## 📋 技术栈

### 前端
- **框架**: uni-app (Vue 3)
- **HTTP**: uni.request
- **日期**: dayjs
- **样式**: CSS3 + Flex/Grid

### 后端
- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis Plus 3.5.5
- **认证**: JWT (JJWT)
- **工具**: Hutool, Lombok

### 数据库
- **主库**: MySQL 8.0+
- **设计**: 第三范式
- **优化**: 联合索引设计

## 🔑 核心功能详解

### 1. 用户认证
- 微信 OAuth2 登录
- 自动注册新用户
- JWT 无状态认证
- Token 自动管理

### 2. 收入记录
**三种计算方式**:
- **记时**: 金额 = 正常工时 × 正常单价 + 加班工时 × 加班单价
- **记件**: 金额 = 数量 × 单价
- **记平方**: 金额 = 面积 × 单价

**加班功能**:
- 仅记时类型支持加班
- 加班工时和加班单价为可选字段
- 如填写加班工时，必须填写加班单价

**特性**:
- 自动金额计算
- 智能验证
- 历史记录查询

### 3. 支出记录
- 快速分类选择
- 灵活备注说明
- 分类统计分析

### 4. 数据统计
- **时间维度**: 按天/周/月
- **收入分析**: 类型分布、日均收入
- **支出分析**: 分类统计、支出排行
- **收支对比**: 结余计算、趋势分析

## 🔌 API 接口清单

### 用户相关
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/user/login` | POST | 微信登录 |
| `/api/user/info` | GET | 获取用户信息 |

### 收入相关
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/income` | POST | 新增收入记录 |
| `/api/income/list` | GET | 获取收入列表 |
| `/api/income/statistics` | GET | 获取收入统计 |

### 支出相关
| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/expense` | POST | 新增支出记录 |
| `/api/expense/list` | GET | 获取支出列表 |
| `/api/expense/statistics` | GET | 获取支出统计 |

详见 `record.md` 中的《接口设计》部分。

## 🎯 关键业务逻辑

### 金额计算（后端 IncomeRecordService）
```java
// 核心计算逻辑，确保精度
private BigDecimal calculateTotalAmount(IncomeRecordRequest request) {
    switch (incomeType) {
        case 1: // 记时
            totalAmount = workHours.multiply(unitPrice);
            if (overtimeHours > 0) {
                totalAmount = totalAmount.add(overtimeHours.multiply(overtimeUnitPrice));
            }
            break;
        case 2: // 记件
            totalAmount = quantity.multiply(unitPrice);
            break;
        case 3: // 记平方
            totalAmount = area.multiply(unitPrice);
            break;
    }
    return totalAmount.setScale(2, RoundingMode.HALF_UP);
}
```

### 统计查询（后端 Mapper）
```sql
-- 按类型统计收入
SELECT income_type, SUM(total_amount) as amount 
FROM income_record 
WHERE user_id = ? AND work_date BETWEEN ? AND ?
GROUP BY income_type;

-- 按原因统计支出
SELECT reason, SUM(amount) as total_amount 
FROM expense_record 
WHERE user_id = ? AND expense_date BETWEEN ? AND ?
GROUP BY reason ORDER BY total_amount DESC;
```

## 📊 页面流程

```
启动应用
  ↓
[未登录] → 登录页面 → 微信授权 → [已登录]
  ↓
┌── 收入页面 ─→ [新增收入] ─→ 选择类型 ─→ 填写信息（可填加班）─→ 保存
├── 支出页面 ─→ [新增支出] ─→ 选择原因 ─→ 填写金额 ─→ 保存
├── 统计页面 ─→ 选择维度 ─→ 查看报表 ─→ 分析数据
└── 我的页面 ─→ 查看概览 ─→ 个人信息 ─→ 退出登录
```

## 🔐 安全性设计

1. **认证**: JWT Token 实现无状态认证
2. **授权**: 基于用户上下文的数据隔离
3. **验证**: 前后端双重数据验证
4. **加密**: 微信 OAuth2 安全登录
5. **防护**: SQL 参数化查询防止注入

## ⚡ 性能优化

1. **数据库**
   - 联合索引优化查询
   - 分页加载大数据集
   - 缓存热点数据

2. **API**
   - gzip 压缩响应
   - 最小化 payload
   - 并发处理能力

3. **前端**
   - 虚拟滚动长列表
   - 组件懒加载
   - 本地存储 Token

## 📝 代码规范

### Java 代码规范
- 使用 Spring Boot 最佳实践
- Service-Mapper 分层设计
- 业务异常统一处理
- 参数验证在请求层

### 前端代码规范
- Vue 3 Composition API
- 文件按功能分类
- 组件化开发
- 响应式设计

### 注释规范
```java
/**
 * 类型描述 - 使用 JavaDoc
 * 核心业务逻辑必须有详细注释
 * @param 参数
 * @return 返回值
 */
```

## 🧪 测试建议

1. **功能测试**: 登录、记录、统计
2. **集成测试**: API 端到端测试
3. **性能测试**: 并发和大数据测试
4. **安全测试**: Token、权限验证

## 🐛 常见问题

### Q: 如何修改微信 AppID？
A: 编辑 `backend/src/main/resources/application.yml` 中的 wechat 配置

### Q: 如何修改后端 API 地址？
A: 编辑 `uni-fronted/utils/request.js` 中的 BASE_URL

### Q: 数据库初始化失败？
A: 确保 MySQL 已启动，用户名密码正确，执行权限足够

### Q: Token 过期如何处理？
A: 前端自动捕获 401 错误并跳转到登录页

### Q: 加班功能如何使用？
A: 在记时类型下，填写正常工时后，可在"加班信息"区域填写加班工时和加班单价

## 📚 文档清单

- `record.md` - 项目需求文档（包含详细接口设计）
- `PROJECT_GUIDE.md` - 项目指南（本文件）
- `backend/README.md` - 后端项目文档
- `uni-fronted/README.md` - 前端项目文档
- `database/init.sql` - 数据库初始化脚本

## 🚀 部署指南

### 后端部署
1. 修改数据库连接配置
2. 修改 JWT 密钥和过期时间
3. 修改微信 AppID/Secret
4. 编译 JAR：`mvn clean package`
5. 运行 JAR：`java -jar work-record-backend-1.0.0.jar`

### 前端部署
1. 配置后端 API 地址
2. 修改小程序 AppID
3. 编译小程序：`npm run build:mp-weixin`
4. 上传到微信小程序管理后台

## 🎁 后续功能

- 🔲 数据导出（图片/PDF）
- 🔲 多设备同步
- 🔲 财务报表
- 🔲 预算提醒
- 🔲 消费分析
- 🔲 云备份
- 🔲 加班统计报表

## 📞 技术支持

**项目维护者**: Touzi
**联系方式**: swwwtouzi@outlook.com
**问题反馈**: https://github.com/onetouzi/record/issues/new

## 📄 许可证

MIT License

---

**最后更新**: 2024-01-01
**项目版本**: 1.0.0