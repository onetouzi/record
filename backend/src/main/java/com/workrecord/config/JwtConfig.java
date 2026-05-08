package com.workrecord.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /** JWT签名密钥 */
    private String secret;

    /** JWT过期时间（毫秒） */
    private Long expiration;
}