package com.workrecord.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增收入记录请求DTO
 * 支持三种收入类型:
 * 1-记时: 需填写workHours，可选填写overtimeHours和overtimeUnitPrice
 * 2-记件: 需填写quantity
 * 3-记平方: 需填写area
 */
@Data
public class IncomeRecordRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 收入类型: 1-记时, 2-记件, 3-记平方 */
    @NotNull(message = "收入类型不能为空")
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
    @NotNull(message = "单价不能为空")
    @Positive(message = "单价必须大于0")
    private BigDecimal unitPrice;

    /** 服务对象/雇主名称 */
    private String serviceObject;

    /** 联系电话 */
    private String contactPhone;

    /** 工作日期 */
    @NotNull(message = "工作日期不能为空")
    private LocalDate workDate;

    /** 备注说明 */
    private String remark;
}