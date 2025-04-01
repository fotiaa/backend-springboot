package com.example.todoapp.service.cache;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CacheService {
    <T> void put(String key, T value, long ttl, TimeUnit timeUnit);
    <T> Optional<T> get(String key, Class<T> clazz);
    <T> Optional<T> get(String key, TypeReference<T> typeReference);
    void evict(String key);
    void clearCache(String cacheName);
}