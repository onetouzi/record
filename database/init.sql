-- =============================================
-- 打零工人员记账小程序 - 数据库初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `work_record` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `work_record`;

-- =============================================
-- 1. 用户表 (user)
-- 存储微信登录用户信息
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `openid` VARCHAR(100) NOT NULL COMMENT '微信openid',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(200) COMMENT '头像URL',
  `phone` VARCHAR(20) COMMENT '手机号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 2. 收入记录表 (income_record)
-- 记录工人的收入信息，支持三种类型:
-- 1-记时: 按工作时长计算，金额 = (正常工时 + 加班工时) × 单价
-- 2-记件: 按数量计算，金额 = 数量 × 单价
-- 3-记平方: 按面积计算，金额 = 面积 × 单价
-- 加班字段仅对记时类型有效
-- =============================================
DROP TABLE IF EXISTS `income_record`;
CREATE TABLE `income_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `income_type` TINYINT NOT NULL COMMENT '收入类型: 1-记时, 2-记件, 3-记平方',
  `work_hours` DECIMAL(5,1) COMMENT '正常工时(小时)，记时类型必填',
  `overtime_hours` DECIMAL(5,1) COMMENT '加班工时(小时)，仅记时类型有效',
  `overtime_unit_price` DECIMAL(10,2) COMMENT '加班单价，仅记时类型有效',
  `quantity` INT COMMENT '数量，记件类型必填',
  `area` DECIMAL(8,2) COMMENT '面积(平方米)，记平方类型必填',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '正常单价',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额，根据类型自动计算',
  `service_object` VARCHAR(100) COMMENT '服务对象/雇主名称',
  `contact_phone` VARCHAR(20) COMMENT '联系电话',
  `work_date` DATE NOT NULL COMMENT '工作日期',
  `remark` VARCHAR(200) COMMENT '备注说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_date` (`user_id`, `work_date`),
  INDEX `idx_user_type` (`user_id`, `income_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收入记录表';

-- =============================================
-- 3. 支出记录表 (expense_record)
-- 记录工人的日常支出
-- =============================================
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `reason` VARCHAR(100) NOT NULL COMMENT '支出原因/用途',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '支出金额',
  `expense_date` DATE NOT NULL COMMENT '支出日期',
  `remark` VARCHAR(200) COMMENT '备注说明',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_user_date` (`user_id`, `expense_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支出记录表';

-- =============================================
-- 数据库升级脚本（针对已存在的数据库）
-- 如果是全新安装，可以忽略此部分
-- =============================================

-- =============================================
-- 初始化测试数据（可选，生产环境请删除）
-- =============================================
-- 插入测试用户
-- INSERT INTO `user` (`openid`, `nickname`, `avatar`, `phone`) VALUES
-- ('test_openid_001', '测试用户', 'https://example.com/avatar.png', '13800138000');

-- 插入测试收入记录（包含加班）
-- INSERT INTO `income_record` (`user_id`, `income_type`, `work_hours`, `overtime_hours`, `overtime_unit_price`, `quantity`, `area`, `unit_price`, `total_amount`, `service_object`, `contact_phone`, `work_date`, `remark`) VALUES
-- (1, 1, 8.0, 2.0, 30.00, NULL, NULL, 25.00, 260.00, '张老板', '13800000001', '2024-01-15', '工地搬砖，加班2小时'),
-- (1, 2, NULL, NULL, NULL, 100, NULL, 5.00, 500.00, '李老板', '13800000002', '2024-01-16', '零件组装'),
-- (1, 3, NULL, NULL, NULL, NULL, 50.00, 30.00, 1500.00, '王老板', '13800000003', '2024-01-17', '墙面粉刷');

-- 插入测试支出记录
-- INSERT INTO `expense_record` (`user_id`, `reason`, `amount`, `expense_date`, `remark`) VALUES
-- (1, '买菜', 50.00, '2024-01-15', '日常开销'),
-- (1, '交通', 30.00, '2024-01-16', '公交充值');