package com.gamzabat.algohub.common.redis;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public interface RedisService {
    void setValues(String key, String value);
    void setValues(String key, String value, Duration duration);
    String getValues(String key);
    void deleteValues(String key);
}
