package com.workrecord.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 微信openid */
    private String openid;

    /** 昵称 */
    private String nickname;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String phone;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}