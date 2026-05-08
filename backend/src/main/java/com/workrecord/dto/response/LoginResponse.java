package com.workrecord.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** JWT Token */
    private String token;

    /** 用户基本信息 */
    private UserInfo userInfo;

    /**
     * 用户基本信息内部类
     */
    @Data
    public static class UserInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 用户ID */
        private Long id;

        /** 昵称 */
        private String nickname;

        /** 头像URL */
        private String avatar;
    }
}