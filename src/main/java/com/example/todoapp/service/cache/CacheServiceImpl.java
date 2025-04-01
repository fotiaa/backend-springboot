package com.example.todoapp.service.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> void put(String key, T value, long ttl, TimeUnit timeUnit) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, timeUnit);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize cache value", e);
        }
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            String jsonValue = (String) value;
            return Optional.of(objectMapper.readValue(jsonValue, clazz));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            String jsonValue = (String) value;
            return Optional.of(objectMapper.readValue(jsonValue, typeReference));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
