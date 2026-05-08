package com.workrecord.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 统计查询请求DTO
 */
@Data
public class StatisticsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计类型: day-日统计, week-周统计, month-月统计 */
    private String dateType;

    /** 统计日期 */
    private LocalDate date;
}