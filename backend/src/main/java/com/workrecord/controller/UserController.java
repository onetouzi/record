package com.workrecord.controller;

import com.workrecord.common.Result;
import com.workrecord.dto.request.WxLoginRequest;
import com.workrecord.dto.response.LoginResponse;
import com.workrecord.dto.response.UserInfoResponse;
import com.workrecord.service.UserService;
import com.workrecord.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户登录、登出、获取用户信息等相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 微信登录接口
     * 通过微信code交换用户openid，实现登录或自动注册
     *
     * @param request 包含微信登录code
     * @return 登录响应（包含token和用户信息）
     */
    @PostMapping("/login")
    public Result<LoginResponse> wxLogin(@RequestBody WxLoginRequest request) {
        log.info("用户微信登录请求");

        // 调用服务进行登录
        LoginResponse response = userService.wxLogin(request.getCode());

        return Result.success("登录成功", response);
    }

    /**
     * 获取当前登录用户信息
     * 需要提供有效的JWT Token
     *
     * @return 用户信息响应
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getUserInfo() {
        log.info("获取用户信息，userId: {}", UserContext.getUserId());

        // 从上下文中获取当前用户ID
        Long userId = UserContext.getUserId();

        // 调用服务获取用户信息
        UserInfoResponse response = userService.getUserInfo(userId);

        return Result.success("获取用户信息成功", response);
    }
}
