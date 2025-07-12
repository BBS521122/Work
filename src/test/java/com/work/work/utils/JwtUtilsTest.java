package com.work.work.utils;

import com.auth0.jwt.interfaces.Claim;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {
    private static final String SECRET_KEY = "test-secret-key";
    private static final long TTL_MILLIS = 3600000; // 1小时

    @Test
    void createJWT_ValidInput_ShouldReturnToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        claims.put("username", "testUser");

        String token = JwtUtils.createJWT(SECRET_KEY, TTL_MILLIS, claims);
        
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // 验证JWT标准三段式结构
    }

    @Test
    void parseJWT_ValidToken_ShouldReturnClaims() {
        // 准备测试数据
        Map<String, Object> originalClaims = new HashMap<>();
        originalClaims.put("userId", 123);
        originalClaims.put("role", "admin");

        // 生成Token
        String token = JwtUtils.createJWT(SECRET_KEY, TTL_MILLIS, originalClaims);

        // 解析Token
        Map<String, Claim> parsedClaims = JwtUtils.parseJWT(SECRET_KEY, token);

        // 验证结果
        assertEquals(123, parsedClaims.get("claims").asMap().get("userId"));
        assertEquals("admin", parsedClaims.get("claims").asMap().get("role"));
    }

    @Test
    void parseJWT_ExpiredToken_ShouldThrowException() {
        // 生成已过期的Token (TTL设置为过去时间)
        Map<String, Object> claims = new HashMap<>();
        claims.put("test", "value");
        String expiredToken = JwtUtils.createJWT(SECRET_KEY, -10000, claims);

        // 验证异常
        assertThrows(
            com.auth0.jwt.exceptions.TokenExpiredException.class,
            () -> JwtUtils.parseJWT(SECRET_KEY, expiredToken)
        );
    }

    @Test
    void parseJWT_InvalidSecret_ShouldThrowException() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("test", "value");
        String validToken = JwtUtils.createJWT(SECRET_KEY, TTL_MILLIS, claims);

        assertThrows(
            com.auth0.jwt.exceptions.JWTVerificationException.class,
            () -> JwtUtils.parseJWT("wrong-secret", validToken)
        );
    }

    @Test
    void createAndParseJWT_ShouldMaintainClaimIntegrity() {
        // 准备复杂claims
        Map<String, Object> originalClaims = new HashMap<>();
        originalClaims.put("intValue", 100);
        originalClaims.put("stringValue", "test");
        originalClaims.put("boolValue", true);
        originalClaims.put("nested", Map.of("key", "value"));

        // 生成并解析
        String token = JwtUtils.createJWT(SECRET_KEY, TTL_MILLIS, originalClaims);
        Map<String, Claim> parsedClaims = JwtUtils.parseJWT(SECRET_KEY, token);
        Map<?, ?> claimsMap = parsedClaims.get("claims").asMap();

        // 验证所有字段
        assertEquals(100, claimsMap.get("intValue"));
        assertEquals("test", claimsMap.get("stringValue"));
        assertEquals(true, claimsMap.get("boolValue"));
        assertEquals("value", ((Map<?, ?>) claimsMap.get("nested")).get("key"));
    }
}
