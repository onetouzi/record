package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 支出统计响应DTO
 */
@Data
public class ExpenseStatisticsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总支出金额 */
    private BigDecimal totalExpense;

    /** 各支出项目统计 */
    private List<ExpenseItemResponse> expenseItems;
}