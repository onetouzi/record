package com.workrecord.util;

/**
 * 用户上下文工具类
 * 使用ThreadLocal存储当前请求的用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 清除当前用户ID
     */
    public static void clear() {
        USER_ID.remove();
    }
}