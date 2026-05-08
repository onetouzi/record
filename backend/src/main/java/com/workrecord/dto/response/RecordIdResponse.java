package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 新增记录响应DTO（返回新增记录的ID）
 */
@Data
public class RecordIdResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 新增记录的ID */
    private Long id;
}