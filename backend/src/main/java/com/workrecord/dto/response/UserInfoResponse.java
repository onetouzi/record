package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息响应DTO
 */
@Data
public class UserInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String phone;
}