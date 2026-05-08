package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 收入记录响应DTO
 */
@Data
public class IncomeRecordResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 收入类型: 1-记时, 2-记件, 3-记平方 */
    private Integer incomeType;

    /** 正常工时(小时) */
    private BigDecimal workHours;

    /** 加班工时(小时)，仅记时类型有效 */
    private BigDecimal overtimeHours;

    /** 加班单价，仅记时类型有效 */
    private BigDecimal overtimeUnitPrice;

    /** 数量 */
    private Integer quantity;

    /** 面积(平方米) */
    private BigDecimal area;

    /** 正常单价 */
    private BigDecimal unitPrice;

    /** 总金额 */
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
}