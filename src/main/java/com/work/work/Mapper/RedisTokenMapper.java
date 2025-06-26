package com.work.work.Mapper;

import java.util.concurrent.TimeUnit;


public interface RedisTokenMapper {
    String getToken(String token);
    void setToken(String token, String value, long timeout, TimeUnit unit);
    void deleteToken(String token);
    boolean exists(String token);
}
