package com.workrecord.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支出明细项DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseItemResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 支出原因 */
    private String reason;

    /** 总金额 */
    private BigDecimal totalAmount;
}