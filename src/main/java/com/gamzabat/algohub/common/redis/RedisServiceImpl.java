package com.gamzabat.algohub.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void setValues(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setValues(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key,value,duration);
    }

    @Override
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if(values.get(key) == null) return "";
        return String.valueOf(values.get(key));
    }

    @Override
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
