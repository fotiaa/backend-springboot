package com.example.todoapp.service.task;

import com.example.todoapp.model.Task;
import com.example.todoapp.repository.TaskRepository;
import com.example.todoapp.utils.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAsyncService taskAsyncService;

    @Autowired
    private CacheUtil cacheUtil;

    @Override
    public List<Task> getUserTasks(String userId) {
        // Try to get from cache first
        Optional<List<Task>> cachedTasks = cacheUtil.getUserTasksFromCache(userId);
        if (cachedTasks.isPresent()) {
            return cachedTasks.get();
        }

        // If not in cache, fetch from repository
        List<Task> tasks = taskRepository.findByCreatedByAndDeletedFalse(userId);

        // Store in cache
        cacheUtil.cacheUserTasks(userId, tasks);

        return tasks;
    }

    @Override
    public Optional<Task> getUserTaskById(String id, String userId) {
        // Try to get from cache first
        Optional<Task> cachedTask = cacheUtil.getUserTaskFromCache(id, userId);
        if (cachedTask.isPresent()) {
            return cachedTask;
        }

        // If not in cache, fetch from repository
        Optional<Task> taskOpt = taskRepository.findByIdAndCreatedByAndDeletedFalse(id, userId);

        // Store in cache if found
        taskOpt.ifPresent(task -> cacheUtil.cacheUserTask(id, userId, task));

        return taskOpt;
    }

    @Override
    public Task createTask(Task task, String userId) {
        // Use async service for task creation
        CompletableFuture<Task> future = taskAsyncService.processTaskCreationAsync(task, userId);

        // For API response, we'll wait for the result
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Error creating task: " + e.getMessage(), e);
        }
    }

    @Override
    public Task updateTask(String id, Task task, String userId) {
        // Use async service for task update
        CompletableFuture<Task> future = taskAsyncService.processTaskUpdateAsync(id, task, userId);

        // For API response, we'll wait for the result
        try {
            return future.get();
        } catch (Exception e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RuntimeException("Error updating task: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getUserTasksByStatus(String status, String userId) {
        // Try to get from cache first
        Optional<List<Task>> cachedTasks = cacheUtil.getUserTasksByStatusFromCache(status, userId);
        if (cachedTasks.isPresent()) {
            return cachedTasks.get();
        }

        // If not in cache, fetch from repository
        List<Task> tasks = taskRepository.findByCreatedByAndStatusAndDeletedFalse(userId, status);

        // Store in cache
        cacheUtil.cacheUserTasksByStatus(status, userId, tasks);

        return tasks;
    }

    @Override
    public List<Task> searchUserTasksByTitle(String title, String userId) {
        // This is a search operation, so it's better not to cache the results
        return taskRepository.findByTitleContainingIgnoreCaseAndCreatedByAndDeletedFalse(title, userId);
    }

    @Override
    public Page<Task> getPaginatedUserTasks(String userId, Pageable pageable) {
        // Try to get from cache first
        Optional<Page<Task>> cachedPage = cacheUtil.getPaginatedUserTasksFromCache(userId, pageable);
        if (cachedPage.isPresent()) {
            return cachedPage.get();
        }

        // If not in cache, fetch from repository
        Page<Task> page = taskRepository.findByCreatedByAndDeletedFalse(userId, pageable);

        // Store in cache
        cacheUtil.cachePaginatedUserTasks(userId, pageable, page);

        return page;
    }

    @Override
    public Page<Task> getPaginatedUserTasksByStatus(String status, String userId, Pageable pageable) {
        // Try to get from cache first
        Optional<Page<Task>> cachedPage = cacheUtil.getPaginatedUserTasksByStatusFromCache(status, userId, pageable);
        if (cachedPage.isPresent()) {
            return cachedPage.get();
        }

        // If not in cache, fetch from repository
        Page<Task> page = taskRepository.findByStatusAndCreatedByAndDeletedFalse(status, userId, pageable);

        // Store in cache
        cacheUtil.cachePaginatedUserTasksByStatus(status, userId, pageable, page);

        return page;
    }

    @Override
    public List<Task> searchUserTasksFullText(String searchTerm, String userId) {
        // This is a search operation, so it's better not to cache the results
        return taskRepository.searchTasksBy(searchTerm, userId);
    }

    @Override
    public void softDeleteTask(String id, String userId) {
        // Use async service for task deletion
        taskAsyncService.processSoftDeleteAsync(id, userId);
    }

    @Override
    public void clearUserCaches(String userId) {
        cacheUtil.clearUserCaches(userId);
    }
}