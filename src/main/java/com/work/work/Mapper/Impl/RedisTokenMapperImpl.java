package com.work.work.Mapper.Impl;

import com.work.work.Mapper.RedisTokenMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenMapperImpl implements RedisTokenMapper {
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisTokenMapperImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    @Override
    public void setToken(String token, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(token, value, timeout, unit);
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public boolean exists(String token) {
        return redisTemplate.hasKey(token);
    }
}
