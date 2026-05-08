package com.workrecord.interceptor;

import com.workrecord.common.Result;
import com.workrecord.util.JwtUtil;
import com.workrecord.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JWT认证拦截器
 * 验证请求头中的Token，并将用户ID存入ThreadLocal
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 请求预处理
     * 验证JWT Token并提取用户信息
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头获取Token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token)) {
            token = request.getHeader("token");
        }

        // Token为空，返回未授权
        if (!StringUtils.hasText(token)) {
            log.warn("请求未携带Token, URI: {}", request.getRequestURI());
            writeUnauthorizedResponse(response, "请先登录");
            return false;
        }

        // 移除Bearer前缀（如果有）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token验证失败, URI: {}", request.getRequestURI());
            writeUnauthorizedResponse(response, "Token无效或已过期");
            return false;
        }

        // 提取用户ID并存入上下文
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserContext.setUserId(userId);
        log.debug("Token验证成功, userId: {}", userId);

        return true;
    }

    /**
     * 请求完成后清理上下文
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    /**
     * 写入未授权响应
     */
    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Void> result = Result.unauthorized(message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}