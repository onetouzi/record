package com.workrecord.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 分页查询请求DTO
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 页码，默认第1页 */
    private Integer page = 1;

    /** 每页数量，默认10条 */
    private Integer size = 10;

    /** 开始日期 */
    private LocalDate startDate;

    /** 结束日期 */
    private LocalDate endDate;
}