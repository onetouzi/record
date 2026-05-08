package com.workrecord.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信登录请求DTO
 */
@Data
public class WxLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 微信登录code */
    @NotBlank(message = "微信登录code不能为空")
    private String code;
}