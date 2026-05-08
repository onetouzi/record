package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支出记录响应DTO
 */
@Data
public class ExpenseRecordResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

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
}