# 零工记账小程序 - 后端项目

## 项目概述

这是一个基于 Spring Boot 3 + MyBatis Plus 的后端服务项目，为打零工人员的记账小程序提供 REST API 接口。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.0+
- **认证**: JWT (JJWT 0.12.3)
- **HTTP**: Spring Boot Web
- **工具**: Hutool 5.8.24, Lombok
- **构建**: Maven

## 项目结构

```
backend/
├── src/main/java/com/workrecord/
│   ├── WorkRecordApplication.java      # 启动类
│   ├── entity/                         # 实体类
│   │   ├── User.java                   # 用户实体
│   │   ├── IncomeRecord.java           # 收入记录实体
│   │   └── ExpenseRecord.java          # 支出记录实体
│   ├── controller/                     # 控制器
│   │   ├── UserController.java         # 用户接口
│   │   ├── IncomeRecordController.java # 收入接口
│   │   └── ExpenseRecordController.java# 支出接口
│   ├── service/                        # 业务服务
│   │   ├── UserService.java            # 用户服务
│   │   ├── IncomeRecordService.java    # 收入服务
│   │   └── ExpenseRecordService.java   # 支出服务
│   ├── mapper/                         # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── IncomeRecordMapper.java     # 包含自定义SQL
│   │   └── ExpenseRecordMapper.java    # 包含自定义SQL
│   ├── dto/                            # 数据传输对象
│   │   ├── request/                    # 请求 DTO
│   │   │   ├── WxLoginRequest.java
│   │   │   ├── IncomeRecordRequest.java
│   │   │   ├── ExpenseRecordRequest.java
│   │   │   ├── PageRequest.java
│   │   │   └── StatisticsRequest.java
│   │   └── response/                   # 响应 DTO
│   │       ├── LoginResponse.java
│   │       ├── UserInfoResponse.java
│   │       ├── IncomeRecordResponse.java
│   │       ├── ExpenseRecordResponse.java
│   │       ├── IncomeStatisticsResponse.java
│   │       ├── ExpenseStatisticsResponse.java
│   │       ├── PageResponse.java
│   │       └── RecordIdResponse.java
│   ├── common/                         # 通用类
│   │   └── Result.java                 # 统一响应类
│   ├── config/                         # 配置类
│   │   ├── JwtConfig.java              # JWT 配置
│   │   ├── WeChatConfig.java           # 微信配置
│   │   └── WebConfig.java              # Web 配置
│   ├── exception/                      # 异常处理
│   │   ├── BusinessException.java      # 业务异常
│   │   └── GlobalExceptionHandler.java # 全局异常处理器
│   ├── interceptor/                    # 拦截器
│   │   └── JwtInterceptor.java         # JWT 认证拦截器
│   └── util/                           # 工具类
│       ├── JwtUtil.java                # JWT 工具
│       └── UserContext.java            # 用户上下文
├── src/main/resources/
│   ├── application.yml                 # 应用配置
│   └── mapper/                         # MyBatis 映射文件（可选）
└── pom.xml                            # Maven 配置
```

## 核心功能模块

### 1. 用户管理 (UserService)

**微信登录流程**:
1. 小程序通过 `uni.login()` 获取 code
2. 后端使用 code 调用微信接口获取 openid
3. 根据 openid 查询或创建用户
4. 生成 JWT Token 返回给前端
5. 前端保存 Token 用于后续请求认证

**相关接口**:
- `POST /api/user/login` - 微信登录
- `GET /api/user/info` - 获取用户信息（需要认证）

### 2. 收入管理 (IncomeRecordService)

**关键业务逻辑**:
支持三种收入计算方式:
- **记时 (type=1)**: 总金额 = 工作时长 × 单价
- **记件 (type=2)**: 总金额 = 数量 × 单价
- **记平方 (type=3)**: 总金额 = 面积 × 单价

**特性**:
- 自动计算总金额并保留两位小数
- 参数验证确保数据完整性
- 支持多维度统计查询

**相关接口**:
- `POST /api/income` - 新增收入记录
- `GET /api/income/list` - 获取记录列表（分页）
- `GET /api/income/statistics` - 获取统计数据

### 3. 支出管理 (ExpenseRecordService)

**特性**:
- 简单的金额记录模式
- 支持按日期和原因分类统计
- 灵活的查询过滤

**相关接口**:
- `POST /api/expense` - 新增支出记录
- `GET /api/expense/list` - 获取记录列表（分页）
- `GET /api/expense/statistics` - 获取统计数据

## 数据库设计

### 用户表 (user)
```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `openid` VARCHAR(100) NOT NULL UNIQUE,
  `nickname` VARCHAR(50),
  `avatar` VARCHAR(200),
  `phone` VARCHAR(20),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
```

### 收入记录表 (income_record)
```sql
CREATE TABLE `income_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `income_type` TINYINT NOT NULL,  -- 1-记时, 2-记件, 3-记平方
  `work_hours` DECIMAL(5,1),
  `quantity` INT,
  `area` DECIMAL(8,2),
  `unit_price` DECIMAL(10,2) NOT NULL,
  `total_amount` DECIMAL(10,2) NOT NULL,  -- 自动计算
  `service_object` VARCHAR(100),
  `contact_phone` VARCHAR(20),
  `work_date` DATE NOT NULL,
  `remark` VARCHAR(200),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_date` (`user_id`, `work_date`),
  INDEX `idx_user_type` (`user_id`, `income_type`)
)
```

### 支出记录表 (expense_record)
```sql
CREATE TABLE `expense_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `reason` VARCHAR(100) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `expense_date` DATE NOT NULL,
  `remark` VARCHAR(200),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_date` (`user_id`, `expense_date`)
)
```

## 关键业务逻辑

### 金额计算（IncomeRecordService::calculateTotalAmount）

```java
private BigDecimal calculateTotalAmount(IncomeRecordRequest request) {
    BigDecimal unitPrice = request.getUnitPrice();
    Integer incomeType = request.getIncomeType();
    BigDecimal totalAmount;

    switch (incomeType) {
        case 1: // 记时
            // 验证工作时长 > 0
            totalAmount = request.getWorkHours().multiply(unitPrice);
            break;
        case 2: // 记件
            // 验证数量 > 0
            totalAmount = new BigDecimal(request.getQuantity()).multiply(unitPrice);
            break;
        case 3: // 记平方
            // 验证面积 > 0
            totalAmount = request.getArea().multiply(unitPrice);
            break;
    }
    
    // 保留两位小数
    return totalAmount.setScale(2, RoundingMode.HALF_UP);
}
```

### 认证拦截（JwtInterceptor）

- 检查请求是否携带 Token
- 验证 Token 有效性
- 提取用户 ID 存入 ThreadLocal
- Token 过期自动拒绝并返回 401

### 统计查询（CustomSQL in Mapper）

```sql
-- 按收入类型统计
SELECT income_type, COALESCE(SUM(total_amount), 0) as amount 
FROM income_record 
WHERE user_id = ? AND work_date BETWEEN ? AND ?
GROUP BY income_type

-- 按支出原因统计
SELECT reason, COALESCE(SUM(amount), 0) as total_amount 
FROM expense_record 
WHERE user_id = ? AND expense_date BETWEEN ? AND ?
GROUP BY reason ORDER BY total_amount DESC
```

## API 接口规范

### 请求规范

所有 API 请求（除登录外）都需要在请求头中包含 Token:
```
Authorization: Bearer <token>
```

或

```
token: <token>
```

### 响应规范

统一使用以下 JSON 格式:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...},
  "timestamp": 1234567890
}
```

**状态码**:
- `200`: 成功
- `400`: 参数错误
- `401`: 未授权（Token 无效或过期）
- `404`: 资源不存在
- `500`: 服务器错误

### 异常处理

全局异常处理器 (`GlobalExceptionHandler`) 处理:
- `BusinessException` - 自定义业务异常
- `MethodArgumentNotValidException` - 参数验证异常
- `BindException` - 参数绑定异常
- `Exception` - 其他系统异常

## 开发指南

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.6+

### 初始化数据库

```bash
# 执行数据库初始化脚本
mysql -u root -p work_record < database/init.sql
```

### 配置应用

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/work_record
    username: root
    password: root  # 改为实际密码

wechat:
  appid: your-wechat-appid      # 微信小程序 AppID
  secret: your-wechat-secret    # 微信小程序 AppSecret

jwt:
  secret: your-jwt-secret-key   # 修改为更长的密钥
  expiration: 604800000         # 过期时间 7 天
```

### 启动应用

```bash
# Maven 编译
mvn clean package

# 运行应用
mvn spring-boot:run

# 或直接运行 JAR
java -jar target/work-record-backend-1.0.0.jar
```

应用将在 `http://localhost:8080` 运行。

## 关键注意事项

### 1. 微信登录配置

- 需要在微信小程序管理后台配置回调域名
- AppID 和 AppSecret 需要正确配置
- 获取的 openid 是用户的唯一标识

### 2. JWT Token 管理

- Token 过期时间建议设置为 7 天以上
- 密钥长度应 >= 256 bit（32 字符）
- 生产环境应使用强密钥

### 3. 数据验证

- 前端和后端都需要验证用户输入
- 金额计算应使用 BigDecimal 避免精度问题
- 日期格式统一使用 LocalDate

### 4. 事务管理

- 新增记录使用 @Transactional 确保原子性
- 数据库查询添加适当索引提高性能
- 使用 BETWEEN 进行日期范围查询

### 5. 安全性

- 使用 JWT 进行无状态认证
- 所有 API 请求使用 HTTPS（生产环境）
- 防止 SQL 注入（使用参数化查询）
- 防止 XSS 攻击（不存储 HTML）

## 性能优化

1. **数据库优化**
   - 为高频查询字段添加索引
   - 使用分页查询而非一次性加载
   - 适当使用缓存减少数据库访问

2. **API 优化**
   - 只返回必要字段
   - 合并多个相关请求
   - 使用 gzip 压缩响应

3. **代码优化**
   - 使用 MyBatis Plus 的 Lambda 查询
   - 避免 N+1 查询问题
   - 适当使用异步处理

## 测试建议

1. **单元测试**
   - Service 层业务逻辑
   - 金额计算精度
   - 参数验证逻辑

2. **集成测试**
   - API 端到端测试
   - 数据库集成测试
   - 事务一致性测试

3. **性能测试**
   - 数据库查询性能
   - 高并发访问
   - 大数据导入

## 后续开发

- [ ] 实现数据导出功能
- [ ] 添加数据备份和恢复
- [ ] 实现多设备数据同步
- [ ] 添加财务报表生成
- [ ] 实现预算提醒功能
- [ ] Elasticsearch 集成进行全文搜索
- [ ] Redis 缓存热点数据
- [ ] 消息队列异步处理任务

## 许可证

MIT

## 技术支持

- 后端 API 文档: `/doc/api.md`
- 数据库设计文档: `/database/design.md`
- 常见问题: `/docs/FAQ.md`
