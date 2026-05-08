package com.workrecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收入记录实体类
 * 支持三种收入类型:
 * 1-记时: 按工作时长计算，金额 = 正常工时 × 正常单价 + 加班工时 × 加班单价
 * 2-记件: 按数量计算，金额 = 数量 × 单价
 * 3-记平方: 按面积计算，金额 = 面积 × 单价
 * 
 * 加班字段（overtimeHours, overtimeUnitPrice）仅对记时类型有效
 */
@Data
@TableName("income_record")
public class IncomeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 收入类型: 1-记时, 2-记件, 3-记平方 */
    private Integer incomeType;

    /** 正常工时(小时)，记时类型必填 */
    private BigDecimal workHours;

    /** 加班工时(小时)，仅记时类型有效 */
    private BigDecimal overtimeHours;

    /** 加班单价，仅记时类型有效 */
    private BigDecimal overtimeUnitPrice;

    /** 数量，记件类型必填 */
    private Integer quantity;

    /** 面积(平方米)，记平方类型必填 */
    private BigDecimal area;

    /** 正常单价 */
    private BigDecimal unitPrice;

    /** 总金额，根据类型自动计算 */
    private BigDecimal totalAmount;

    /** 服务对象/雇主名称 */
    private String serviceObject;

    /** 联系电话 */
    private String contactPhone;

    /** 工作日期 */
    private LocalDate workDate;

    /** 备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}