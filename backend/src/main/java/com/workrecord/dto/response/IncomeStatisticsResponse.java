package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 收入统计响应DTO
 */
@Data
public class IncomeStatisticsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总收入金额 */
    private BigDecimal totalIncome;

    /** 各收入类型金额统计: key为类型(1/2/3), value为金额 */
    private Map<String, BigDecimal> incomeTypes;

    /** 工作天数 */
    private Integer workDays;

    /** 详细记录列表（可选） */
    private List<IncomeRecordResponse> records;
}