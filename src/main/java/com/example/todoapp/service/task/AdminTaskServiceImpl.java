package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.service.cache.CacheService;
import com.example.todoapp.utils.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AdminTaskServiceImpl implements AdminTaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAsyncService taskAsyncService;

    @Autowired
    private CacheUtil cacheUtil;

    @Override
    public List<Task> getAllTasks() {
        // Try to get from cache first
        Optional<List<Task>> cachedTasks = cacheUtil.getAllTasksFromCache();
        if (cachedTasks.isPresent()) {
            return cachedTasks.get();
        }

        // If not in cache, fetch from repository
        List<Task> tasks = taskRepository.findByDeletedFalse();

        // Store in cache
        cacheUtil.cacheAllTasks(tasks);

        return tasks;
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        // Try to get from cache first
        Optional<Task> cachedTask = cacheUtil.getTaskFromCache(id);
        if (cachedTask.isPresent()) {
            return cachedTask;
        }

        // If not in cache, fetch from repository
        Optional<Task> taskOpt = taskRepository.findByIdAndDeletedFalse(id);

        // Store in cache if found
        taskOpt.ifPresent(task -> cacheUtil.cacheTask(id, task));

        return taskOpt;
    }

    @Override
    public Task updateTask(String id, Task task) {
        return getTaskById(id)
                .map(existingTask -> {
                    String oldStatus = String.valueOf(existingTask.getStatus());
                    String userId = existingTask.getCreatedBy();

                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setStatus(task.getStatus());
                    existingTask.setUpdatedAt(LocalDateTime.now());
                    Task savedTask = taskRepository.save(existingTask);

                    // Invalidate all relevant caches
                    cacheUtil.invalidateTaskCaches(id, userId, oldStatus, String.valueOf(task.getStatus()));

                    // Update cache with new task data
                    cacheUtil.cacheTask(id, savedTask);

                    // Asynchronously index for search
                    taskAsyncService.indexTaskForSearchAsync(savedTask);

                    return savedTask;
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void softDeleteTask(String id) {
        getTaskById(id)
                .map(task -> {
                    String status = String.valueOf(task.getStatus());
                    String userId = task.getCreatedBy();

                    task.setDeleted(true);
                    task.setUpdatedAt(LocalDateTime.now());
                    Task savedTask = taskRepository.save(task);

                    // Invalidate all relevant caches
                    cacheUtil.invalidateDeletionCaches(id, userId, status);

                    return savedTask;
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void restoreTask(String id) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        taskOpt.map(task -> {
            task.setDeleted(false);
            task.setUpdatedAt(LocalDateTime.now());
            Task savedTask = taskRepository.save(task);

            // Cache the restored task
            cacheUtil.cacheTask(id, savedTask);

            // Asynchronously index for search
            taskAsyncService.indexTaskForSearchAsync(savedTask);

            return savedTask;
        }).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public void permanentDeleteTask(String id) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        taskOpt.ifPresent(task -> {
            String userId = task.getCreatedBy();
            String status = String.valueOf(task.getStatus());

            // Delete from database
            taskRepository.deleteById(id);

            // Invalidate all relevant caches
            cacheUtil.invalidateDeletionCaches(id, userId, status);
        });
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        // Try to get from cache first
        Optional<List<Task>> cachedTasks = cacheUtil.getTasksByStatusFromCache(status);
        if (cachedTasks.isPresent()) {
            return cachedTasks.get();
        }

        // If not in cache, fetch from repository
        List<Task> tasks = taskRepository.findByStatus(status);

        // Store in cache
        cacheUtil.cacheTasksByStatus(status, tasks);

        return tasks;
    }

    @Override
    public List<Task> searchTasksByTitle(String title) {
        // This is a search operation, so we don't cache it
        return taskRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public Page<Task> getPaginatedTasks(Pageable pageable) {
        // Try to get from cache first
        Optional<Page<Task>> cachedPage = cacheUtil.getPaginatedTasksFromCache(pageable);
        if (cachedPage.isPresent()) {
            return cachedPage.get();
        }

        // If not in cache, fetch from repository
        Page<Task> page = taskRepository.findAll(pageable);

        // Store in cache
        cacheUtil.cachePaginatedTasks(pageable, page);

        return page;
    }

    @Override
    public List<Task> searchTasksFullText(String searchTerm) {
        return taskRepository.searchTasks(searchTerm);
    }

    @Override
    public Page<Task> getPaginatedTasksByStatus(String status, Pageable pageable) {
        // Try to get from cache first
        Optional<Page<Task>> cachedPage = cacheUtil.getPaginatedTasksByStatusFromCache(status, pageable);
        if (cachedPage.isPresent()) {
            return cachedPage.get();
        }

        // If not in cache, fetch from repository
        Page<Task> page = taskRepository.findByStatus(status, pageable);

        // Store in cache
        cacheUtil.cachePaginatedTasksByStatus(status, pageable, page);

        return page;
    }

    @Override
    public void clearAdminCaches() {
        cacheUtil.clearAdminCaches();
    }
}