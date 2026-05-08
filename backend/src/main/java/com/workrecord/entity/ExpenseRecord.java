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
 * 支出记录实体类
 */
@Data
@TableName("expense_record")
public class ExpenseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 支出原因/用途 */
    private String reason;

    /** 支出金额 */
    private BigDecimal amount;

    /** 支出日期 */
    private LocalDate expenseDate;

    /** 备注说明 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}