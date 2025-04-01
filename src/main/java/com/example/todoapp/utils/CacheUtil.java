package com.example.todoapp.utils;

import com.example.todoapp.model.Task;
import com.example.todoapp.service.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;

import static com.example.todoapp.constant.CacheConstant.*;
import java.util.List;
import java.util.Optional;

@Component
public class CacheUtil {
    @Autowired
    private CacheService cacheService;

    // Generic task cache methods
    public Optional<Task> getTaskFromCache(String id) {
        return cacheService.get(TASK_BY_ID_PREFIX + id, Task.class);
    }

    public Optional<Task> getUserTaskFromCache(String id, String userId) {
        return cacheService.get(TASK_PREFIX + id + "::" + userId, Task.class);
    }

    public void cacheTask(String id, Task task) {
        cacheService.put(TASK_BY_ID_PREFIX + id, task, LONG_TTL, DEFAULT_TIME_UNIT);
    }

    public void cacheUserTask(String id, String userId, Task task) {
        cacheService.put(TASK_PREFIX + id + "::" + userId, task, LONG_TTL, DEFAULT_TIME_UNIT);
    }

    // Task list cache methods
    public Optional<List<Task>> getAllTasksFromCache() {
        return cacheService.get(ALL_TASKS, new TypeReference<List<Task>>() {});
    }

    public Optional<List<Task>> getUserTasksFromCache(String userId) {
        return cacheService.get(USER_TASKS_PREFIX + userId, new TypeReference<List<Task>>() {});
    }

    public Optional<List<Task>> getTasksByStatusFromCache(String status) {
        return cacheService.get(TASKS_BY_STATUS_PREFIX + status, new TypeReference<List<Task>>() {});
    }

    public Optional<List<Task>> getUserTasksByStatusFromCache(String status, String userId) {
        return cacheService.get(USER_TASKS_BY_STATUS_PREFIX + status + "::" + userId, new TypeReference<List<Task>>() {});
    }

    public void cacheAllTasks(List<Task> tasks) {
        cacheService.put(ALL_TASKS, tasks, MEDIUM_TTL, DEFAULT_TIME_UNIT);
    }

    public void cacheUserTasks(String userId, List<Task> tasks) {
        cacheService.put(USER_TASKS_PREFIX + userId, tasks, MEDIUM_TTL, DEFAULT_TIME_UNIT);
    }

    public void cacheTasksByStatus(String status, List<Task> tasks) {
        cacheService.put(TASKS_BY_STATUS_PREFIX + status, tasks, MEDIUM_TTL, DEFAULT_TIME_UNIT);
    }

    public void cacheUserTasksByStatus(String status, String userId, List<Task> tasks) {
        cacheService.put(USER_TASKS_BY_STATUS_PREFIX + status + "::" + userId, tasks, MEDIUM_TTL, DEFAULT_TIME_UNIT);
    }

    // Paginated cache methods
    public Optional<Page<Task>> getPaginatedTasksFromCache(Pageable pageable) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_TASKS_PREFIX, pageable);
        return cacheService.get(cacheKey, new TypeReference<Page<Task>>() {});
    }

    public Optional<Page<Task>> getPaginatedUserTasksFromCache(String userId, Pageable pageable) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_USER_TASKS_PREFIX + userId + "::", pageable);
        return cacheService.get(cacheKey, new TypeReference<Page<Task>>() {});
    }

    public Optional<Page<Task>> getPaginatedTasksByStatusFromCache(String status, Pageable pageable) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_TASKS_BY_STATUS_PREFIX + status + "::", pageable);
        return cacheService.get(cacheKey, new TypeReference<Page<Task>>() {});
    }

    public Optional<Page<Task>> getPaginatedUserTasksByStatusFromCache(String status, String userId, Pageable pageable) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_USER_TASKS_BY_STATUS_PREFIX + status + "::" + userId + "::", pageable);
        return cacheService.get(cacheKey, new TypeReference<Page<Task>>() {});
    }

    public void cachePaginatedTasks(Pageable pageable, Page<Task> page) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_TASKS_PREFIX, pageable);
        cacheService.put(cacheKey, page, SHORT_TTL, DEFAULT_TIME_UNIT);
    }

    public void cachePaginatedUserTasks(String userId, Pageable pageable, Page<Task> page) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_USER_TASKS_PREFIX + userId + "::", pageable);
        cacheService.put(cacheKey, page, SHORT_TTL, DEFAULT_TIME_UNIT);
    }

    public void cachePaginatedTasksByStatus(String status, Pageable pageable, Page<Task> page) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_TASKS_BY_STATUS_PREFIX + status + "::", pageable);
        cacheService.put(cacheKey, page, SHORT_TTL, DEFAULT_TIME_UNIT);
    }

    public void cachePaginatedUserTasksByStatus(String status, String userId, Pageable pageable, Page<Task> page) {
        String cacheKey = buildPaginatedCacheKey(PAGINATED_USER_TASKS_BY_STATUS_PREFIX + status + "::" + userId + "::", pageable);
        cacheService.put(cacheKey, page, SHORT_TTL, DEFAULT_TIME_UNIT);
    }

    // Cache invalidation methods
    public void invalidateTaskCaches(String id, String userId, String oldStatus, String newStatus) {
        // User caches
        cacheService.evict(TASK_PREFIX + id + "::" + userId);
        cacheService.evict(USER_TASKS_PREFIX + userId);
        cacheService.evict(USER_TASKS_BY_STATUS_PREFIX + oldStatus + "::" + userId);
        cacheService.evict(USER_TASKS_BY_STATUS_PREFIX + newStatus + "::" + userId);
        cacheService.evict(PAGINATED_USER_TASKS_PREFIX + userId);
        cacheService.evict(PAGINATED_USER_TASKS_BY_STATUS_PREFIX + oldStatus + "::" + userId);
        cacheService.evict(PAGINATED_USER_TASKS_BY_STATUS_PREFIX + newStatus + "::" + userId);

        // Admin caches
        cacheService.evict(TASK_BY_ID_PREFIX + id);
        cacheService.evict(ALL_TASKS);
        cacheService.evict(TASKS_BY_STATUS_PREFIX + oldStatus);
        cacheService.evict(TASKS_BY_STATUS_PREFIX + newStatus);
        cacheService.evict(PAGINATED_TASKS_PREFIX);
        cacheService.evict(PAGINATED_TASKS_BY_STATUS_PREFIX + oldStatus);
        cacheService.evict(PAGINATED_TASKS_BY_STATUS_PREFIX + newStatus);
    }

    public void invalidateCreationCaches(String userId, String status) {
        cacheService.evict(USER_TASKS_PREFIX + userId);
        cacheService.evict(ALL_TASKS);
        cacheService.evict(TASKS_BY_STATUS_PREFIX + status);
    }

    public void invalidateDeletionCaches(String id, String userId, String status) {
        // User caches
        cacheService.evict(TASK_PREFIX + id + "::" + userId);
        cacheService.evict(USER_TASKS_PREFIX + userId);
        cacheService.evict(USER_TASKS_BY_STATUS_PREFIX + status + "::" + userId);
        cacheService.evict(PAGINATED_USER_TASKS_PREFIX + userId);
        cacheService.evict(PAGINATED_USER_TASKS_BY_STATUS_PREFIX + status + "::" + userId);

        // Admin caches
        cacheService.evict(TASK_BY_ID_PREFIX + id);
        cacheService.evict(ALL_TASKS);
        cacheService.evict(TASKS_BY_STATUS_PREFIX + status);
        cacheService.evict(PAGINATED_TASKS_PREFIX);
        cacheService.evict(PAGINATED_TASKS_BY_STATUS_PREFIX + status);
    }

    // Clear all caches
    public void clearUserCaches(String userId) {
        cacheService.clearCache(USER_TASKS_CACHE);
        cacheService.clearCache(TASK_CACHE);
        cacheService.clearCache(USER_TASKS_BY_STATUS_CACHE);
        cacheService.clearCache(PAGINATED_USER_TASKS_CACHE);
        cacheService.clearCache(PAGINATED_USER_TASKS_BY_STATUS_CACHE);
    }

    public void clearAdminCaches() {
        cacheService.clearCache(ALL_TASKS);
        cacheService.clearCache(TASK_BY_ID_CACHE);
        cacheService.clearCache(TASKS_BY_STATUS_CACHE);
        cacheService.clearCache(PAGINATED_TASKS_CACHE);
        cacheService.clearCache(PAGINATED_TASKS_BY_STATUS_CACHE);
    }

    // Utility methods
    private String buildPaginatedCacheKey(String prefix, Pageable pageable) {
        return prefix + pageable.getPageNumber() + "::" +
                pageable.getPageSize() + "::" +
                pageable.getSort().toString();
    }
}
