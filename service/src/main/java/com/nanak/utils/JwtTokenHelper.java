package com.nanak.utils;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author: nanak
 * @CreateTime: 2024-11-09
 * @Description:
 * @Version: 1.0
 */@Component
@Slf4j
public class JwtTokenHelper {
    // 密钥
    private String secretKey;
    // accessToken 过期时间，单位：秒
    private Long accessTokenExpireTime;
    // refreshToken 过期时间，单位：秒
    private Long refreshTokenExpireTime;

    public String generateAccessToken(String username) {
        HashMap<String, Object> map = new HashMap<>();
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + accessTokenExpireTime * 1000);


        map.put("username", username);
        map.put("expireTime", expireTime);
        String  token = JWT.create().addPayloads(map).setKey(secretKey.getBytes()).setIssuedAt(now).setNotBefore(now).setExpiresAt(expireTime).sign();
        log.info("Generated Access Token for user {}: {}", username, token);
        return token;
    }

    public String generateRefreshToken(String username) {
        HashMap<String, Object> map = new HashMap<>();
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + refreshTokenExpireTime * 1000);

        map.put("username", username);
        map.put("expireTime", expireTime);
        String  token = JWT.create().addPayloads(map).setKey(secretKey.getBytes()).setIssuedAt(now).setNotBefore(now).setExpiresAt(expireTime).sign();
        log.info("Generated Refresh Token for user {}: {}", username, token);
        return token;
    }

    public Boolean verifyToken(String token) {
        boolean isValid = JWTUtil.verify(token, secretKey.getBytes());
        try {
            JWTValidator.of(token).validateDate();
        } catch (ValidateException e) {
            isValid = false;
        }
        log.info("Token verification result: {}", isValid);
        return isValid;
    }

    public String refreshToken(String token) {
        String username = JWTUtil.parseToken(token).getPayload("username").toString();
        String newAccessToken = generateAccessToken(username);
        log.info("Refreshed Access Token for user {}: {}", username, newAccessToken);
        return newAccessToken;
    }

    public String getUsername(String token) {
        String username = JWTUtil.parseToken(token).getPayload("username").toString();
        log.info("Extracted username from token: {}", username);
        return username;
    }

    public JwtTokenHelper(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-expire-time}") Long accessTokenExpireTime,
            @Value("${jwt.refresh-token-expire-time}") Long refreshTokenExpireTime) {
        this.secretKey = secretKey;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
        checkProperties();
    }

    public void checkProperties() {
        if (accessTokenExpireTime == null || refreshTokenExpireTime == null || secretKey == null) {
            throw new IllegalArgumentException("JwtTokenHelper配置错误，缺少必要的配置项");
        }
        if (accessTokenExpireTime > refreshTokenExpireTime) {
            throw new IllegalArgumentException("refreshTokenExpireTime 不能小于 accessTokenExpireTime");
        }
        log.info("JwtTokenHelper配置加载成功，secretKey: {}, accessTokenExpireTime: {}, refreshTokenExpireTime: {}",
                secretKey, accessTokenExpireTime, refreshTokenExpireTime);
    }
}