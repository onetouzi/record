package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应DTO
 * @param <T> 数据类型
 */
@Data
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 数据列表 */
    private List<T> list;
}