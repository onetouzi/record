# 打零工人员记账小程序开发文档

## 1. 项目概述

### 1.1 项目背景

为满足打零工人员的记账需求，开发一款专门针对工人使用的记账小程序，帮助用户记录收入和支出情况，进行财务统计分析。特别支持加班工时的单独计价功能。

### 1.2 技术架构

- **前端**：uni-app Vue.js
- **后端**：Java Spring Boot
- **数据库**：MySQL
- **登录方式**：微信登录
- **项目类型**：小程序

### 1.3 功能模块

1. **收入管理**：记录不同类型的收入（支持加班工时）
2. **支出管理**：记录日常开支
3. **统计分析**：提供多维度的财务统计
4. **用户管理**：微信登录和个人信息管理

## 2. 数据库设计

### 2.1 用户表 (user)

```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `openid` VARCHAR(100) NOT NULL COMMENT '微信openid',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(200) COMMENT '头像',
  `phone` VARCHAR(20) COMMENT '手机号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2.2 收入记录表 (income_record)

```sql
CREATE TABLE `income_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `income_type` TINYINT NOT NULL COMMENT '收入类型: 1-记时, 2-记件, 3-记平方',
  `work_hours` DECIMAL(5,1) COMMENT '正常工时(小时)，记时类型必填',
  `overtime_hours` DECIMAL(5,1) COMMENT '加班工时(小时)，仅记时类型有效',
  `overtime_unit_price` DECIMAL(10,2) COMMENT '加班单价，仅记时类型有效',
  `quantity` INT COMMENT '数量，记件类型必填',
  `area` DECIMAL(8,2) COMMENT '面积(平方米)，记平方类型必填',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '正常单价',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
  `service_object` VARCHAR(100) COMMENT '服务对象',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `work_date` DATE NOT NULL COMMENT '工作日期',
  `remark` VARCHAR(200) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_date` (`user_id`, `work_date`),
  INDEX `idx_user_type` (`user_id`, `income_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2.3 支出记录表 (expense_record)

```sql
CREATE TABLE `expense_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `reason` VARCHAR(100) NOT NULL COMMENT '支出原因',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
  `expense_date` DATE NOT NULL COMMENT '支出日期',
  `remark` VARCHAR(200) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_date` (`user_id`, `expense_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 3. 接口设计

### 3.1 用户相关接口

#### 微信登录

- **URL**: `/api/user/login`
- **Method**: POST
- **Description**: 微信登录接口
- **Parameters**:
    - `code` (string, required): 微信登录code
- **Response**:

```json
{
  "token": "string",
  "user_info": {
    "id": 1,
    "nickname": "string",
    "avatar": "string"
  }
}
```

#### 获取用户信息

- **URL**: `/api/user/info`
- **Method**: GET
- **Description**: 获取用户信息
- **Authentication**: 需要token
- **Response**:

```json
{
  "id": 1,
  "nickname": "string",
  "avatar": "string",
  "phone": "string"
}
```

### 3.2 收入相关接口

#### 新增收入记录

- **URL**: `/api/income`
- **Method**: POST
- **Description**: 新增收入记录
- **Parameters**:
    - `income_type` (int, required): 收入类型 (1-记时, 2-记件, 3-记平方)
    - `work_hours` (number, optional): 正常工时(小时)，记时类型必填
    - `overtime_hours` (number, optional): 加班工时(小时)，仅记时类型有效
    - `overtime_unit_price` (number, optional): 加班单价，仅记时类型有效
    - `quantity` (int, optional): 数量，记件类型必填
    - `area` (number, optional): 面积(平方米)，记平方类型必填
    - `unit_price` (number, required): 正常单价
    - `service_object` (string, optional): 服务对象
    - `contact_phone` (string, optional): 联系电话
    - `work_date` (date, required): 工作日期
    - `remark` (string, optional): 备注
- **Response**:

```json
{
  "success": true,
  "message": "收入记录添加成功",
  "data": {
    "id": 1
  }
}
```

#### 获取收入记录列表

- **URL**: `/api/income/list`
- **Method**: GET
- **Description**: 获取收入记录列表
- **Parameters**:
    - `page` (int, optional): 页码
    - `size` (int, optional): 每页数量
    - `start_date` (date, optional): 开始日期
    - `end_date` (date, optional): 结束日期
- **Response**:

```json
{
  "total": 10,
  "list": [
    {
      "id": 1,
      "income_type": 1,
      "work_hours": 8.0,
      "overtime_hours": 2.0,
      "overtime_unit_price": 30.00,
      "quantity": null,
      "area": null,
      "unit_price": 25.00,
      "total_amount": 260.00,
      "service_object": "张老板",
      "contact_phone": "13800000000",
      "work_date": "2024-01-15",
      "remark": "工地搬砖，加班2小时",
      "create_time": "2024-01-15 08:00:00"
    }
  ]
}
```

#### 收入统计

- **URL**: `/api/income/statistics`
- **Method**: GET
- **Description**: 获取收入统计
- **Parameters**:
    - `date_type` (string, required): 统计类型 (day|week|month)
    - `date` (date, required): 日期
- **Response**:

```json
{
  "total_income": 5000.00,
  "income_types": {
    "1": 3000.00,
    "2": 1500.00,
    "3": 500.00
  },
  "work_days": 25
}
```

### 3.3 支出相关接口

#### 新增支出记录

- **URL**: `/api/expense`
- **Method**: POST
- **Description**: 新增支出记录
- **Parameters**:
    - `reason` (string, required): 支出原因
    - `amount` (number, required): 金额
    - `expense_date` (date, required): 支出日期
    - `remark` (string, optional): 备注
- **Response**:

```json
{
  "success": true,
  "message": "支出记录添加成功",
  "data": {
    "id": 1
  }
}
```

#### 获取支出记录列表

- **URL**: `/api/expense/list`
- **Method**: GET
- **Description**: 获取支出记录列表
- **Parameters**:
    - `page` (int, optional): 页码
    - `size` (int, optional): 每页数量
    - `start_date` (date, optional): 开始日期
    - `end_date` (date, optional): 结束日期
- **Response**:

```json
{
  "total": 5,
  "list": [
    {
      "id": 1,
      "reason": "买菜",
      "amount": 50.00,
      "expense_date": "2024-01-15",
      "remark": "日常开销",
      "create_time": "2024-01-15 08:00:00"
    }
  ]
}
```

#### 支出统计

- **URL**: `/api/expense/statistics`
- **Method**: GET
- **Description**: 获取支出统计
- **Parameters**:
    - `date_type` (string, required): 统计类型 (day|week|month)
    - `date` (date, required): 日期
- **Response**:

```json
{
  "total_expense": 1500.00,
  "expense_items": [
    {
      "reason": "买菜",
      "total_amount": 800.00
    },
    {
      "reason": "交通",
      "total_amount": 300.00
    }
  ]
}
```

## 4. 前端页面设计

### 4.1 主页面（收入页面）

- **布局**：
    - 顶部：日历组件 + 本月收入统计
    - 中部：大的"记工"按钮
    - 底部：导航栏（收入、支出、我的）

### 4.2 记账页面

- **功能**：
    - 选择记账类型（记时、记件、记平方）
    - 填写相应的工作信息
    - **记时类型支持填写加班工时和加班单价**
    - 选择工作日期
    - 保存记录

### 4.3 支出页面

- **布局**：
    - 顶部：日历组件 + 本月支出统计
    - 中部：支出记录列表
    - 底部：新增开支按钮

### 4.4 我的页面

- **统计信息**：
    - 每日/每周/每月收入统计
    - 不同工作类型的收入对比
    - 收支平衡分析
    - 工作天数统计

## 5. 核心业务逻辑

### 5.1 金额计算规则

**记时类型 (income_type = 1)**:
```
总金额 = 正常工时 × 正常单价 + 加班工时 × 加班单价
```

**记件类型 (income_type = 2)**:
```
总金额 = 数量 × 单价
```

**记平方类型 (income_type = 3)**:
```
总金额 = 面积 × 单价
```

### 5.2 加班规则

- 加班字段（overtime_hours, overtime_unit_price）仅对记时类型有效
- 如果填写了加班工时，必须填写加班单价
- 加班工时和加班单价都是可选字段

## 6. 开发指引

### 6.1 项目结构

```
project/
├── backend/          # 后端代码
│   ├── src/main/java # Java源码
│   ├── src/main/resources # 配置文件
│   └── pom.xml       # Maven配置
├── uni-fronted/      # 前端代码（uni-app）
│   ├── pages/        # 页面文件
│   ├── api/          # API接口
│   ├── utils/        # 工具函数
│   └── pages.json    # 路由配置
└── database/         # 数据库脚本
    └── init.sql      # 初始化脚本
```

### 6.2 技术栈

- **后端**：Spring Boot 3、MyBatis Plus、JWT
- **前端**：uni-app (Vue 3)
- **数据库**：MySQL 8.0

### 6.3 开发环境

- **JDK**：21
- **Node.js**：14+
- **MySQL**：8.0+

### 6.4 接口规范

- **返回格式**：统一使用JSON格式，使用统一的result类去返回
- **状态码**：
    - 200：成功
    - 400：参数错误
    - 401：未授权
    - 404：资源不存在
    - 500：服务器错误

## 7. 注意事项

1. **微信登录**：需要配置微信小程序的AppID和AppSecret
2. **数据验证**：前端和后端都需要进行数据验证
3. **安全性**：使用JWT进行用户认证，防止SQL注入
4. **性能优化**：对常用查询添加索引，使用分页查询
5. **用户体验**：提供友好的错误提示和加载状态
6. **加班功能**：仅记时类型支持加班，加班工时和加班单价需同时填写

## 8. 扩展计划

1. **数据导出**：支持导出记账数据(导出为图像)
2. **多设备同步**：支持多设备数据同步
3. **报表生成**：生成财务报表
4. **加班统计**：单独的加班时长和加班费统计

---

**文档版本**：v1.0
**最后更新**：2024-01-01