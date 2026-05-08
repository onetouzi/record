package com.workrecord.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增支出记录请求DTO
 */
@Data
public class ExpenseRecordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支出原因/用途 */
    @NotBlank(message = "支出原因不能为空")
    private String reason;

    /** 支出金额 */
    @NotNull(message = "支出金额不能为空")
    @Positive(message = "支出金额必须大于0")
    private BigDecimal amount;

    /** 支出日期 */
    @NotNull(message = "支出日期不能为空")
    private LocalDate expenseDate;

    /** 备注说明 */
    private String remark;
}