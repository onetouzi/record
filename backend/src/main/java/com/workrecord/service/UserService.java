package com.workrecord.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.workrecord.config.WeChatConfig;
import com.workrecord.dto.response.LoginResponse;
import com.workrecord.dto.response.UserInfoResponse;
import com.workrecord.entity.User;
import com.workrecord.exception.BusinessException;
import com.workrecord.mapper.UserMapper;
import com.workrecord.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务服务
 * 处理微信登录和用户信息管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final WeChatConfig weChatConfig;

    /** 微信登录API地址 */
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录
     * 通过微信code换取openid，实现用户登录或注册
     * @param code 微信登录code
     * @return 登录响应（token和用户信息）
     */
    @Transactional
    public LoginResponse wxLogin(String code) {
        // 调用微信接口获取openid
        String openid = getOpenIdFromWeChat(code);

        // 查询用户是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(wrapper);

        // 用户不存在则自动注册
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("用户" + openid.substring(openid.length() - 6));
            userMapper.insert(user);
            log.info("新用户注册成功, openid: {}", openid);
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        response.setUserInfo(userInfo);

        log.info("用户登录成功, userId: {}", user.getId());
        return response;
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setPhone(user.getPhone());
        return response;
    }

    /**
     * 调用微信接口获取openid
     * @param code 微信登录code
     * @return openid
     */
    private String getOpenIdFromWeChat(String code) {
        // 构建请求URL
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_LOGIN_URL, weChatConfig.getAppid(), weChatConfig.getSecret(), code);

        // 发送HTTP请求
        String response = HttpUtil.get(url);
        log.debug("微信登录响应: {}", response);

        // 解析响应
        JSONObject json = JSONUtil.parseObj(response);
        if (json.containsKey("errcode") && json.getInt("errcode") != 0) {
            log.error("微信登录失败: {}", response);
            throw new BusinessException(401, "微信登录失败: " + json.getStr("errmsg"));
        }

        return json.getStr("openid");
    }
}